package lerrain.service.underwriting;

import com.alibaba.fastjson.JSONObject;
import lerrain.service.common.Log;
import lerrain.service.common.ServiceTools;
import lerrain.tool.Common;
import lerrain.tool.formula.Factors;
import lerrain.tool.formula.Formula;
import lerrain.tool.formula.Function;
import lerrain.tool.formula.Value;
import lerrain.tool.script.Stack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

@Service
public class UnderwritingService
{
    @Autowired
    UnderwritingDao uwDao;

    @Autowired
    WriteQueue msgQueue;

    @Autowired
    ServiceTools tools;

    Map<String, Quest> map = new LinkedHashMap<>(); // 所有题库
    Map<String, ProductQuest> pqMap = new HashMap<>();    // 产品关联题库

    Map<Long, Underwriting> temp = new HashMap<>();

    @PostConstruct
    public void reset()
    {
        List<Quest> list = uwDao.listAll();
        for (Quest q : list)
            map.put(q.getCode(), q);

        List<ProductQuest> pqList = uwDao.listAllProductRela();
        for (ProductQuest pq : pqList) {
            pq.resetQuest(this.map);
            pqMap.put(pq.getProductId(), pq);
        }

        msgQueue.start();
    }

    public Quest getQuest(String code)
    {
        return map.get(code);
    }

    public Underwriting create(Map v)
    {
        Underwriting uw = new Underwriting();
        uw.setId(tools.nextId("underwriting"));
        uw.putAnswer(v);

        synchronized (temp)
        {
            temp.put(uw.getId(), uw);
        }

        return uw;
    }

    public Underwriting getUnderwriting(Long uwId)
    {
        synchronized (temp)
        {
            if (temp.containsKey(uwId))
                return temp.get(uwId);
        }

        Underwriting uw = uwDao.load(uwId, map);
        if (uw == null)
            throw new RuntimeException("underwriting: " + uwId + " not found");

        synchronized (temp)
        {
            temp.put(uw.getId(), uw);
        }

        return uw;
    }

    public List<Quest> list(Long uwId, int step, JSONObject val)
    {
        Underwriting uw = getUnderwriting(uwId);

        //check
        //uw.getResult(step - 1);

        List<Quest> r = new ArrayList<>();

        uw.putAnswer(val);

        Stack stack = new Stack(uw.getAnswer());
        stack.declare("R", new QuestResult(uw));

        ProductQuest pq = this.pqMap.get(uw.getProductId());
        if(pq == null || Common.isEmpty(pq.getQuests())) {
            throw new RuntimeException("underwriting: " + uwId + ", productId: "+uw.getProductId()+" not quests");
        }

        for (Quest q : pq.getQuests().values())
        {
            if (q.getType() == step)
            {
                if (q.getType() == Underwriting.STEP_DISEASE1)
                {
                    Boolean bool = boolOf(uw.getAnswer(q.getDisease()));
                    if (bool == null || !bool)
                        continue;
                }

                if (q.getCondition() == null)
                {
                    r.add(q);
                }
                else
                {
                    try
                    {
                        if (Value.booleanOf(q.getCondition(), stack))
                            r.add(q);
                    }
                    catch (Exception e)
                    {
                        Log.alert(e);
                    }

                    /*
                    Object res = q.getCondition().run(stack);
                    if (res instanceof Boolean)
                    {
                        if ((Boolean)res)
                            r.add(q);
                    }
                    else
                    {
                        char x = result(q, res);
                        if (x == 'C')
                            r.add(q);
                    }
                    */
                }
            }
        }

        synchronized (uw)
        {
            uw.setQuests(step, r);
        }

        msgQueue.add(uw);

        return r;
    }

