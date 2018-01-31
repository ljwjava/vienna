package lerrain.service.fee;

import com.alibaba.fastjson.JSON;
import lerrain.service.fee.function.FindFee;
import lerrain.service.fee.function.IYunBao;
import lerrain.service.fee.function.JsonOf;
import lerrain.service.fee.function.Store;
import lerrain.tool.formula.Formula;
import lerrain.tool.formula.Value;
import lerrain.tool.script.Stack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class PlatformFee
{
    @Autowired
    IYunBao iyb;

    @Autowired
    FindFee findFee;

    @Autowired
    Store store;

    Stack root;

    Map<Long, Stack> stacks = new HashMap<>();

    Map<Long, List<FeeAction>> map = new HashMap<>();

    public void reset()
    {
        root = new Stack();
        root.declare("jsonOf", new JsonOf());
        root.declare("findFee", findFee);
        root.declare("store", store);
        root.declare("IYB", iyb);

        map.clear();
    }

    public void setVals(Long platformId, Map vals)
    {
        Stack stack = new Stack(root);
        stack.declare("PLATFORM_ID", platformId);
        if (vals != null)
            stack.setAll(vals);

        stacks.put(platformId, stack);
    }

    public void add(Long platformId, Date time, Formula pay, Formula calc, Formula charge)
    {
        List<FeeAction> list = map.get(platformId);
        if (list == null)
        {
            list = new ArrayList<>();
            map.put(platformId, list);
        }

        list.add(actionOf(time, pay, calc, charge));
        map.put(platformId, list);
    }

    public boolean pay(Fee fee, Date time)
    {
        List<FeeAction> list = map.get(fee.getPlatformId());

        if (list != null) for (FeeAction feeAction : list)
        {
            if (feeAction.time.before(time))
            {
                Stack stack = new Stack(stacks.get(fee.getPlatformId()));
                stack.declare("self", fee);

                return Value.booleanOf(feeAction.pay, stack);
            }
        }

        return false;
    }

    public boolean charge(Long platformId, Date time, Object biz)
    {
        List<FeeAction> list = map.get(platformId);

        if (list != null) for (FeeAction feeAction : list)
        {
            if (feeAction.time.before(time))
            {
                Stack stack = new Stack(stacks.get(platformId));
                stack.declare("self", biz);

                feeAction.charge.run(stack);
                return true;
            }
        }

        return false;
    }

    public Object calc(Long platformId, Date time, Object biz)
    {
        List<FeeAction> list = map.get(platformId);

        if (list != null) for (FeeAction feeAction : list)
        {
            if (feeAction.time.before(time))
            {
                Stack stack = new Stack(stacks.get(platformId));
                stack.declare("self", biz);

                return feeAction.calc.run(stack);
            }
        }

        return null;
    }

    public static FeeAction actionOf(Date time, Formula pay, Formula calc, Formula charge)
    {
        FeeAction feeAction = new FeeAction();

        feeAction.time = time;
        feeAction.pay = pay;
        feeAction.calc = calc;
        feeAction.charge = charge;

        return feeAction;
    }

    private static class FeeAction
    {
        Date time;
        Formula pay, calc, charge;
    }
}
