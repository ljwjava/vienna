package lerrain.service.template;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import lerrain.service.common.ServiceTools;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

@Repository
public class TemplateDao {

    @Autowired
    JdbcTemplate jdbc;
    @Autowired
    ServiceTools tools;


    public Long saveOrUpdate(Template cc) {
        if (cc.getId() == null) {
            cc.setId(tools.nextId("template"));
            jdbc.update("insert into t_cs_template(id, template_name, title, message, banner, creator, gmt_created, modifier, gmt_modified, is_deleted) values(?,?,?,?,?,?,?,?,?,?)",
                    cc.getId(), cc.getTemplateName(), cc.getTitle(), cc.getMessage(), cc.getBanner(), "system", new Date(), "system", new Date(), "N");
        } else {
            jdbc.update("update t_cs_template set template_name=?, title=?, message=?, banner=?, modifier=?, gmt_modified=? where id=?",
                    cc.getTemplateName(), cc.getTitle(), cc.getMessage(), cc.getBanner(), "system", new Date(), cc.getId());
        }
        return cc.getId();
    }

    public Template get(Long id) {
        String sql = " select * from t_cs_template where id=" + id;
        List<Template> templates = jdbc.query(sql, new Object[]{}, new BeanPropertyRowMapper<>(Template.class));
        return templates != null && templates.size() > 0 ? templates.get(0) : null;
    }


    public List<Template> list(String name, int from, int num) {
        StringBuffer sql = new StringBuffer("select * from t_cs_template where is_deleted='N' ");
        if (StringUtils.isNotBlank(name)) {
            sql.append(" and template_name like '%" + name + "%' ");
        }
        sql.append(" order by gmt_modified desc");
        sql.append(" limit ?, ?");

        return jdbc.query(sql.toString(), new Object[]{from, num}, new RowMapper<Template>() {

            @Override
            public Template mapRow(ResultSet m, int arg1) throws SQLException {
                Template t = new Template();
                t.setId(m.getLong("id"));
                t.setTemplateName(m.getString("template_name"));
                t.setGmtModified(m.getDate("gmt_modified"));
                t.setLink(m.getString("link"));
                return t;
            }
        });
    }

    public int count(String name) {
        StringBuffer sql = new StringBuffer("select count(1) from t_cs_template where is_deleted='N' ");
        if (StringUtils.isNotBlank(name)) {
            sql.append(" and template_name like '%" + name + "%' ");
        }
        return jdbc.queryForObject(sql.toString(), Integer.class);
    }

    public int delete(Long id) {
        String sql = "update t_cs_template set is_deleted='Y',gmt_modified=now() where id= " + id;
        return jdbc.update(sql);
    }

    public Long saveOrUpdateTemplateProduct(TemplateProduct tp) {
        if (tp.getId() == null) {
            tp.setId(tools.nextId("templateProduct"));
            jdbc.update("INSERT INTO t_cs_template_product(id, package_name, pro_desc, premium, link, pic, label, button_name, button_link, is_index, index_pic, creator, gmt_created, modifier, gmt_modified, is_deleted)" +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    tp.getId(), tp.getPackageName(), tp.getProDesc(), tp.getPremium(), tp.getLink(), tp.getPic(), tp.getLabel(), tp.getButtonName(), tp.getButtonLink(), tp.getIsIndex(), tp.getIndexPic(), "system", new Date(), "system", new Date(), "N");
        } else {
            jdbc.update("update t_cs_template_product set package_name=?, pro_desc=?, premium=?, link=?,pic=?,label=?,button_name=?,button_link=?,is_index=?,index_pic=?, gmt_modified=? where id=?",
                    tp.getPackageName(), tp.getProDesc(), tp.getPremium(), tp.getLink(), tp.getPic(), tp.getLabel(), tp.getButtonName(), tp.getButtonLink(), tp.getIsIndex(), tp.getIndexPic(), new Date(), tp.getId());
        }
        return tp.getId();
    }

