package lerrain.service.shop;

import lombok.Data;

@Data
public class RateTemplate {
    private Long tempId;
    private Long relId;
    private Long userId;
    private Long schemeId;
    private String code;
    private String name;
    private String used;
    private String creator;
    private String modifier;
}
