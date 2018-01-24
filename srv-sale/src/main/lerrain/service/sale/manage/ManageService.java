package lerrain.service.sale.manage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ManageService
{
    @Autowired ManageDao manageDao;

    public List<String[]> getCompanyList()
    {
        return manageDao.loadCompanyList();
    }

    public Product loadProduct(Long productId)
    {
        return manageDao.loadProduct(productId);
    }

    public int count(String search)
    {
        return manageDao.count(search);
    }

    public List<Product> list(String search)
    {
        return list(search, 0, -1);
    }

    public List<Product> list(String search, int from, int number)
    {
        return manageDao.list(search, from, number < 0 ? 999 : number);
    }
}
