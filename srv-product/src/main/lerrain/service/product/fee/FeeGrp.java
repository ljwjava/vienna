package lerrain.service.product.fee;

import java.util.*;

public class FeeGrp
{
    Map<String, Grp> map = new HashMap<>();

    public void addKey(String pre, Object[] keys, FeeDefine fee)
    {
         Grp grp = map.get(pre);
         if (grp == null)
         {
             grp = new Grp();
             map.put(pre, grp);
         }

         grp.add(keys, fee);
    }

    public List<FeeDefine> find(String pre, Object[] keys, Date time)
    {
        Grp grp = map.get(pre);
        return grp == null ? null : grp.find(keys, time);
    }

    private class Grp
    {
        List<Object[]> vals = new ArrayList<>();

        public List<FeeDefine> find(Object[] keys, Date time)
        {
            List<FeeDefine> r = new ArrayList<>();

            for (Object[] val : vals)
            {
                boolean match = true;

                Object[] v = (Object[])val[0];
                int len = Math.min(v.length, keys.length);

                for (int i = 0; i < len; i++)
                {
                    if (v[i] != null && (keys[i] == null || !v[i].equals(keys[i].toString())))
                    {
                        match = false;
                        break;
                    }
                }

                if (match)
                {
                    FeeDefine fee = (FeeDefine)val[1];
                    if (fee.match(time))
                        r.add(fee);
                }
            }

            return r;
        }

        public void add(Object[] keys, FeeDefine fee)
        {
            vals.add(new Object[] {keys, fee});
        }
    }
}
