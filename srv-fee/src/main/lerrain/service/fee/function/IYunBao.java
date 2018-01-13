package lerrain.service.fee.function;

import com.alibaba.fastjson.JSON;
import lerrain.service.fee.Fee;
import lerrain.service.fee.IybPay;
import lerrain.service.fee.IybPushMsg;
import lerrain.tool.Common;
import lerrain.tool.formula.Factors;
import lerrain.tool.formula.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class IYunBao extends HashMap<String, Object>
{
    @Autowired
    IybPay iybPay;

    @Autowired
    IybPushMsg pushMsg;

    public IYunBao()
    {
        this.put("pay", new Function()
        {
            @Override
            public Object run(Object[] objects, Factors factors)
            {
                try
                {
                    Fee fee = (Fee)objects[0];

                    if (fee.getDraweeType() != 2 || fee.getDrawee() != 1 || fee.getPayeeType() != 1)
                        return JSON.parseObject("{isSuccess: false}");

                    Long iybPolicyId = null;
                    Long productId = null;
                    Long parentUserId = null;
                    double premium = 0;
                    String productName = "";

                    Map map = fee.getExtra();
                    if (map != null && (fee.getType() == 1 || fee.getType() == 2))
                    {
                        productName = Common.trimStringOf(map.get("productName"));
                        iybPolicyId = Common.toLong(map.get("iybPolicyId"));
                        parentUserId = Common.toLong(map.get("parentUserId"));
                        productId = Common.toLong(map.get("productId"));
                        premium = Common.toDouble(map.get("premium"));
                    }

                    return iybPay.pay(fee.getPayee(), fee.getType(), fee.getAmount(), productName, fee.getFreeze(), fee.getMemo(), iybPolicyId, fee.getBizNo(), parentUserId, productId, premium);
                }
                catch (Exception e)
                {
                    throw new RuntimeException(e);
                }
            }
        });

        this.put("push", new Function()
        {
            @Override
            public Object run(Object[] objects, Factors factors)
            {
                return pushMsg.send(objects[0].toString(), "tips", "self", objects[1].toString(), objects[2].toString(), objects[3].toString(), null);
            }
        });
    }
}


//    private static final String msgTitle = "一笔热腾腾的推广费到账啦！点击这里，加油赚云宝！";
//    private static final String msgContent = "您有%.2f元推广费已到云宝账户，还有更多高推广费产品，再接再厉！封神之日可待！独乐乐不如众乐乐，邀请好友分享云宝赚钱秘籍！";
//    private static final String msgParentTitle = "您的伙伴推广成功，您的云宝新增了一笔收入！点此查看谁帮我在赚钱";
//    private static final String msgParentContent = "恭喜您！您的伙伴%s帮您赚取了%.2f元！更多热销产品在线，快去分享给小伙伴一起交流云宝心得！";
//
//
//    public void pay2(Fee fee)
//    {
//        int status = 9;
//
//        try
//        {
//            String productName = fee.getMemo();
//            Long bizId = null;
//            Long productId = null;
//            double premium = 0;
//            String fromUserName = "";
//
//            if((fee.getType() == 1 || fee.getType() == 2) && fee.getExtraInfoJson() != null){
//                productName = fee.getExtraInfoJson().getString("productName");
//                bizId = fee.getExtraInfoJson().getLong("policyId");
//                productId = fee.getExtraInfoJson().getLong("productId");
//                premium = fee.getExtraInfoJson().getDouble("premium");
//                fromUserName = fee.getExtraInfoJson().getString("fromUserName");
//                fromUserName = "null".equals(fromUserName) ? "" : fromUserName;
//            }
//
//            JSONObject json = JSON.parseObject(iybPay.pay(fee.getUserId(), fee.getType(), fee.getUnit(), fee.getAmount(), productName, fee.getFreeze(), fee.getMemo(), bizId, fee.getBizNo(), fee.getFromUserId(), productId, premium));
//            if (json != null && json.getBoolean("isSuccess")){
//                status = 1;
//                // 佣金推送消息
//                if(fee.getType() == 1){
//                    String r = pushMsg.send(String.valueOf(fee.getUserId()), "tips", "self", "提醒", msgTitle, String.format(msgContent, fee.getAmount()), null);
//                    if(r == null){
//                        System.out.println("佣金消息推送异常：" + fee.getId());
//                    }
//                }else if(fee.getType() == 2){
//                    String r = pushMsg.send(String.valueOf(fee.getUserId()), "tips", "self", "提醒", msgParentTitle, String.format(msgParentContent, fromUserName, fee.getAmount()), null);
//                    if(r == null){
//                        System.out.println("佣金消息推送异常：" + fee.getId());
//                    }
//                }
//            }
//        }
//        catch (Exception e)
//        {
//            Log.alert(e);
//        }
//
//        feeDao.pay(fee.getId(), status, new Date());
//    }