package lerrain.service.shop;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class RateTemp {
    private Long   tempId;
    private Long   relId;
    private Long   userId;
    private Long   subUserId;
    private Long   schemeId;
    private List<Shop> shops;
    private String code;
    private String name;
    private String used;
    private String creator;
    private String modifier;
    private Date   gmtCreated;
    private Date   gmtModified;
    private String isDeleted;
}
