package lerrain.service.shop;

import lerrain.service.common.ServiceTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ShopDao
{
	@Autowired JdbcTemplate jdbc;
	@Autowired ServiceTools tools;
}