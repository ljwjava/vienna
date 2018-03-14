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

	public Long save(Long id, String code, String name, Long companyId, int type)
	{
		if (id == null)
		{
			id = tools.nextId("product");
			jdbc.update("insert into t_product(id, code, name, company_id, type) value(?, ?, ?, ?, ?)", id, code, name, companyId, type);
		}
		else
		{
			jdbc.update("update t_product set code=?, name=?, company_id=?, type=? where id=?", code, name, companyId, type, id);
		}

		return id;
	}
}