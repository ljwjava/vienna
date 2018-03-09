package lerrain.service.policy;

import com.alibaba.fastjson.JSONObject;

import java.util.Date;
import java.util.List;

public class PolicyClause
{
    Long id;

    String clauseId;
    String clauseCode;
    String clauseName;

    Date effectiveTime;
    Date finishTime;

    Double premium;

    String pay;
    String insure;
    String purchase;

    Double quantity;
    Double amount;
    String rank;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getClauseId()
    {
        return clauseId;
    }

    public void setClauseId(String clauseId)
    {
        this.clauseId = clauseId;
    }

    public String getClauseCode()
    {
        return clauseCode;
    }

    public void setClauseCode(String clauseCode)
    {
        this.clauseCode = clauseCode;
    }

    public String getClauseName()
    {
        return clauseName;
    }

    public void setClauseName(String clauseName)
    {
        this.clauseName = clauseName;
    }

    public Date getEffectiveTime()
    {
        return effectiveTime;
    }

    public void setEffectiveTime(Date effectiveTime)
    {
        this.effectiveTime = effectiveTime;
    }

    public Date getFinishTime()
    {
        return finishTime;
    }

    public void setFinishTime(Date finishTime)
    {
        this.finishTime = finishTime;
    }

    public Double getPremium()
    {
        return premium;
    }

    public void setPremium(Double premium)
    {
        this.premium = premium;
    }

    public String getPurchase()
    {
        return purchase;
    }

    public void setPurchase(String purchase)
    {
        this.purchase = purchase;
    }

    public Double getQuantity()
    {
        return quantity;
    }

    public void setQuantity(Double quantity)
    {
        this.quantity = quantity;
    }

    public Double getAmount()
    {
        return amount;
    }

    public void setAmount(Double amount)
    {
        this.amount = amount;
    }

    public String getRank()
    {
        return rank;
    }

    public void setRank(String rank)
    {
        this.rank = rank;
    }

    public String getInsure()
    {
        return insure;
    }

    public void setInsure(String insure)
    {
        this.insure = insure;
    }

    public String getPay()
    {
        return pay;
    }

    public void setPay(String pay)
    {
        this.pay = pay;
    }
}
