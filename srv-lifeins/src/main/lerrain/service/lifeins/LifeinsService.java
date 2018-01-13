package lerrain.service.lifeins;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import lerrain.project.insurance.product.Company;
import lerrain.project.insurance.product.Config;
import lerrain.project.insurance.product.Insurance;
import lerrain.project.insurance.product.InsuranceMgr;

import lerrain.service.common.Log;
import lerrain.service.lifeins.format.FGraphParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service("lifeins")
public class LifeinsService
{
    @Autowired
    LifeinsDao lifeinsDao;

    private Map<String, Company> company;

    private Map<String, Insurance> product;

    @Value("${path.lifeins}")
    String dataPath;

    public void reset()
    {
//        Config.addFilter("fgraph", new FGraphFilter());
        Config.addParser("fgraph", new FGraphParser());

        try
        {
            product = new HashMap<>();

            System.out.println("CLAUSE PATH: " + dataPath);

            InsuranceMgr ins = InsuranceMgr.managerOf(dataPath, "insurance.xml");
            company = ins.load();

            lifeinsDao.loadPacks();
            lifeinsDao.supplyClauses();

            for (Entry<String, Company> e : company.entrySet())
            {
                if (e.getValue().getProductList() != null)
                {
                    for (Insurance prd : (List<Insurance>) e.getValue().getProductList())
                        addProduct(prd);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void addProduct(Insurance ins)
    {
        Log.debug("load <%s>%s", ins.getId(), ins.getName());
        product.put(ins.getId(), ins);
    }

    public Map<String, Company> getAllCompany()
    {
        return company;
    }

    public Company getCompany(String vendor)
    {
        if (vendor == null)
            return company.get("nci");

        return company.get(vendor);
    }

    public Insurance getProduct(String productId)
    {
        return product.get(productId);
    }
}
