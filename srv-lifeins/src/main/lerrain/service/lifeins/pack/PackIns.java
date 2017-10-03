package lerrain.service.lifeins.pack;

import lerrain.tool.formula.Formula;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lerrain on 2017/4/26.
 */
public class PackIns
{
    Long id;

    String code;
    String name;

    List<InputField> inputForm;

    String planId;

    Formula pretreat;
    Formula perform;

    Map<String, Formula> opts;

    Map<String, Object> vendor;

    Map<String, Object> docs;

//    Map<String, List<SummaryBlock>> summary;

    public Formula getPretreat()
    {
        return pretreat;
    }

    public void setPretreat(Formula pretreat)
    {
        this.pretreat = pretreat;
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

    public String getPlanId()
    {
        return planId;
    }

    public void setPlanId(String planId)
    {
        this.planId = planId;
    }

    public Map<String, Object> getDocs()
    {
        return docs;
    }

    public void setDocs(Map<String, Object> docs)
    {
        this.docs = docs;
    }

//    public Map<String, List<SummaryBlock>> getSummary()
//    {
//        return summary;
//    }
//
//    public void setSummary(Map<String, List<SummaryBlock>> summary)
//    {
//        this.summary = summary;
//    }

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

    public Formula getPerform()
    {
        return perform;
    }

    public void setPerform(Formula perform)
    {
        this.perform = perform;
    }

    public Map<String, Object> getVendor()
    {
        return vendor;
    }

    public void setVendor(Map<String, Object> vendor)
    {
        this.vendor = vendor;
    }

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }
}
