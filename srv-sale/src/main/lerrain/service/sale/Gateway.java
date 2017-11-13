package lerrain.service.sale;

import lerrain.tool.script.Script;

/**
 * Created by lerrain on 2017/11/13.
 */
public class Gateway
{
    public static final int TYPE_JSON    = 1;
    public static final int TYPE_HTML    = 2;
    public static final int TYPE_ACTION  = 3;

    public static final int SUPPORT_GET  = 1;
    public static final int SUPPORT_POST = 2;

    int type;
    int support;
    int seq = 1000000;

    boolean login;

    String uri;
    String redirect;

    String[] with;

    Script script;

    public boolean match(String uri)
    {
        return uri.equals(uri);
    }

    public boolean isSupport(int support)
    {
        return (type & support) > 0;
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
        this.uri = uri;
    }

    public String[] getWith()
    {
        return with;
    }

    public void setWith(String[] with)
    {
        this.with = with;
    }

    public String getRedirect()
    {
        return redirect;
    }

    public void setRedirect(String redirect)
    {
        this.redirect = redirect;
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
