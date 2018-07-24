package lerrain.service.shop;

import com.alibaba.fastjson.JSONObject;
import lerrain.service.common.ServiceTools;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

@Repository
public class ShopDao
{
	@Autowired JdbcTemplate jdbc;
	@Autowired ServiceTools tools;

	public int countType(Shop contion)
	{
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT");
		sql.append(" count(dd.`code`)");
		sql.append(" FROM (");
		sql.append(" SELECT t.`code`, p.ware_id as wareId");
		sql.append(" FROM");
		sql.append(" t_cs_commodity_rate_template_relation r");
		sql.append(" INNER JOIN t_product_fee_cust f ON r.rel_temp_id = f.scheme_id");
		sql.append(" INNER JOIN t_ware_pack p ON p.id = f.product_id");
		sql.append(" INNER JOIN t_cs_commodity c ON c.ware_id = p.ware_id");
		sql.append(" INNER JOIN t_cs_commodity_type t ON c.rel_type_code = t.`code`");
		sql.append(" LEFT JOIN t_cs_commodity_share s ON s.rel_commodity_id = c.id");
		sql.append(" WHERE r.is_deleted='N'");
		sql.append(" AND c.is_deleted='N'");
		sql.append(" AND t.is_deleted='N'");
		sql.append(" AND s.is_deleted='N'");
		sql.append(" AND c.online_state = 1");
		sql.append(" AND t.online_state = 1");
		if (StringUtils.isNotBlank(contion.getQrcodeUid())){
			sql.append(" AND c.id in ("+this.queryProductsByQrcodeInfo(contion).getProductIds()+")");
		}
		if(null != contion.getUserId()) {
			sql.append(" AND r.rel_user_id = "+contion.getUserId());
		}
		if (StringUtils.isNotBlank(contion.getCommodityName())) {
			sql.append(" AND c.`name` LIKE '%"+ contion.getCommodityName() +"%'");
		}
		sql.append(" GROUP BY wareId");
		sql.append(" ) as dd");
		return jdbc.queryForObject(sql.toString(), Integer.class);
	}

