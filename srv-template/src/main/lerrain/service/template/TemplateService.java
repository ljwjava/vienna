package lerrain.service.template;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TemplateService {

    @Autowired
    TemplateDao dao;

    public List<Template> list(String name, Long userId, int from, int num) {
        return dao.list(name, userId, from, num);
    }

    public int count(String name, Long userId) {
        return dao.count(name, userId);
    }

    public Long saveOpUpdate(Template t) {
        if (StringUtils.isBlank(t.getTemplateName())) {
            throw new RuntimeException("templateName不能为空");
        }
        return dao.saveOrUpdate(t);
    }

    public int delete(Long id) {
        return dao.delete(id);
    }

    public TemplateProduct get(Long id) {
        return dao.getTp(id);
    }

    public Long saveOrUpdateTemplateProduct(TemplateProduct tp) {
        if (dao.getTp(tp.getId()) == null) {
            tp.setId(null);
        }
        return dao.saveOrUpdateTemplateProduct(tp);
    }

    public List<TemplateProductType> listProType(JSONObject p, int from, int num) {
        return dao.listProType(p, from, num);
    }

    public int countProType(JSONObject p) {
        return dao.countProType(p);
    }

    public Long saveOpUpdateProType(TemplateProductType tpt) {
        return dao.saveOrUpdateProType(tpt);
    }

    public Long saveTpRelation(TemplateProductRelation tpr) {
        return dao.saveTpRelation(tpr);
    }

    public Integer removeTpRelation(Long templateId) {
        return dao.removeTpRelation(templateId);
    }

    public Integer removeProTypeRelation(Long proTypeId) {
        return dao.removeProTypeRelation(proTypeId);
    }

    public Long saveProTypeRelation(TemplateProductTypeRelation tptr) {
        return dao.saveProTypeRelation(tptr);
    }

    public TemplateUserRelation queryByTUserId(Long templateId, Long userId) {
        return dao.queryByTUserId(templateId, userId);
    }

    public Long saveTUserRelation(TemplateUserRelation tur) {
        return dao.saveTUserRelation(tur);
    }

    public Integer removeTurRelation(Long templateId) {
        return dao.removeTurRelation(templateId);
    }

    public void batchSaveTpRelation(List<TemplateProductRelation> list) {
        if (list == null || list.size() <= 0) {
            return;
        }
        dao.batchSaveTpRelation(list);
    }

    public void batchSaveProTypeRelation(List<TemplateProductTypeRelation> list) {
        if (list == null || list.size() <= 0) {
            return;
        }
        dao.batchSaveProTypeRelation(list);
    }

    public Template findByTemplateId(Long templateId) {
        Template template = dao.get(templateId);
        return template;
    }

    public List<TemplateProductRelation> queryProductIdByTemplateId(Long templateId) {
        return dao.queryProductIdByTemplateId(templateId);
    }

    public List<TemplateProduct> queryTps(List<Long> ids) {
        if (ids == null || ids.size() <= 0) {
            return Lists.newArrayList();
        }
        return dao.queryTps(ids);
    }

    public List<TemplateProductType> queryByProductId(List<Long> pIds) {
        if (pIds == null || pIds.size() <= 0) {
            return Lists.newArrayList();
        }
        return dao.queryByProductId(pIds);

    }

    public List<TemplateProductTypeRelation> queryTptrsByTemplateIdAndProductId(Long templateId, List<Long> idList) {
        if (templateId == null || idList == null || idList.size() <= 0) {
            return Lists.newArrayList();
        }
        return dao.queryTptrsByTemplateIdAndProductId(templateId, idList);
    }
}
