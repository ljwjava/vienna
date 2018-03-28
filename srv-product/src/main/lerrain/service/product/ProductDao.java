package lerrain.service.product;

import lerrain.service.common.ServiceTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ProductDao
{
	@Autowired JdbcTemplate jdbc;
	@Autowired ServiceTools tools;

	public Long save(Long id, String code, String name, Long companyId, int type, Long categoryId)
	{
		if (id == null)
		{
			id = tools.nextId("product");
			jdbc.update("insert into t_product(id, code, name, company_id, type, category_id) value(?, ?, ?, ?, ?, ?)", id, code, name, companyId, type, categoryId);
		}
		else
		{
			jdbc.update("update t_product set code=?, name=?, company_id=?, type=?, category_id=? where id=?", code, name, companyId, type, categoryId, id);
		}

		return id;
	}
}