    public char verify(Long uwId, int step, Map<String, Object> ans)
    {
        Underwriting uw = getUnderwriting(uwId);

        char r = Underwriting.RESULT_PASS;

        if (step == Underwriting.STEP_APPLY)
            r = Underwriting.RESULT_CONTINUE;

        synchronized (uw)
        {
            if (step == Underwriting.STEP_DISEASE)
            {
                Map<String, Object> vals = uw.getAnswer();

                ProductQuest pq = this.pqMap.get(uw.getProductId());
                if(pq == null || Common.isEmpty(pq.getQuests())) {
                    throw new RuntimeException("verify: " + uwId + ", productId: "+uw.getProductId()+" not quests");
                }

                for (Quest q : pq.getQuests().values())
                {
                    if (q.getType() >= Underwriting.STEP_DISEASE)
                        vals.remove(q.getCode());
                }
            }

            uw.putAnswer(ans);

            if (ans != null) for (Map.Entry<String, Object> e : ans.entrySet())
            {
                Quest q = map.get(e.getKey());
                int res = result(uw, q, Common.trimStringOf(e.getValue()));

                if (res == Underwriting.RESULT_FAIL || res == Underwriting.RESULT_NULL)
                {
                    r = Underwriting.RESULT_FAIL;
                    break;
                }

                if (res == Underwriting.RESULT_CONTINUE)
                {
                    r = Underwriting.RESULT_CONTINUE;
                }
            }

            uw.setResult(step, r);
        }

        msgQueue.add(uw);

        return r;
    }

    private Boolean boolOf(Object ans)
    {
        if (ans instanceof Boolean)
        {
            return (Boolean)ans;
        }
        else if (ans instanceof Number)
        {
            return ((Number)ans).intValue() != 0;
        }
        else if (ans instanceof String)
        {
            return "Y".equalsIgnoreCase((String)ans) || "1".equals(ans) ? true : "N".equalsIgnoreCase((String)ans) || "0".equals(ans) ? false : null;
        }

        return null;
    }

    private char result(Underwriting uw, Quest q, Object ans)
    {
        String next = q.getNext();

        if (q.getWidget() == Quest.WIDGET_SWITCH)
        {
            if (ans instanceof Boolean)
            {
                return next.charAt((Boolean)ans ? 0 : 1);
            }
            else if (ans instanceof Number)
            {
                return next.charAt(1 - ((Number)ans).intValue());
            }
            else if (ans instanceof String)
            {
                int index = "Y".equalsIgnoreCase((String)ans) || "O".equalsIgnoreCase((String)ans) || "1".equals(ans) || "2".equals(ans) ? 0 : "N".equalsIgnoreCase((String)ans) || "0".equals(ans) ? 1 : -1;
                if (index < 0)
                    throw new RuntimeException("Quest<" + q.getCode() + "> answer invalid");

                return next.charAt(index);
            }
        }
        else if (q.getWidget() == Quest.WIDGET_SELECT)
        {
            if (ans instanceof Number)
            {
                return next.charAt(((Number)ans).intValue() - 1);
            }
            else if (ans instanceof String)
            {
                char c = ((String)ans).charAt(0);
                int index = -1;

                if (c >= 'A' && c <= 'Z')
                    index = c - 'A';
                else if (c >= 'a' && c <= 'z')
                    index = c - 'a';
                else if (c >= '1' && c <= '9')
                    index = c - '1';

                if (index < 0)
                    throw new RuntimeException("Quest<" + q.getCode() + "> answer invalid");

                return next.charAt(index);
            }
        }
        else if (q.getWidget() == Quest.WIDGET_INPUT)
        {
            Stack s = new Stack(uw.getAnswer());
            s.set("self", ans);

            Formula f = (Formula)q.getAnswer();
            if (f != null)
            {
                Boolean b = boolOf(f.run(s));
                if (b == null)
                    throw new RuntimeException("Quest<" + q.getCode() + "> answer invalid");

                return next.charAt(b ? 0 : 1);
            }

            return Underwriting.RESULT_PASS;
        }

        return Underwriting.RESULT_NULL;
    }

    public class QuestResult implements Function
    {
        Underwriting uw;

        public QuestResult(Underwriting uw)
        {
            this.uw = uw;
        }

        @Override
        public Object run(Object[] objects, Factors factors)
        {
            String code = objects[0].toString();
            Quest q = map.get(code);
            Object res = factors.get(code);

            char c = result(uw, q, res);
            return c == 'C';
        }
    }
}
