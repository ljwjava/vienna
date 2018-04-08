package lerrain.service.underwriting;

import com.alibaba.fastjson.JSONObject;
import lerrain.service.common.ServiceTools;
import lerrain.tool.Common;
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
    ServiceTools tools;

    Map<String, Quest> map = new LinkedHashMap<>();

    Map<Long, Underwriting> temp = new HashMap<>();

    @PostConstruct
    public void reset()
    {
        List<Quest> list = uwDao.listAll();

        for (Quest q : list)
            map.put(q.getCode(), q);
    }

    public Underwriting create()
    {
        Underwriting uw = new Underwriting();
        uw.setId(tools.nextId("underwriting"));

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
            return temp.get(uwId);
        }
    }

    public List<Quest> find(Long uwId, int step, JSONObject val)
    {
        Underwriting uw = getUnderwriting(uwId);

        //check
        //uw.getResult(step - 1);

        List<Quest> r = new ArrayList<>();

        Stack stack = new Stack(val);
        for (Quest q : map.values())
        {
            if (q.getType() == step)
            {
                if (q.getCondition() == null)
                {
                    r.add(q);
                }
                else
                {
                    if (Value.booleanOf(q.getCondition(), stack))
                        r.add(q);
                }
            }
        }

        uw.setQuestList(step, r);

        return r;
    }

    public char verify(Long uwId, int step, Map<String, Object> ans)
    {
        Underwriting uw = getUnderwriting(uwId);

        char r = Underwriting.RESULT_PASS;

        if (step == Underwriting.STEP_APPLY)
            r = Underwriting.RESULT_CONTINUE;

        for (Map.Entry<String, Object> e : ans.entrySet())
        {
            Quest q = map.get(e.getKey());
            int res = result(q, Common.trimStringOf(e.getValue()));

            if (res == Underwriting.RESULT_FAIL || res == Underwriting.RESULT_NULL)
                return Underwriting.RESULT_FAIL;
            else if (res == Underwriting.RESULT_CONTINUE)
                r = Underwriting.RESULT_CONTINUE;
        }

        uw.setAnswer(step, ans, r);

        return r;
    }

    private char result(Quest q, Object ans)
    {
        String next = q.getNext();

        if (q.getWidget() == Quest.WIDGET_SWITCH)
        {
            if (ans instanceof Boolean)
            {
                return next.charAt((Boolean)ans ? 0 : 1);
            }
            else if (ans instanceof String)
            {
                int index = "Y".equalsIgnoreCase((String)ans) ? 0 : "N".equalsIgnoreCase((String)ans) ? 1 : -1;
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

                if (index < 0)
                    throw new RuntimeException("Quest<" + q.getCode() + "> answer invalid");

                return next.charAt(index);
            }
        }
        else if (q.getWidget() == Quest.WIDGET_INPUT)
        {
            Stack s = new Stack();
            s.set("self", ans);

            return Underwriting.RESULT_PASS;
        }


        return Underwriting.RESULT_NULL;
    }
}
