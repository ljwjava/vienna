package lerrain.service.sale.function;

import lerrain.service.common.ServiceMgr;
import lerrain.tool.formula.Factors;
import lerrain.tool.formula.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class IybFunc implements Factors
{
    @Autowired
    ServiceMgr serviceMgr;

    Map<String, Object> map = new HashMap<>();

    public IybFunc()
    {
        map.put("saveOrder", new Function() {
            @Override
            public Object run(Object[] objects, Factors factors)
            {
                return null;
            }
        });

        map.put("savePolicy", new Function()
        {
            @Override
            public Object run(Object[] objects, Factors factors)
            {
                return null;
            }
        });

        map.put("apply", new Function()
        {
            @Override
            public Object run(Object[] objects, Factors factors)
            {
                return null;
            }
        });
    }

    @Override
    public Object get(String s)
    {
        return map.get(s);
    }

/*
    private class Sofa implements Function
    {
        String privateKey = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAIL41QYJi6aNDkA471sXN4OAtr1jTleHk+1uGTuQ/DB1lvHBghfY8cFLk+wT6VRO23D0on5GyyepNgMEMHZcBBxMHh0rCozwadjcGvdYYMQ2BUEIvti7cfdfVBHj3waeEwpqMe4eLJKWHQPtTKNcMF07r4eGZPldXYbEo9xCMIOvAgMBAAECgYBuirqxV8koj5FhnyxWg6f1M+QIwRJUSjgOg4iEgAB6niUCc0LsAc06SiHVdZDP+aa5FaE4V2QMW4Mc1KJao5tLWH+SfXjbBuNrNDw2/o9mZJoFu0QXyxsbvfPQSRVSD3OnJbxrsQlvMMb2WjSgCEeHFIyOFdc43rTRjxwDyd5ogQJBAMjdCdwih+GGEqmqfIK4VAEFObNCx3pSBksR1dMBe8QxVEJjU3RURtyvYOThEL64bbiGrwtlLDORpyUwLJWAJeECQQCm7GwnczHB/J4XYHX4s0Xg0mcvhozBQ5ojulxwqP1IUbyz1mjp5JZITDdxL+fl4dR71uzHD533S7XXKs7B8LuPAkAjz2SGnpFjH6gMH5z7ISm41Nmon+s4X49HqvJYIBeUrsa630JtpujLR2ka5RU7K15EW56xhWQP/ZOzCZepP6OBAkBCl3T6NAF4sy/agZfutI4/B5E0q4fBnheDA1jXdQM+c6VFVVcbYB74DrfbhtILqpEbKn3hWACb5G9RvHkpc8FPAkApdWhq3GbSQ+nkoonWM5BaZKD+fFkKXh6MeCUfCF11aUzdWei3pgqJWP9NcYNRywkspfBqzk4F118sVbNYdfPT";

        public static void main(String[] args) throws Exception {
            String server = "https://sofa-test.iyunbao.com";
            // 业务数据
            JSONObject json = new JSONObject();
            json.put("productPackageId", "50530008");
            json.put("insuredProfession", "1-2");
            json.put("productCode", "768");
            json.put("liabilityCode", "L1003");
            json.put("amount", "100000");
            // 平台数据
            JSONObject jo = new JSONObject();
//            jo.put(Service.APPKEY, "80d8f47b98b04a3ea27c678deeac3dc0");
//            jo.put(Service.BIZCONTENT, json.toJSONString());
//            jo.put(Service.CHARSET, "UTF-8");
//            jo.put(Service.FORMAT, "json");
//            jo.put(Service.SERVICENAME, "zhongan.group.Policy.GroupPolicyInter");
//            jo.put(Service.SIGNTYPE, "RSA");
            // 1.开发者私钥加签 2.I云宝公钥验签 3.I云宝公钥加密 4.开发者私钥解密


            CipherUtil.xxx;

            jo.put(Service.SIGN, CryptUtils.sign(json.toJSONString().getBytes(),privateKey));
            jo.put(Service.TIMESTAMP, ThreadLocalDateUtils.yyyyMMddHHmmssSSS.get().format(new Date()));
            // 版本号 非必填 不填默认1.0.0
            jo.put(Service.VERSION, "1.0.0");
            try {
                System.out.println("http request : " + jo.toJSONString());
                String rsp = HttpUtils.post(server + "/open/iyb/sofa", jo.toJSONString());
                System.out.println("http response : "+rsp);
                JSONObject responseJson = JSONObject.parseObject(rsp);
                // 开发者私钥解密验签
                boolean result = CryptUtils.verifyByPrivateKey(responseJson.getString("bizContent"),privateKey,responseJson.getString("sign"));
                System.out.println("验证签名结果是："+result);
                System.out.println("业务数据为："+responseJson.getString("bizContent"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public Object run(Object[] objects, Factors factors)
        {
            return null;
        }
    }
*/

}