    public List<TemplateProductType> listProType(JSONObject p, int from, int num) {
        StringBuffer sql = new StringBuffer("select * from t_cs_template_product_type where is_deleted='N' ");
        sql.append(" order by gmt_modified desc");
        sql.append(" limit ?, ?");
        return jdbc.query(sql.toString(), new Object[]{from, num}, new RowMapper<TemplateProductType>() {
            @Override
            public TemplateProductType mapRow(ResultSet m, int arg1) throws SQLException {
                TemplateProductType t = new TemplateProductType();
                t.setId(m.getLong("id"));
                t.setProductTypeName(m.getString("product_type_name"));
                t.setGmtModified(m.getDate("gmt_modified"));
                return t;
            }
        });
    }

    public int countProType(JSONObject p) {
        StringBuffer sql = new StringBuffer("select count(1) from t_cs_template_product_type where is_deleted='N' ");
        return jdbc.queryForObject(sql.toString(), Integer.class);
    }

    public Long saveOrUpdateProType(TemplateProductType tpt) {
        if (tpt.getId() == null) {
            tpt.setId(tools.nextId("productType"));
            jdbc.update("insert into t_cs_template_product_type(id,product_type_name,creator,gmt_created,modifier,gmt_modified,is_deleted) " +
                    "VALUES (?,?,?,?,?,?,?) ", tpt.getId(), tpt.getProductTypeName(), "system", new Date(), "system", new Date(), "N");
        } else {
            jdbc.update("update t_cs_template_product_type set product_type_name=?,gmt_modified=? where id=?", tpt.getProductTypeName(), tpt.getId());
        }
        return tpt.getId();
    }

    public Long saveTpRelation(TemplateProductRelation tpr) {
        if (tpr.getId() == null) {
            tpr.setId(tools.nextId("tpRelation"));
            jdbc.update("insert into t_cs_template_product_relation(id,template_id,product_id,,creator,gmt_created,modifier,gmt_modified,is_deleted) VALUES (?,?,?,?,?,?,?,?)",
                    tpr.getId(), tpr.getTemplateId(), tpr.getProductId(), "system", new Date(), "system", new Date(), "N");
        }
        return tpr.getId();
    }

    public Long saveProTypeRelation(TemplateProductTypeRelation tptr) {
        if (tptr.getId() == null) {
            tptr.setId(tools.nextId("tptrRelation"));
            jdbc.update("insert into t_cs_template_product_type_relation(id,product_type_id,product_id,creator,gmt_created,modifier,gmt_modified,is_deleted) VALUES (?,?,?,?,?,?,?,?)",
                    tptr.getId(), tptr.getProductTypeId(), tptr.getProductId(), "system", new Date(), "system", new Date(), "N");
        }
        return tptr.getId();
    }

    public Long saveTUserRelation(TemplateUserRelation tur) {
        if (tur.getId() == null) {
            tur.setId(tools.nextId("turRelation"));
            jdbc.update("insert into t_cs_template_user_relation(id,user_id,template_id,creator,gmt_created,modifier,gmt_modified,is_deleted) VALUES (?,?,?,?,?,?,?,?)",
                    tur.getId(), tur.getUserId(), tur.getTemplateId(), "system", new Date(), "system", new Date(), "N");
        }
        return tur.getId();
    }

    public TemplateProduct getTp(Long id) {
        if (id == null) {
            return null;
        }
        String sql = " select * from t_cs_template_product where id=" + id + " and is_deleted='N' ";
        List<TemplateProduct> tps = jdbc.query(sql, new Object[]{}, new BeanPropertyRowMapper<>(TemplateProduct.class));
        return (tps != null && tps.size() > 0) ? tps.get(0) : null;
    }

    public Integer removeTpRelation(Long templateId) {
        String sql = "delete from  t_cs_template_product_relation where template_id= " + templateId;
        return jdbc.update(sql);
    }

    public Integer removeProTypeRelation(Long typeId) {
        String sql = "delete from  t_cs_template_product_type_relation where product_type_id= " + typeId;
        return jdbc.update(sql);
    }

