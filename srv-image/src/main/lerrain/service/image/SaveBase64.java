package lerrain.service.image;

import lerrain.tool.Common;
import lerrain.tool.Disk;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * 保存base64文件
 */
@Component
public class SaveBase64
{
    @Value("${path.images}")
    String imagesRoot;

    public Object run(Object[] v)
    {
        String s = "";  // base64串
        if(v == null || v.length < 2 || Common.isEmpty(v[1])){
            System.out.println("save_base64 - 缺少参数(base64串<含前缀>, 相对路径)");
            return null;
        }

        s = String.valueOf(v[0]);
        String tp = s.substring(0, s.indexOf("base64,")+6);
        s = s.substring(s.indexOf("base64,")+7);
        String type = ".jpg";   // 文件类型后缀（图片）
        if(tp.indexOf("png") >= 0){
            type = ".png";
        }   // tp.indexOf("jpg") >= 0 || tp.indexOf("jpeg") >= 0

        String fileName = System.currentTimeMillis() + type;    // 文件名：xxx.jpg
        String path = imagesRoot + Common.getString(new Date(),"/yyyy/MM/dd/");


        return path + fileName;
    }

    public static void main(String[] args) throws IOException {
        String s = Common.encodeBase64(Disk.load(new File("D:\\work\\zhongan\\需求文档\\其他需求\\script_20171127平安驾乘险接口测试\\界面图片\\pingan_logo_607x100.png"))).replaceAll("\r\n", "");
        System.out.println(s);

        s = "data:image/png;base64," + s;
        System.out.println(s);
        System.out.println(s.substring(0, s.indexOf("base64,")+6));
        System.out.println(s.substring(s.indexOf("base64,")+7));

        System.out.println(Common.getString(new Date(),"/yyyy/MM/dd/"));

//        Disk.saveToDisk(new ByteArrayInputStream(Common.decodeBase64ToByte(s)), new File("D:\\work\\zhongan\\需求文档\\其他需求\\script_20171127平安驾乘险接口测试\\界面图片\\pingan_logo_607x1002.png"));
    }
}
