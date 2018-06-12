package lerrain.service.sale;

import lerrain.tool.formula.Formula;
import lerrain.tool.script.Stack;

import java.util.List;
import java.util.Map;

/**
 * Created by lerrain on 2017/4/26.
 */
public class PackIns
{
    public static final int PRICE_FIXED     = 1;
    public static final int PRICE_FACTORS   = 2;
    public static final int PRICE_PLAN      = 3;
    public static final int PRICE_LIFE      = 4;
    public static final int PRICE_PRODUCT   = 5;
    public static final int PRICE_OTHER     = 9;

    Long id;
    Ware ware;

    String code;
    String name;

    int type;
    int applyMode;
    int priceType;

    Object price;
    Object show;

    boolean dynamicForm = false;

    List<InputField> inputForm;

    Stack stack;

    Map<String, Formula> opts;

    Map<String, Object> extra;
    Map<String, Object> vendor;
    Map<String, Object> formOpt;
    Map<String, Object> opClassify;

    public PackIns clone() {
        // TODO: 待处理
        return this;
    }

    public int getType()
    {
        return type;
    }

    public void setType(int type)
    {
        this.type = type;
    }

    public Map<String, Object> getExtra()
    {
        return extra;
    }

    public void setExtra(Map<String, Object> extra)
    {
        this.extra = extra;
    }

    public Ware getWare()
    {
        return ware;
    }

    public void setWare(Ware ware)
    {
        this.ware = ware;
    }

    public Stack getStack()
    {
        return stack;
    }

    public void setStack(Stack stack)
    {
        this.stack = stack;
    }

    public int getPriceType()
    {
        return priceType;
    }

    public void setPriceType(int priceType)
    {
        this.priceType = priceType;
    }

    public Object getPrice()
    {
        return price;
    }

    public void setPrice(Object price)
    {
        this.price = price;
    }

    public Map<String, Object> getFormOpt()
    {
        return formOpt;
    }

    public void setFormOpt(Map<String, Object> formOpt)
    {
        this.formOpt = formOpt;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Map<String, Formula> getOpts()
    {
        return opts;
    }

    public void setOpts(Map<String, Formula> opts)
    {
        this.opts = opts;
    }

    public int getApplyMode()
    {
        return applyMode;
    }

    public void setApplyMode(int applyMode)
    {
        this.applyMode = applyMode;
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public List<InputField> getInputForm()
    {
        return inputForm;
    }

    public void setInputForm(List<InputField> inputForm)
    {
        this.inputForm = inputForm;
    }

    public Map<String, Object> getVendor()
    {
        return vendor;
    }

    public void setVendor(Map<String, Object> vendor)
    {
        this.vendor = vendor;
    }

    public Object getShow()
    {
        return show;
    }

    public void setShow(Object show)
    {
        this.show = show;
    }

    public boolean isDynamicForm()
    {
        return dynamicForm;
    }

    public void setDynamicForm(boolean dynamicForm)
    {
        this.dynamicForm = dynamicForm;
    }

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public Map<String, Object> getOpClassify() {
        return opClassify;
    }

    public void setOpClassify(Map<String, Object> opClassify) {
        this.opClassify = opClassify;
    }
}
