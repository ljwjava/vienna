package lerrain.service.image;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lerrain.service.image.util.FileTypeEnum;
import lerrain.tool.Common;
import lerrain.tool.Disk;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Annotate lyx
 */
@Controller
@RequestMapping("/image")
public class ImageController
{
    @Value("${path.images}")
    String imagesDir;

    /**
     * 创建并返回file
     * @param dir - dir不存在则会创建
     * @param fileName
     * @return
     */
    private File createFile(String dir, String fileName){
        File dirf = new File(dir);
        if(!dirf.exists()){
            dirf.mkdirs();
        }

        return Disk.fileOf(dir, fileName);
    }

    /**
     * 上传图片影像
     * 报文格式
      {
		imageDatas: 
		[{
			"class": "apply_img", //目录名称
			"data": base64串
		}]
	
	  }
     * @return
     */
    @RequestMapping("/upload/base64.json")
    @ResponseBody
    @CrossOrigin
    public JSONObject uploadImage4Base64(@RequestBody JSONObject request) throws IOException {
        JSONObject res = new JSONObject();
        res.put("result", "success");

        JSONArray imgDatas = request.getJSONArray("imageDatas");
        if(imgDatas != null && imgDatas.size() > 0){
            List<String> urls = new ArrayList<String>();
            String path = Common.getString(new Date(),"/yyyy/MM/dd/HH/");

            for(int i = 0; i < imgDatas.size(); i++){
                JSONObject jo = imgDatas.getJSONObject(i);
                String imgData = jo.getString("data");  // base64数据
                String imgClass = jo.getString("class");    // 图片用途归类
                imgClass = Common.isEmpty(imgClass) ? "other" : imgClass;
                if(Common.isEmpty(imgData)){
                    continue;
                }
                String tp = imgData.substring(0, imgData.indexOf("base64,")+6);
                imgData = imgData.substring(imgData.indexOf("base64,")+7);
                String type = ".jpg";   // 文件类型后缀（图片）
                if(tp.indexOf("png") >= 0){
                    type = ".png";
                }
                String fileName = System.currentTimeMillis() + String.format("%05.0f", Math.ceil(Math.random()*10000)) + type;
                Disk.saveToDisk(new ByteArrayInputStream(Common.decodeBase64ToByte(imgData)), createFile(imagesDir + "temp/" + imgClass + path, fileName));

                urls.add("temp/" + imgClass + path + fileName);
            }

            res.put("content", urls);
        }else{
            res.put("result", "fail");
        }

        return res;
    }



    /**
     * 获取(下载?)图片
     * 请求格式：http://ip:端口/image/load/temp/目录名称/2018/07/03/20/153062017552303201.jpg
     */
    @RequestMapping("/load/**")
    @CrossOrigin
    public void loadImage(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String uri = request.getRequestURI();
        String pathName = uri.substring(uri.indexOf("/image/load/") + 12);
        String type = "image/png";
        if(pathName.lastIndexOf(".") >= 0){
            String t = pathName.substring(pathName.lastIndexOf("."));
            type = FileTypeEnum.FILETYPE.getMime(t);
        }
        type = type == null ? "image/png" : type;

        response.setContentType(type);
        OutputStream os = response.getOutputStream();
        os.write(Disk.load(new File(imagesDir + pathName)));
    }

    /**
     * 获取图片(base64字符串形式)
     * 请求格式：http://ip:端口/image/load/base64/temp/目录名称/2018/07/03/20/153062017552303201.jpg
     */
    @RequestMapping("/load/base64/**")
    @CrossOrigin
    public void loadImageBase64(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String uri = request.getRequestURI();
        String pathName = uri.substring(uri.indexOf("/image/load/base64/") + 19);
        String type = "image/png";
        if(pathName.lastIndexOf(".") >= 0){
            String t = pathName.substring(pathName.lastIndexOf("."));
            type = FileTypeEnum.FILETYPE.getMime(t);
        }
        type = type == null ? "image/png" : type;
        response.getOutputStream().write(("data:"+type+";base64," + Common.encodeBase64(Disk.load(new File(imagesDir + pathName))).replaceAll("\r\n", "")).getBytes());
    }

    /**
     * 获取图片base64字符串
     * 请求格式：http://ip:端口/image/get/base64.json?pathName=temp/目录名称/2018/07/03/20/153062017552303201.jpg
     * 返回JSON报文
     */
    @RequestMapping("/get/base64.json")
    @ResponseBody
    @CrossOrigin
    public JSONObject getImageBase64(@RequestBody JSONObject req) throws IOException {
        JSONObject res = new JSONObject();
        res.put("result", "success");

        String pathName = req.getString("pathName");
        if(Common.isEmpty(pathName)){
            res.put("result", "fail");
            return res;
        }
        String type = "image/png";
        if(pathName.lastIndexOf(".") >= 0){
            String t = pathName.substring(pathName.lastIndexOf("."));
            type = FileTypeEnum.FILETYPE.getMime(t);
        }
        type = type == null ? "image/png" : type;

        res.put("content", "data:"+type+";base64," + Common.encodeBase64(Disk.load(new File(imagesDir + pathName))).replaceAll("\r\n", ""));
        return res;
    }


}
