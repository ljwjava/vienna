package lerrain.service.biz.function;

import lerrain.tool.formula.Factors;
import lerrain.tool.formula.Function;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

public class Md5 implements Function
{
    @Override
    public Object run(Object[] objects, Factors factors)
    {
//        return Common.md5Of(objects[0].toString());

        try
        {
            return md5Of(objects[0].toString().getBytes("UTF-8"));
        }
        catch (UnsupportedEncodingException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static String md5Of(byte[] source) {
        String s = null;
        char hexDigits[] = {
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
                'e', 'f' };
        try {
            java.security.MessageDigest md = java.security.MessageDigest
                    .getInstance("MD5");
            md.update(source);
            byte tmp[] = md.digest();
            char str[] = new char[16 * 2];
            int k = 0;
            for (int i = 0; i < 16; i++) {
                byte byte0 = tmp[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            s = new String(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }
}
