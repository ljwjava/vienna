package lerrain.service.biz;

import lerrain.tool.formula.Factors;
import lerrain.tool.formula.Function;
import lerrain.tool.script.Stack;

import javax.servlet.http.HttpSession;

/**
 * Created by lerrain on 2017/11/13.
 */
public class SessionAdapter extends Stack
{
    HttpSession session;

    public SessionAdapter(HttpSession session)
    {
        this.session = session;
    }

    @Override
    public Object get(String s)
    {
        if ("free".equals(s))
        {
            return new Function()
            {
                @Override
                public Object run(Object[] objects, Factors factors)
                {
                    session.invalidate();
                    return null;
                }
            };
        }

        return session.getAttribute(s);
    }

    @Override
    public void set(String name, Object value)
    {
        session.setAttribute(name, value);
    }
}
