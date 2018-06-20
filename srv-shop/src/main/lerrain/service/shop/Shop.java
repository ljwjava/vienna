package lerrain.service.shop;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.util.Date;

@Data
public class Shop {
    private Long wareId;                        //产品包id
    private Long orgId;                       //业务员userId,如果是老渠道accountId
    private String companyId;                   //业务员归属公司id
    private String commodityTypeCode;           //商品类型编码
    private String commodityTypeName;           //商品类型名称
    private Long commodityId;                 //商品ID
    private String commodityCode;               //商品编码
    private String commodityName;               //商品名称
    private String commodityDesc;               //商品描述
    private String priceDesc;                   //商品价格
    private String priceDescText;               //商品价格描述
    private String homeImage;                   //商品主图地址
    private String shareImage;                  //商品分享小图地址
    private String saleLimit;                   //销售限制，1:可销售 2:可查看不可销售 3:不可销售
    private String shareTitle;                  //商品分享标题
    private String shareDesc;                   //商品分享描述
    private String marketType;                  //营销类型new hot reward
    private String marketIconUrl;               //营销图片地址
    private String helperUrl;                   //助手
    private String commissionUrl;               //佣金明细
    private String commissionRate;              //佣金比例
    private String commissionRateText;          //佣金提示信息
    private String onlineStatus;                //0未上架, 1上架, 2:不显示
    private String supplierCode;                //供应商编码
    private JSONObject supplierInfo;            //供应商信息
    private String commodityLink;               //商品链接地址
    private String extraInfo;                   //扩展信息
    private JSONArray factors;                  //商品展示因子
    private JSONArray rules;                    //商品展示规则
    private String sort;                        //排序

    private String creator;
    private Date gmtCreated;
    private String modifier;
    private Date gmtModified;
    private String isDeleted;

}
