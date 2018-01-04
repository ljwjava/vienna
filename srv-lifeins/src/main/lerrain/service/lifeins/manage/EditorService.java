package lerrain.service.lifeins.manage;

import lerrain.project.insurance.product.Company;
import lerrain.service.lifeins.LifeinsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Service
public class EditorService
{
    @Value("${path.lifeins}")
    String dataPath;

    @Autowired
    LifeinsService lifeSrv;

    Map<String, Company> allCompany;

    Map<String, InsXml> map = new HashMap<>();

    public void reset()
    {
        allCompany = lifeSrv.getAllCompany();
        scan(new File(dataPath));
    }

    public void scan(File dir)
    {
        File[] fs = dir.listFiles();
        if (fs != null) for (File f : fs)
        {
            if (f.isDirectory())
            {
                scan(f);
            }
            else if (f.getName().toLowerCase().endsWith(".xml"))
            {
                try
                {
                    InsXml ins = new InsXml(f, allCompany);
                    if (ins.isInsurance())
                    {
                        map.put(ins.getId(), ins);
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    public InsXml load(String id)
    {
        return map.get(id);
    }
}
