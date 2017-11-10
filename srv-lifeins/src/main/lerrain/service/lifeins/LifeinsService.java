package lerrain.service.lifeins;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;

import lerrain.project.insurance.product.Company;
import lerrain.project.insurance.product.Config;
import lerrain.project.insurance.product.Insurance;
import lerrain.project.insurance.product.InsuranceMgr;

import lerrain.service.lifeins.format.FGraphFilter;
import lerrain.service.lifeins.format.FGraphParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service("lifeins")
public class LifeinsService
{
    private Map<String, Company> company;

    private Map<String, Insurance> product;

    public static final String[] INSURANCE_PATH = {"./product/", "X:/files/lifeins/"};

    @Value("${path.lifeins}")
    String dataPath;

    @PostConstruct
    public void reset()
    {
//        Config.addFilter("fgraph", new FGraphFilter());
        Config.addParser("fgraph", new FGraphParser());

        try
        {
            String PATH = null;

            if (dataPath != null && new File(dataPath).isDirectory())
            {
                PATH = dataPath;
            }
            else for (String path : INSURANCE_PATH)
            {
                if (new File(path).isDirectory())
                {
                    PATH = path;
                    break;
                }
            }

            product = new HashMap<>();

            System.out.println("CLAUSE PATH: " + PATH);

            InsuranceMgr ins = InsuranceMgr.managerOf(PATH, "insurance.xml");
            company = ins.load();

            for (Entry<String, Company> e : company.entrySet())
            {
                if (e.getValue().getProductList() != null)
                {
                    for (Insurance prd : (List<Insurance>) e.getValue().getProductList())
                    {
                        System.out.println(String.format("CLAUSE LOAD: <%s>%s", prd.getId(), prd.getName()));

                        product.put(prd.getId(), prd);
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
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
