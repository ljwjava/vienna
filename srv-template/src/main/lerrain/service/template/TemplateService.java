package lerrain.service.template;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TemplateService {

    @Autowired
    TemplateDao dao;

    public List<Template> list(String name, int from, int num) {
        return dao.list(name, from, num);
    }

    public int count(String name) {
        return dao.count(name);
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
}
