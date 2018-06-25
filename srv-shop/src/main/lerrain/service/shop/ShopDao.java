package lerrain.service.shop;

import com.alibaba.fastjson.JSONObject;
import lerrain.service.common.ServiceTools;
import lerrain.tool.Common;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Repository
public class ShopDao
{
	@Autowired JdbcTemplate jdbc;
	@Autowired ServiceTools tools;

	public int count(Long userId, String name, String type)
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
		sql.append(" WHERE 1=1");
		sql.append(" AND p.rel_org_id = 1");
		sql.append(" AND c.online_state = 1");
		sql.append(" AND t.online_state = 1");
		if (null != userId) {
			sql.append(" AND p.rel_org_id = "+userId);
		}
		if (StringUtils.isNotBlank(type)) {
			sql.append(" AND t.`code` = '"+type+"'");
		}
		if (StringUtils.isNotBlank(name)) {
			sql.append(" AND c.`name` LIKE '%"+name+"%'");
		}

		return jdbc.queryForObject(sql.toString(), Integer.class);
	}

	public int countType(String search)
	{
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT ");
		sql.append(" count(t.`code`)");
		sql.append("FROM");
		sql.append("`t_cs_commodity_plan` p");
		sql.append(" INNER JOIN t_cs_commodity c ON p.rel_commodity_id = c.id");
		sql.append(" INNER JOIN t_cs_commodity_type t ON p.rel_type_code = t.`code`");
		sql.append(" INNER JOIN t_cs_commodity_share s ON s.rel_commodity_id = p.rel_commodity_id");
		sql.append(" WHERE 1=1");
		sql.append(" AND p.rel_org_id = 1");
		sql.append(" AND c.online_state = 1");
		sql.append(" AND t.online_state = 1");
		if (StringUtils.isNotBlank(search)) {
			sql.append(" AND c.`name` LIKE '%"+ search +"%'");
		}

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
		sql.append(" WHERE 1=1");
		sql.append(" AND p.rel_org_id = 1");
		sql.append(" AND c.online_state = 1");
		sql.append(" AND t.online_state = 1");
        if (StringUtils.isNotBlank(search)) {
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

	public List<Shop> commoditys(Long userId, String name, String type, int from, int number)
	{
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT ");
		sql.append(" p.rel_org_id AS orgId,");
		sql.append(" /*companyId*/");
		sql.append(" t.`code` AS commodityTypeCode,");
		sql.append(" t.`name` AS commodityTypeName,");
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
        sql.append(" DATE_FORMAT(c.gmt_created,'%Y-%m-%d %T') AS gmtCreated,");
        sql.append(" c.modifier,");
        sql.append(" DATE_FORMAT(c.gmt_modified,'%Y-%m-%d %T') AS gmtModified,");
        sql.append(" c.is_deleted AS isDeleted,");
		sql.append(" c.sort");
		sql.append(" FROM");
		sql.append(" `t_cs_commodity_plan` p");
		sql.append(" INNER JOIN t_cs_commodity c ON p.rel_commodity_id = c.id");
		sql.append(" INNER JOIN t_cs_commodity_type t ON p.rel_type_code = t.`code`");
		sql.append(" INNER JOIN t_cs_commodity_share s ON s.rel_commodity_id = p.rel_commodity_id");
		sql.append(" LEFT JOIN t_cs_commodity_market m ON m.rel_id = p.rel_commodity_id AND m.rel_type = 'commodity'");
		sql.append(" LEFT JOIN t_cs_commodity_label_relation l ON p.rel_commodity_id = l.commodity_id AND p.rel_type_code = l.rel_type_code AND p.rel_org_id = l.org_id");
		sql.append(" WHERE 1=1");
		sql.append(" AND p.rel_org_id = 1");
		sql.append(" AND c.online_state = 1");
		sql.append(" AND t.online_state = 1");
		if (null != userId) {
			sql.append(" AND p.rel_org_id = "+userId);
		}
		if (StringUtils.isNotBlank(type)) {
			sql.append(" AND t.`code` = '"+type+"'");
		}
		if (StringUtils.isNotBlank(name)) {
			sql.append(" AND c.`name` LIKE '%"+name+"%'");
		}
		sql.append(" /*AND l.id IN (1, 2)*/");
		sql.append(" limit ?, ?");

		return jdbc.query(sql.toString(), new Object[] {from, number}, new RowMapper<Shop>()
		{
			@Override
			public Shop mapRow(ResultSet m, int arg1) throws SQLException
			{
				Shop p = new Shop();
				p.setOrgId(m.getLong("orgId"));
				p.setCommodityTypeCode(m.getString("commodityTypeCode"));
				p.setCommodityTypeName(m.getString("commodityTypeName"));
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

				p.setCreator(m.getString("creator"));
				p.setGmtCreated(m.getDate("gmtCreated"));
				p.setModifier(m.getString("modifier"));
				p.setGmtModified(m.getDate("gmtModified"));
				p.setIsDeleted(m.getString("isDeleted"));
				return p;
			}
		});
	}

    public int countTemplate(Long userId, String name)
    {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT count(*)");
        sql.append(" FROM");
        sql.append(" `t_cs_commodity_rate_template` t");
        sql.append(" WHERE 1=1");
		if (null != userId) {
			sql.append(" and t.rel_user_id = "+userId);
		}
		if (StringUtils.isNotBlank(name)) {
			sql.append(" and t.name LIKE '%"+name+"%'");
		}

        return jdbc.queryForObject(sql.toString(), Integer.class);
    }

    public List<JSONObject> rateTemplates(Long userId, String name, int from, int number){
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT ");
		sql.append(" r.rel_user_id,");
		sql.append(" t.scheme_id,");
		sql.append(" t.`code`,");
		sql.append(" t.`name`,");
		sql.append(" r.used,");
		sql.append(" r.creator,");
		sql.append(" r.gmt_created,");
		sql.append(" r.modifier,");
		sql.append(" r.gmt_modified,");
		sql.append(" r.is_deleted");
        sql.append(" FROM");
        sql.append(" `t_cs_commodity_rate_template` t");
		sql.append(" INNER JOIN `t_cs_commodity_rate_template_relation` r ON t.id = r.rel_temp_id");
        sql.append(" WHERE 1=1");
		if (null != userId) {
			sql.append(" and r.rel_user_id = "+userId);
		}
        if (StringUtils.isNotBlank(name)) {
            sql.append(" and t.name LIKE '%"+name+"%'");
        }
        sql.append(" limit ?, ?");

        return jdbc.query(sql.toString(), new Object[] {from, number}, new RowMapper<JSONObject>()
        {
            @Override
            public JSONObject mapRow(ResultSet m, int arg1) throws SQLException
            {
                JSONObject j = new JSONObject();
                j.put("rel_user_id",m.getString("rel_user_id"));
                j.put("scheme_id",m.getString("scheme_id"));
                j.put("code",m.getString("code"));
                j.put("name", m.getString("name"));
                j.put("used",m.getString("used"));
                j.put("creator",m.getString("creator"));
                j.put("gmt_created",m.getString("gmt_created"));
                j.put("modifier",m.getString("modifier"));
                j.put("gmt_modified",m.getString("gmt_modified"));
                j.put("is_deleted",m.getString("is_deleted"));
                return j;
            }
        });
    }

    public int countScheme(String search)
    {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT count(*)");
        sql.append(" FROM");
        sql.append(" `t_cs_commodity_rate_template` t");
        sql.append(" WHERE 1=1");

        if (StringUtils.isNotBlank(search)) {
            sql.append(" and t.scheme_id = "+search);
        }

        return jdbc.queryForObject(sql.toString(), Integer.class);
    }

	public Shop queryShopById(Long wareId)
	{
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT c.*,");
		sql.append(" t.`name` as rel_type_name,");
		sql.append(" s.*");
		sql.append(" FROM t_cs_commodity c");
		sql.append(" INNER JOIN t_cs_commodity_type t ON c.rel_type_code = t.`code`");
		sql.append(" INNER JOIN t_cs_commodity_share s ON c.id = s.rel_commodity_id");
		sql.append(" WHERE c.ware_id = ?");
		return jdbc.queryForObject(sql.toString(), new Object[] {wareId}, new RowMapper<Shop>()
		{
			@Override
			public Shop mapRow(ResultSet rs, int arg1) throws SQLException
			{
				Shop p = new Shop();
				p.setCommodityId(rs.getLong("id"));
				p.setWareId(rs.getLong("ware_id"));
				p.setCommodityTypeCode(rs.getString("rel_type_code"));
				p.setCommodityTypeName(rs.getString("rel_type_name"));
				p.setCommodityCode(rs.getString("code"));
				p.setCommodityName(rs.getString("name"));
				p.setCommodityDesc(rs.getString("desc"));
				p.setPriceDesc(rs.getString("mini_premium"));
				p.setHomeImage(rs.getString("home_image"));
				p.setShareImage(rs.getString("share_logo"));
				p.setSaleLimit(rs.getString("sale_limit"));
				p.setShareTitle(rs.getString("share_title"));
				p.setShareDesc(rs.getString("share_desc"));
				p.setOnlineStatus(rs.getString("online_state"));
				p.setSupplierCode(rs.getString("rel_supplier_code"));
				p.setCommodityLink(rs.getString("link_url"));
				p.setSort(rs.getLong("sort")+"");

				return p;
			}
		});
	}

	public Long saveOrUpdateRateTemplate(RateTemplate rt)
	{
		String insert = "INSERT INTO `vie_biz`.`t_cs_commodity_rate_template` (`id`, `scheme_id`, `code`, `name`, `creator`, `modifier`) VALUES (?, ?, ?, ?, ?, ?);";
		String update = "UPDATE `vie_biz`.`t_cs_commodity_rate_template` SET `name`=?, `creator`=?, `modifier`=? WHERE (`id`=?);";

		if(null != rt.getTempId()){
			jdbc.update(update, rt.getName(), rt.getCreator(), rt.getModifier());
		}else{
			rt.setTempId(tools.nextId("cdRateTemp"));
			jdbc.update(insert, rt.getTempId(), rt.getTempId(), rt.getCode(), rt.getName(), rt.getCreator(), rt.getModifier());
		}

		return rt.getTempId();
	}

	public Long saveOrUpdateRateTemplateRelation(RateTemplate rt)
	{
		String insert = "INSERT INTO `vie_biz`.`t_cs_commodity_rate_template_relation` (`id`, `rel_user_id`, `rel_temp_id`, `used`, `creator`, `modifier`) VALUES (?, ?, ?, ?, ?, ?);";
		String update = "UPDATE `vie_biz`.`t_cs_commodity_rate_template_relation` SET `rel_user_id`=?, `rel_temp_id`, `used`=?, `creator`=?, `modifier`=? WHERE (`id`=?);";

		if(null != rt.getRelId()){
			jdbc.update(update, rt.getUserId(), rt.getTempId(), rt.getUsed(), rt.getCreator(), rt.getModifier(), rt.getRelId());
		}else{
			rt.setRelId(tools.nextId("cdRateTempRel"));
			jdbc.update(insert, rt.getRelId(), rt.getUserId(), rt.getTempId(), rt.getUsed(), rt.getCreator(), rt.getModifier());
		}

		return rt.getRelId();
	}
}