	public List<JSONObject> types(Shop contion, int from, int number){
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
		sql.append(" c.`code` AS cdCode,");
		sql.append(" p.ware_id AS wareId");
		sql.append(" FROM");
		sql.append(" t_cs_commodity_rate_template_relation r");
		sql.append(" INNER JOIN t_product_fee_cust f ON r.rel_temp_id = f.scheme_id");
		sql.append(" INNER JOIN t_ware_pack p ON p.id = f.product_id");
		sql.append(" INNER JOIN t_cs_commodity c ON c.ware_id = p.ware_id");
		sql.append(" INNER JOIN t_cs_commodity_type t ON c.rel_type_code = t.`code`");
		sql.append(" LEFT JOIN t_cs_commodity_share s ON s.rel_commodity_id = c.id");
		sql.append(" WHERE r.is_deleted='N'");
		sql.append(" AND c.is_deleted='N'");
		sql.append(" AND t.is_deleted='N'");
		sql.append(" AND s.is_deleted='N'");
		sql.append(" AND c.online_state = 1");
		sql.append(" AND t.online_state = 1");
		if (StringUtils.isNotBlank(contion.getQrcodeUid())){
			sql.append(" AND c.id in ("+this.queryProductsByQrcodeInfo(contion).getProductIds()+")");
		}
		if(null != contion.getUserId()) {
			sql.append(" AND r.rel_user_id = "+contion.getUserId());
		}
        if (StringUtils.isNotBlank(contion.getCommodityName())) {
            sql.append(" AND c.`name` LIKE '%"+ contion.getCommodityName() +"%'");
        }
		sql.append(" GROUP BY wareId");
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

	public int count(Shop contion)
	{
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT");
		sql.append(" count(*)");
		sql.append(" FROM (");
		sql.append(" SELECT p.ware_id AS wareId");
		sql.append(" FROM");
		sql.append(" t_cs_commodity_rate_template_relation r");
		sql.append(" INNER JOIN t_product_fee_cust f ON r.rel_temp_id = f.scheme_id");
		sql.append(" INNER JOIN t_ware_pack p ON p.id = f.product_id");
		sql.append(" INNER JOIN t_cs_commodity c ON c.ware_id = p.ware_id");
		sql.append(" INNER JOIN t_cs_commodity_type t ON c.rel_type_code = t.`code`");
		sql.append(" LEFT JOIN t_cs_commodity_share s ON s.rel_commodity_id = c.id");
		sql.append(" LEFT JOIN t_cs_commodity_market m ON m.rel_id = c.id");
		sql.append(" AND m.rel_type = 'commodity'");
		sql.append(" LEFT JOIN t_cs_commodity_label_relation l ON c.id = l.commodity_id");
		sql.append(" AND c.rel_type_code = l.rel_type_code");
		sql.append(" AND r.rel_user_id = l.user_id");
		sql.append(" WHERE r.is_deleted='N'");
		sql.append(" AND c.is_deleted='N'");
		sql.append(" AND t.is_deleted='N'");
		sql.append(" AND s.is_deleted='N'");
		sql.append(" AND c.online_state = 1");
		sql.append(" AND t.online_state = 1");
		sql.append(" AND r.used = 'Y'");
		if (StringUtils.isNotBlank(contion.getQrcodeUid())){
			sql.append(" AND c.id in ("+this.queryProductsByQrcodeInfo(contion).getProductIds()+")");
		}
		if (null != contion.getUserId()) {
			sql.append(" AND r.rel_user_id = "+contion.getUserId());
		}
		if (!StringUtils.equals("all",contion.getCommodityTypeCode()) && StringUtils.isNotBlank(contion.getCommodityTypeCode())) {
			sql.append(" AND t.`code` = '"+contion.getCommodityTypeCode()+"'");
		}
		if (StringUtils.isNotBlank(contion.getCommodityName())) {
			sql.append(" AND c.`name` LIKE '%"+contion.getCommodityName()+"%'");
		}
		sql.append(" GROUP BY wareId");
		sql.append(" ) as dd");
		return jdbc.queryForObject(sql.toString(), Integer.class);
	}

	public List<Shop> commoditys(Shop contion, int from, int number)
	{
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT ");
		sql.append(" p.ware_id AS wareId,");
		sql.append(" r.rel_user_id AS userId,");
		sql.append(" r.sub_user_id AS subUserId,");
		sql.append(" r.rel_temp_id AS tempId,");
		sql.append(" f.product_id AS productId,");
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
		sql.append(" t_cs_commodity_rate_template_relation r");
		sql.append(" INNER JOIN t_product_fee_cust f ON r.rel_temp_id = f.scheme_id");
		sql.append(" INNER JOIN t_ware_pack p ON p.id = f.product_id");
		sql.append(" INNER JOIN t_cs_commodity c ON c.ware_id = p.ware_id");
		sql.append(" INNER JOIN t_cs_commodity_type t ON c.rel_type_code = t.`code`");
		sql.append(" LEFT JOIN t_cs_commodity_share s ON s.rel_commodity_id = c.id");
		sql.append(" LEFT JOIN t_cs_commodity_market m ON m.rel_id = c.id");
		sql.append(" AND m.rel_type = 'commodity'");
		sql.append(" LEFT JOIN t_cs_commodity_label_relation l ON c.id = l.commodity_id");
		sql.append(" AND c.rel_type_code = l.rel_type_code");
		sql.append(" AND r.rel_user_id = l.user_id");
		sql.append(" WHERE r.is_deleted = 'N'");
		sql.append(" AND c.is_deleted = 'N'");
		sql.append(" AND t.is_deleted = 'N'");
		sql.append(" AND s.is_deleted = 'N'");
		sql.append(" AND c.online_state = 1");
		sql.append(" AND t.online_state = 1");
		sql.append(" AND r.used = 'Y'");
		if (StringUtils.isNotBlank(contion.getCommodityIds())){
            sql.append(" AND c.id in ("+contion.getCommodityIds()+")");
        }
		if (StringUtils.isNotBlank(contion.getQrcodeUid())){
			sql.append(" AND c.id in ("+this.queryProductsByQrcodeInfo(contion).getProductIds()+")");
		}
		if (null != contion.getUserId()) {
			sql.append(" AND r.rel_user_id = "+contion.getUserId());
		}
		if (!StringUtils.equals("all",contion.getCommodityTypeCode()) && StringUtils.isNotBlank(contion.getCommodityTypeCode())) {
			sql.append(" AND t.`code` = '"+contion.getCommodityTypeCode()+"'");
		}
		if (StringUtils.isNotBlank(contion.getCommodityName())) {
			sql.append(" AND c.`name` LIKE '%"+contion.getCommodityName()+"%'");
		}
		sql.append(" GROUP BY wareId");
		sql.append(" limit ?, ?");

		return jdbc.query(sql.toString(), new Object[] {from, number}, new RowMapper<Shop>()
		{
			@Override
			public Shop mapRow(ResultSet m, int arg1) throws SQLException
			{
				Shop p = new Shop();
				p.setWareId(m.getLong("wareId"));
				p.setUserId(m.getLong("userId"));
				p.setSubUserId(m.getLong("subUserId"));
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

    public int countTemplate(RateTemp contion)
    {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT count(*)");
        sql.append(" FROM");
        sql.append(" `t_cs_commodity_rate_template` t");
		sql.append(" WHERE t.is_deleted='N'");
		if (null != contion.getUserId()) {
			sql.append(" and t.creator = "+contion.getUserId());
		}
        return jdbc.queryForObject(sql.toString(), Integer.class);
    }

    public List<JSONObject> rateTemplates(RateTemp contion, int from, int number){
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT ");
		sql.append(" t.id as tempId,");
		sql.append(" t.scheme_id as schemeId,");
		sql.append(" t.`code`,");
		sql.append(" t.`name`,");
		sql.append(" t.creator,");
		sql.append(" t.gmt_created,");
		sql.append(" t.modifier,");
		sql.append(" t.gmt_modified,");
		sql.append(" t.is_deleted");
        sql.append(" FROM");
        sql.append(" `t_cs_commodity_rate_template` t");
        sql.append(" WHERE t.is_deleted='N'");
		if (null != contion.getUserId()) {
			sql.append(" and t.creator = "+contion.getUserId());
		}
        if (StringUtils.isNotBlank(contion.getName())) {
            sql.append(" and t.name LIKE '%"+contion.getName()+"%'");
        }
        sql.append(" limit ?, ?");

        return jdbc.query(sql.toString(), new Object[] {from, number}, new RowMapper<JSONObject>()
        {
            @Override
            public JSONObject mapRow(ResultSet m, int arg1) throws SQLException
            {
                JSONObject j = new JSONObject();
                j.put("userId",m.getString("creator"));
				j.put("tempId",m.getString("tempId"));
                j.put("schemeId",m.getString("schemeId"));
                j.put("code",m.getString("code"));
                j.put("name", m.getString("name"));
                j.put("creator",m.getString("creator"));
                j.put("gmtCreated",m.getString("gmt_created"));
                j.put("modifier",m.getString("modifier"));
                j.put("gmtModified",m.getString("gmt_modified"));
                j.put("isDeleted",m.getString("is_deleted"));
                return j;
            }
        });
    }

	public int countUsedTemplateByTempId(Long tempId)
	{
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT");
		sql.append(" COUNT(r.rel_user_id)");
		sql.append(" FROM");
		sql.append(" `t_cs_commodity_rate_template_relation` r");
		sql.append(" WHERE r.is_deleted='N'");
		if (null != tempId) {
			sql.append(" and r.rel_temp_id = "+tempId);
		}

		return jdbc.queryForObject(sql.toString(), Integer.class);
	}

	public int countUsedTemplate(RateTemp contion)
	{
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT count(*)");
		sql.append(" FROM");
		sql.append(" `t_cs_commodity_rate_template` t");
		sql.append(" INNER JOIN `t_cs_commodity_rate_template_relation` r ON t.id = r.rel_temp_id");
		sql.append(" WHERE t.is_deleted='N'");
		sql.append(" and r.is_deleted='N'");
		if (null != contion.getSubUserId()) {
			sql.append(" and r.sub_user_id = "+contion.getSubUserId());
		}
		if (null != contion.getUserId()) {
			sql.append(" and r.rel_user_id = "+contion.getUserId());
		}
		if (StringUtils.isNotBlank(contion.getUsed())) {
			sql.append(" and r.used = '"+contion.getUsed()+"'");
		}
		return jdbc.queryForObject(sql.toString(), Integer.class);
	}

	public List<JSONObject> usedRateTemplates(RateTemp contion, int from, int number){
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT ");
		sql.append(" t.id as tempId,");
		sql.append(" r.id as relId,");
		sql.append(" t.scheme_id as schemeId,");
		sql.append(" r.rel_user_id as userId,");
		sql.append(" r.sub_user_id as subUserId,");
		sql.append(" r.used,");
		sql.append(" t.`code`,");
		sql.append(" t.`name`,");
		sql.append(" t.creator,");
		sql.append(" t.gmt_created,");
		sql.append(" t.modifier,");
		sql.append(" t.gmt_modified,");
		sql.append(" t.is_deleted");
		sql.append(" FROM");
		sql.append(" `t_cs_commodity_rate_template` t");
		sql.append(" INNER JOIN `t_cs_commodity_rate_template_relation` r ON t.id = r.rel_temp_id");
		sql.append(" WHERE t.is_deleted='N'");
		if (null != contion.getSubUserId()) {
			sql.append(" and r.sub_user_id = "+contion.getSubUserId());
		}
		if (null != contion.getUserId()) {
			sql.append(" and r.rel_user_id = "+contion.getUserId());
		}
		if (StringUtils.isNotBlank(contion.getUsed())) {
			sql.append(" and r.used = '"+contion.getUsed()+"'");
		}
		sql.append(" limit ?, ?");

		return jdbc.query(sql.toString(), new Object[] {from, number}, new RowMapper<JSONObject>()
		{
			@Override
			public JSONObject mapRow(ResultSet m, int arg1) throws SQLException
			{
				JSONObject j = new JSONObject();
				j.put("relId",m.getString("relId"));
				j.put("tempId",m.getString("tempId"));
				j.put("schemeId",m.getString("schemeId"));
				j.put("userId",m.getString("userId"));
				j.put("subUserId",m.getString("subUserId"));
				j.put("code",m.getString("code"));
				j.put("name", m.getString("name"));
				j.put("creator",m.getString("creator"));
				j.put("gmtCreated",m.getString("gmt_created"));
				j.put("modifier",m.getString("modifier"));
				j.put("gmtModified",m.getString("gmt_modified"));
				j.put("isDeleted",m.getString("is_deleted"));
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
		sql.append(" LEFT JOIN t_cs_commodity_share s ON c.id = s.rel_commodity_id");
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

	public Long saveOrUpdateRateTemplate(RateTemp rt)
	{
		String insert = "INSERT INTO `vie_biz`.`t_cs_commodity_rate_template` (`id`, `scheme_id`, `code`, `name`, `creator`, `modifier`) VALUES (?, ?, ?, ?, ?, ?);";
		String update = "UPDATE `vie_biz`.`t_cs_commodity_rate_template` SET `code`=?, `name`=?, `creator`=?, `modifier`=? WHERE (`id`=?);";

		if(null != rt.getTempId()){
			jdbc.update(update, rt.getCode(), rt.getName(), rt.getCreator(), rt.getModifier(), rt.getTempId());
		}else{
			rt.setTempId(tools.nextId("cdRateTemp"));
			jdbc.update(insert, rt.getTempId(), rt.getTempId(), rt.getCode(), rt.getName(), rt.getCreator(), rt.getModifier());
		}

		return rt.getTempId();
	}

	public Long saveOrUpdateRateTemplateRelation(RateTemp rt)
	{
		String insert = "INSERT INTO `vie_biz`.`t_cs_commodity_rate_template_relation` (`id`, `rel_user_id`, `sub_user_id`, `rel_temp_id`, `used`, `creator`, `modifier`) VALUES (?, ?, ?, ?, ?, ?, ?);";
		String update = "UPDATE `vie_biz`.`t_cs_commodity_rate_template_relation` SET `rel_user_id`=?, `sub_user_id`=?, `rel_temp_id`=?, `used`=?, `creator`=?, `modifier`=?, `is_deleted`=? WHERE (`id`=?);";

		if(null != rt.getRelId()){
			jdbc.update(update, rt.getUserId(), rt.getSubUserId(), rt.getTempId(), rt.getUsed(), rt.getCreator(), rt.getModifier(), rt.getIsDeleted(), rt.getRelId());
		}else{
			rt.setRelId(tools.nextId("cdRateTempRel"));
			jdbc.update(insert, rt.getRelId(), rt.getUserId(), rt.getSubUserId(), rt.getTempId(), rt.getUsed(), rt.getCreator(), rt.getModifier());
		}

		return rt.getRelId();
	}

	public Long deleteRateTemplate(RateTemp rt)
	{
		String delTemp = "UPDATE `vie_biz`.`t_cs_commodity_rate_template` SET `is_deleted`='Y', `modifier`=? WHERE (`id`=?);";
		String delTempRel = "UPDATE `vie_biz`.`t_cs_commodity_rate_template_relation` SET `is_deleted`='Y', `modifier`=? WHERE (`rel_temp_id`=?);";

		jdbc.update(delTemp, rt.getModifier(), rt.getTempId());
		jdbc.update(delTempRel, rt.getModifier(), rt.getTempId());

		return rt.getTempId();
	}


	public List<JSONObject> queryProductsByWareId(Long wareId)
	{
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT ");
		sql.append(" p.*,");
		sql.append(" r.content");
		sql.append(" FROM");
		sql.append(" `t_ware_pack` p");
		sql.append(" INNER JOIN t_cs_product_rate r ON p.id = r.product_id");
		sql.append(" WHERE");
		sql.append(" p.ware_id = ?");
		return jdbc.query(sql.toString(), new Object[] {wareId}, new RowMapper<JSONObject>()
		{
			@Override
			public JSONObject mapRow(ResultSet rs, int arg1) throws SQLException
			{
				JSONObject p = new JSONObject();
				p.put("id", rs.getLong("id"));
				p.put("code", rs.getString("code"));
				p.put("name", rs.getString("name"));
				p.put("ware_id", rs.getLong("ware_id"));
				p.put("content", rs.getString("content"));
				return p;
			}
		});
	}

	public Long saveOrUpdateQrcodeInfo(Qrcode qrcode)
	{
		String insert = "INSERT INTO `vie_biz`.`t_cs_qrcode_info` (`id`, `qrcode_uid`, `user_id`, `product_ids`, `creator`, `modifier`) VALUES (?, ?, ?, ?, ?, ?);";
		String update = "UPDATE `vie_biz`.`t_cs_qrcode_info` SET `product_ids`=?, `modifier`=?, `gmt_modified`=? WHERE (`user_id`=?);";

		if(null != qrcode.getId()){
			jdbc.update(update, qrcode.getProductIds(), qrcode.getUserId(), new Date(), qrcode.getUserId());
		}else{
			qrcode.setId(tools.nextId("csQrcode"));
			jdbc.update(insert,qrcode.getId(), qrcode.getQrcodeUid(), qrcode.getUserId(), qrcode.getProductIds(), qrcode.getUserId(), qrcode.getUserId());
		}

		return qrcode.getId();
	}

	public Qrcode queryProductsByQrcodeInfo(Shop contion)
	{
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT * FROM t_cs_qrcode_info q");
		sql.append(" WHERE q.is_deleted = 'N'");
		sql.append(" AND q.qrcode_uid = ?");
		sql.append(" AND q.user_id = ?");
		return jdbc.queryForObject(sql.toString(), new Object[] {contion.getQrcodeUid(),contion.getUserId()}, new RowMapper<Qrcode>()
		{
			@Override
			public Qrcode mapRow(ResultSet m, int arg1) throws SQLException
			{
				Qrcode p = new Qrcode();
				p.setId(m.getLong("id"));
				p.setQrcodeUid(m.getString("qrcode_uid"));
				p.setProductIds(m.getString("product_ids"));
				p.setCreator(m.getString("creator"));
				p.setGmtCreated(m.getDate("gmt_created"));
				p.setModifier(m.getString("modifier"));
				p.setGmtModified(m.getDate("gmt_modified"));
				p.setIsDeleted(m.getString("is_deleted"));
				return p;
			}
		});
	}

	public Long saveOrUpdateCommodityPlan(Shop shop)
	{
		String update = "UPDATE `vie_biz`.`t_cs_commodity_plan` SET `rel_user_id`=?, `rel_commodity_id`=?, `rel_commoidty_code`=?, `rel_commoidty_name`=?, `rel_type_code`=?, `rel_type_name`=?, `modifier`=?, `gmt_modified`=?,  WHERE (`id`=?);";
		String insert = "INSERT INTO `vie_biz`.`t_cs_commodity_plan` (`id`, `rel_user_id`, `rel_commodity_id`, `rel_commoidty_code`, `rel_commoidty_name`, `rel_type_code`, `rel_type_name`, `creator`, `modifier`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";

		if(null != shop.getCommodityPlanId()){
			jdbc.update(update, shop.getSubUserId(), shop.getCommodityId(), shop.getCommodityCode(),shop.getCommodityName(), shop.getCommodityTypeCode(), shop.getCommodityTypeName(), shop.getModifier(),shop.getGmtModified() ,shop.getCommodityPlanId());
		}else{
			shop.setCommodityPlanId(tools.nextId("csCommodityPlan"));
			jdbc.update(insert,shop.getCommodityPlanId(), shop.getSubUserId(), shop.getCommodityId(), shop.getCommodityCode(),shop.getCommodityName(), shop.getCommodityTypeCode(), shop.getCommodityTypeName(), shop.getCreator(), shop.getModifier());
		}

		return shop.getCommodityPlanId();
	}
}