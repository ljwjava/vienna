package lerrain.service.shop;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lerrain.service.common.ServiceTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class ShopDao
{
	@Autowired JdbcTemplate jdbc;
	@Autowired ServiceTools tools;

	public int count(String search)
	{
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT count(*)");
		sql.append(" FROM");
		sql.append(" `t_cs_commodity_plan` p");
		sql.append(" INNER JOIN t_cs_commodity c ON p.rel_commodity_id = c.id");
		sql.append(" INNER JOIN t_cs_commodity_type t ON p.rel_type_code = t.`code`");
		sql.append(" INNER JOIN t_cs_commodity_share s ON s.rel_commodity_id = p.rel_commodity_id");
		sql.append(" LEFT JOIN t_cs_commodity_market m ON m.rel_id = p.rel_commodity_id AND m.rel_type = 'commodity'");
		sql.append(" LEFT JOIN t_cs_commodity_label_relation l ON p.rel_commodity_id = l.commodity_id AND p.rel_type_code = l.rel_type_code AND p.rel_org_id = l.org_id");
		sql.append(" WHERE");
		sql.append(" p.rel_org_id = 1");
		sql.append(" AND c.online_state = 1");
		sql.append(" AND t.online_state = 1");
		sql.append(" /*AND c.`name` LIKE '%尊享e生%'*/");
		sql.append(" /*AND p.rel_type_code = 'health'*/");

		return jdbc.queryForObject(sql.toString(), Integer.class);
	}

	public List<JSONObject> types(String search, int from, int number){
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT ");
        sql.append(" tt.tagId,");
		sql.append(" tt.tagCode,");
		sql.append(" tt.tagName,");
		sql.append(" COUNT(tt.cdCode) as prdNumbers");
		sql.append(" FROM");
		sql.append(" (SELECT");
        sql.append(" t.id AS tagId,");
		sql.append(" t.`code` AS tagCode,");
		sql.append(" t.`name` AS tagName,");
		sql.append(" c.`code` AS cdCode");
		sql.append(" FROM");
		sql.append(" `t_cs_commodity_plan` p");
		sql.append(" INNER JOIN t_cs_commodity c ON p.rel_commodity_id = c.id");
		sql.append(" INNER JOIN t_cs_commodity_type t ON p.rel_type_code = t.`code`");
		sql.append(" INNER JOIN t_cs_commodity_share s ON s.rel_commodity_id = p.rel_commodity_id");
		sql.append(" WHERE");
		sql.append(" p.rel_org_id = 1");
		sql.append(" AND c.online_state = 1");
		sql.append(" AND t.online_state = 1");
        if (search != null && !"".equals(search)) {
            sql.append(" AND c.`name` LIKE '%"+ search +"%'");
        }
		sql.append(" limit ?, ?");
		sql.append(" ) tt");
		sql.append(" GROUP BY tt.tagCode");

		return jdbc.query(sql.toString(), new Object[] {from, number}, new RowMapper<JSONObject>()
		{
			@Override
			public JSONObject mapRow(ResultSet m, int arg1) throws SQLException
			{
				JSONObject j = new JSONObject();
				j.put("tagId",m.getString("tagId"));
				j.put("tagCode",m.getString("tagCode"));
				j.put("tagName", m.getString("tagName"));
				j.put("prdNumbers",m.getString("prdNumbers"));
				return j;
			}
		});
	}

	public List<Shop> commoditys(String search, int from, int number)
	{
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT ");
		sql.append(" p.rel_org_id AS orgId,");
		sql.append(" /*companyId*/");
		sql.append(" t.`code` AS commodityTypeCode,");
		sql.append(" c.id AS commodityId,");
		sql.append(" c.`code` AS commodityCode,");
		sql.append(" c.`name` AS commodityName,");
		sql.append(" c.`desc` AS commodityDesc,");
		sql.append(" c.mini_premium AS priceDesc,");
		sql.append(" c.premium_des AS priceDescText,");
		sql.append(" c.home_image AS homeImage,");
		sql.append(" s.share_logo AS shareImage,");
		sql.append(" c.sale_limit AS saleLimit,");
		sql.append(" s.share_title AS shareTitle,");
		sql.append(" s.share_desc AS shareDesc,");
		sql.append(" m.`code` AS marketType,");
		sql.append(" m.icon AS marketIconUrl,");
		sql.append(" c.helper_url AS helperUrl,");
		sql.append(" c.commission_url AS commissionUrl,");
		sql.append(" /*佣金比例*/");
		sql.append(" /*佣金提示信息*/");
		sql.append(" c.online_state AS onlineStatus,");
		sql.append(" c.link_url AS commodityLink,");
		sql.append(" c.rel_supplier_code AS supplierCode,");
		sql.append(" c.extra_info AS extraInfo,");
        sql.append(" c.creator,");
        sql.append(" c.gmt_created AS gmtCreated,");
        sql.append(" c.modifier,");
        sql.append(" c.gmt_modified AS gmtModified,");
        sql.append(" c.is_deleted AS isDeleted,");
		sql.append(" c.sort");
		sql.append(" FROM");
		sql.append(" `t_cs_commodity_plan` p");
		sql.append(" INNER JOIN t_cs_commodity c ON p.rel_commodity_id = c.id");
		sql.append(" INNER JOIN t_cs_commodity_type t ON p.rel_type_code = t.`code`");
		sql.append(" INNER JOIN t_cs_commodity_share s ON s.rel_commodity_id = p.rel_commodity_id");
		sql.append(" LEFT JOIN t_cs_commodity_market m ON m.rel_id = p.rel_commodity_id AND m.rel_type = 'commodity'");
		sql.append(" LEFT JOIN t_cs_commodity_label_relation l ON p.rel_commodity_id = l.commodity_id AND p.rel_type_code = l.rel_type_code AND p.rel_org_id = l.org_id");
		sql.append(" WHERE");
		sql.append(" p.rel_org_id = 1");
		sql.append(" AND c.online_state = 1");
		sql.append(" AND t.online_state = 1");
		if (search != null && null != search) {
			sql.append(" AND c.`name` LIKE '%"+search+"%'");
		}
		sql.append(" /*AND l.id IN (1, 2)*/");
		sql.append(" limit ?, ?");
//		sql.append("LIMIT 0, 12;");

		return jdbc.query(sql.toString(), new Object[] {from, number}, new RowMapper<Shop>()
		{
			@Override
			public Shop mapRow(ResultSet m, int arg1) throws SQLException
			{
				Shop p = new Shop();
				p.setOrgId(m.getLong("orgId"));
				p.setCommodityTypeCode(m.getString("commodityTypeCode"));
				p.setCommodityId(m.getLong("commodityId"));
				p.setCommodityCode(m.getString("commodityCode"));
				p.setCommodityName(m.getString("commodityName"));
				p.setCommodityDesc(m.getString("commodityDesc"));
				p.setPriceDesc(m.getString("priceDesc"));
				p.setPriceDescText(m.getString("priceDescText"));
				p.setHomeImage(m.getString("homeImage"));
				p.setShareImage(m.getString("shareImage"));
				p.setSaleLimit(m.getString("saleLimit"));
				p.setShareTitle(m.getString("shareTitle"));
				p.setShareDesc(m.getString("shareDesc"));
				p.setMarketType(m.getString("marketType"));
				p.setMarketIconUrl(m.getString("marketIconUrl"));
				p.setHelperUrl(m.getString("helperUrl"));
				p.setCommissionUrl(m.getString("commissionUrl"));
				p.setOnlineStatus(m.getString("onlineStatus"));
				p.setCommodityLink(m.getString("commodityLink"));
				p.setSupplierCode(m.getString("supplierCode"));
				p.setExtraInfo(m.getString("extraInfo"));
				p.setSort(m.getString("sort"));
				return p;
			}
		});
	}
}