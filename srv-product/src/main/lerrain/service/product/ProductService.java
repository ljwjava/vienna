package lerrain.service.product;

import lerrain.tool.Common;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductService
{
	@Autowired
	ProductDao productDao;

	public void reset()
	{
	}

	public Long save(Long id, String code, String name, Long companyId, int type, Long categoryId)
	{
		if (companyId == null)
			throw new RuntimeException("companyId不能为空");
		if (Common.isEmpty(name))
			throw new RuntimeException("name不能为空");
		if (Common.isEmpty(code))
			code = null;

		return productDao.save(id, code, name, companyId, type, categoryId);
	}
}
