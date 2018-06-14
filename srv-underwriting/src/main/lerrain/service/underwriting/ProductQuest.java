package lerrain.service.underwriting;

import lerrain.tool.Common;

import java.util.LinkedHashMap;
import java.util.Map;

public class ProductQuest
{
    String productId;   // packId
    String code;    // questCodes
    Map<String, Quest> quests = new LinkedHashMap<>(); // 所有题库

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Map<String, Quest> getQuests() {
        return quests;
    }

    public void setQuests(Map<String, Quest> quests) {
        this.quests = quests;
    }

    public void resetQuest(Map<String, Quest> qm){
        if(Common.isEmpty(qm))
            return;
        this.quests = new LinkedHashMap<>();
        for (String key : qm.keySet()) {
            if((","+this.code+",").indexOf(","+key+",") >= 0) {
                this.quests.put(key, qm.get(key));
            }
        }
    }
}
