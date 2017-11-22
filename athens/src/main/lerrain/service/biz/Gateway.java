package lerrain.service.biz;

import lerrain.service.common.Log;
import lerrain.tool.script.Script;

/**
 * Created by lerrain on 2017/11/13.
 */
public class Gateway
{
    public static final int TYPE_JSON               = 1;
    public static final int TYPE_HTML               = 2;
    public static final int TYPE_ACTION             = 3;

    public static final int SUPPORT_GET             = 1;
    public static final int SUPPORT_POST            = 2;

    public static final int FORWARD_NULL            = 0;
    public static final int FORWARD_MICRO_SERVICE   = 1;
    public static final int FORWARD_REDIRECT_LOCAL  = 2;
    public static final int FORWARD_REDIRECT_REMOTE = 3;

    Long id;
    Long envId;

    int type;
    int support;
    int forward;
    int seq = 1000000;

    boolean login;

    String uri;
    String uriX;
    String forwardTo;

    String[] with;

    Script script;

    public boolean match(String uri)
    {
        if (this.uriX != null)
            return uri.startsWith(this.uri) && uri.endsWith(this.uriX);

        return this.uri.equals(uri);
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public boolean isSupport(int support)
    {
        return (type & support) > 0;
    }

    public Long getEnvId()
    {
        return envId;
    }

    public void setEnvId(Long envId)
    {
        this.envId = envId;
    }

    public boolean isLogin()
    {
        return login;
    }

    public void setLogin(boolean login)
    {
        this.login = login;
    }

    public Script getScript()
    {
        return script;
    }

    public void setScript(Script script)
    {
        this.script = script;
    }

    public int getSeq()
    {
        return seq;
    }

    public void setSeq(int seq)
    {
        this.seq = seq;
    }

    public int getType()
    {
        return type;
    }

    public void setType(int type)
    {
        this.type = type;
    }

    public String getUri()
    {
        return uri;
    }

    public void setUri(String uri)
    {
        int p = uri.indexOf("*");
        if (p >= 0)
        {
            this.uri = uri.substring(0, p);
            this.uriX = uri.substring(p + 1);
        }
        else
        {
            this.uri = uri;
        }
    }

    public String[] getWith()
    {
        return with;
    }

    public void setWith(String[] with)
    {
        this.with = with;
    }

    public int getForward()
    {
        return forward;
    }

    public void setForward(int forward)
    {
        this.forward = forward;
    }

    public String getForwardTo(String from)
    {
        if (this.uriX == null)
            return forwardTo == null ? from : forwardTo;

        if (forwardTo == null)
            return this.uri + from.substring(this.uri.length(), from.length() - this.uriX.length()) + this.uriX;

        return forwardTo.replace("*", from.substring(this.uri.length(), from.length() - this.uriX.length()));
    }

    public void setForwardTo(String forwardTo)
    {
        this.forwardTo = forwardTo;
    }

    public int getSupport()
    {
        return support;
    }

    public void setSupport(int support)
    {
        this.support = support;
    }
}
