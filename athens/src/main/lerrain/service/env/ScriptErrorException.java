package lerrain.service.env;

import lerrain.tool.formula.Factors;

public class ScriptErrorException extends RuntimeException
{
    Factors factors;

    String referNo;

    public ScriptErrorException(Factors factors, String msg, String referNo)
    {
        super(msg);

        this.factors = factors;
        this.referNo = referNo;
    }

    public String getReferNo()
    {
        return referNo;
    }

    public void setReferNo(String referNo)
    {
        this.referNo = referNo;
    }

    public Factors getFactors()
    {
        return factors;
    }

    public void setFactors(Factors factors)
    {
        this.factors = factors;
    }
}