    public void batchSaveTpRelation(List<TemplateProductRelation> list) {
        final List<TemplateProductRelation> tpr = list;
        String sql = "insert into t_cs_template_product_relation(template_id,product_id,creator,gmt_created,modifier,gmt_modified,is_deleted) VALUES (?,?,?,?,?,?,?)";
        jdbc.batchUpdate(sql, new BatchPreparedStatementSetter() {
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Long templateId = tpr.get(i).getTemplateId();
                Long productId = tpr.get(i).getProductId();
                ps.setLong(1, templateId);
                ps.setLong(2, productId);
                ps.setString(3, "system");
                ps.setDate(4, new java.sql.Date(new Date().getTime()));
                ps.setString(5, "system");
                ps.setDate(6, new java.sql.Date(new Date().getTime()));
                ps.setString(7, "N");
            }

            public int getBatchSize() {
                return tpr.size();
            }
        });
    }

    public void batchSaveProTypeRelation(List<TemplateProductTypeRelation> list) {
        final List<TemplateProductTypeRelation> tpr = list;
        String sql = "insert into t_cs_template_product_type_relation(product_type_id,product_id,template_id,creator,gmt_created,modifier,gmt_modified,is_deleted) VALUES (?,?,?,?,?,?,?,?)";
        jdbc.batchUpdate(sql, new BatchPreparedStatementSetter() {
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Long productTypeId = tpr.get(i).getProductTypeId();
                Long productId = tpr.get(i).getProductId();
                Long templateId = tpr.get(i).getTemplateId();
                ps.setLong(1, productTypeId);
                ps.setLong(2, productId);
                ps.setLong(3, templateId);
                ps.setString(4, "system");
                ps.setDate(5, new java.sql.Date(new Date().getTime()));
                ps.setString(6, "system");
                ps.setDate(7, new java.sql.Date(new Date().getTime()));
                ps.setString(8, "N");
            }

            public int getBatchSize() {
                return tpr.size();
            }
        });
    }

    public TemplateUserRelation queryByTUserId(Long templateId, Long userId) {
        String sql = " select * from t_cs_template_user_relation where user_id=" + userId + " and template_id=" + templateId;
        List<TemplateUserRelation> tps = jdbc.query(sql, new Object[]{}, new BeanPropertyRowMapper<>(TemplateUserRelation.class));
        return (tps != null && tps.size() > 0) ? tps.get(0) : null;
    }

    public List<TemplateProductRelation> queryProductIdByTemplateId(Long templateId) {
        String sql = "select * from t_cs_template_product_relation where is_deleted='N' and template_id=" + templateId;
        return jdbc.query(sql, new Object[]{}, new BeanPropertyRowMapper<>(TemplateProductRelation.class));
    }

    public List<TemplateProduct> queryTps(List<Long> ids) {
        if (ids == null && ids.size() <= 0) {
            return Lists.newArrayList();
        }
        StringBuilder sql = new StringBuilder(" select * from t_cs_template_product where is_deleted='N' and id in ( ");
        for (Long id : ids) {
            sql.append(id).append(",");
        }
        sql.append(" )");
        return jdbc.query(sql.replace(sql.lastIndexOf(","), sql.lastIndexOf(",") + 1, "").toString(),
                new Object[]{}, new BeanPropertyRowMapper<>(TemplateProduct.class));
    }

    public List<TemplateProductType> queryByProductId(List<Long> pIds) {
        if (pIds == null && pIds.size() <= 0) {
            return Lists.newArrayList();
        }
        StringBuilder sql = new StringBuilder(" select * from t_cs_template_product_type where is_deleted='N' and id in ( select product_type_id from t_cs_template_product_type_relation where  product_id in( ");
        for (Long id : pIds) {
            sql.append(id).append(",");
        }
        sql.append(" ))");
        return jdbc.query(sql.replace(sql.lastIndexOf(","), sql.lastIndexOf(",") + 1, "").toString(),
                new Object[]{}, new BeanPropertyRowMapper<>(TemplateProductType.class));
    }

    public List<TemplateProductTypeRelation> queryTptrsByTemplateIdAndProductId(Long templateId, List<Long> idList) {
        if (idList == null && idList.size() <= 0) {
            return Lists.newArrayList();
        }
        StringBuilder sql = new StringBuilder(" select * from t_cs_template_product_type_relation where is_deleted='N' and template_id= " + templateId + " and product_id in ( ");
        for (Long id : idList) {
            sql.append(id).append(",");
        }
        sql.append(" )");
        return jdbc.query(sql.replace(sql.lastIndexOf(","), sql.lastIndexOf(",") + 1, "").toString(),
                new Object[]{}, new BeanPropertyRowMapper<>(TemplateProductTypeRelation.class));
    }
}