package lerrain.service.shop;

import lombok.Data;

import java.util.Date;

@Data
public class Qrcode {
    private Long              id;
    private String            qrcodeUid;
    private Long              userId;
    private String            productIds;
    private String            isDeleted;
    private String            creator;
    private String            modifier;
    private Date              gmtCreated;
    private Date              gmtModified;
}
