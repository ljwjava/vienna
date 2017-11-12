package lerrain.service.sale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WareService
{
    @Autowired
    WareDao cd;

    @Autowired
    PlatformBizService cs;

    Map<Object, Ware> map = new HashMap<>();

    Map<String, List<Ware>> temp = new HashMap<>();

    public void reset()
    {
        temp.clear();
        map.clear();

        List<Ware> list = cd.loadAll();

        for (Ware c : list)
        {
            map.put(c.getId(), c);
            map.put(c.getCode(), c);
        }
    }

    public Ware getWare(Long id)
    {
        return map.get(id);
    }

    public Ware getWare(String code)
    {
        return map.get(code);
    }

    public List<Ware> find(Long platformId, String tag)
    {
        if (platformId == null)
            return null;

        String key = platformId + "/" + tag;

        if (temp.containsKey(key))
            return temp.get(key);

        List<Ware> r = new ArrayList<>();

        for (Long id : cd.find(platformId))
        {
            Ware c = map.get(id);
            if (c != null && c.match(tag))
                r.add(c);
        }

        temp.put(tag, r);

        return r;
    }
}
