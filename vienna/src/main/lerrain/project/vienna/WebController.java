package lerrain.project.vienna;

import com.alibaba.fastjson.JSONObject;
import lerrain.service.common.Log;
import lerrain.tool.Disk;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;

@Controller
public class WebController
{
    @Autowired
    WebService jsxSrv;

    @RequestMapping("/reset")
    @ResponseBody
    @CrossOrigin
    public String reset()
    {
        jsxSrv.reset();
        return "success";
    }

    @RequestMapping("/{path}/{file}.{type:web|mobile}")
    @CrossOrigin
    public void web2(@PathVariable String path, @PathVariable String file, @PathVariable String type, HttpServletResponse resp)
    {
//        Map m = jsxSrv.run(file);

        try (OutputStream os = resp.getOutputStream())
        {
            String full = path + "/" + file;
            os.write(jsxSrv.getCache(type, "header2"));

//            if (m != null)
//            {
//                String title = (String)m.get("TITLE");
//                os.write
//            }

            if (jsxSrv.exists(path + "/" + path + ".css"))
                os.write(String.format("<link href=\"%s.css\" rel=\"stylesheet\">\n", path).getBytes());
            else
                os.write(String.format("<link href=\"../css/%s.css\" rel=\"stylesheet\">\n", type).getBytes());

            if (jsxSrv.exists(full + ".css"))
                os.write(String.format("<link href=\"%s.css\" rel=\"stylesheet\">\n", file).getBytes());

            if (jsxSrv.exists(path + "/" + file + ".head"))
                os.write(Disk.load(jsxSrv.getFile(path + "/" + file + ".head")));

            if (jsxSrv.exists(full + ".js"))
            {
                os.write(String.format("<script src=\"%s.js\"></script>\n", file).getBytes());
            }
            else if (jsxSrv.exists(full + ".jsx"))
            {
                os.write("<script src=\"https://static.zhongan.com/website/health/iyb/resource/activity/gpo/common/js/react/react.js\"></script>\n".getBytes());
                os.write("<script src=\"https://static.zhongan.com/website/health/iyb/resource/activity/gpo/common/js/react/react-dom.js\"></script>\n".getBytes());
                os.write("<script src=\"https://static.zhongan.com/website/health/iyb/resource/activity/gpo/common/js/browser/browser.min.js\"></script>\n".getBytes());

                os.write("<script type=\"text/babel\">".getBytes());
                os.write(jsxSrv.loadJsx(full + ".jsx").getBytes("utf-8"));
                os.write("</script>".getBytes());
            }

            os.write(jsxSrv.getCache(type, "center2"));

            if (jsxSrv.exists(full + ".html"))
                os.write(Disk.load(jsxSrv.getFile(full + ".html")));

            os.write(jsxSrv.getCache(type, "footer2"));
        }
        catch (Exception e)
        {
            Log.debug(e);
        }
    }

    @RequestMapping("/{file}.{type:web|mobile}")
    @CrossOrigin
    public void web(@PathVariable String file, @PathVariable String type, HttpServletResponse resp)
    {
        try (OutputStream os = resp.getOutputStream())
        {
            os.write(jsxSrv.getCache(type, "header"));
            os.write(String.format("<link href=\"./css/%s.css\" rel=\"stylesheet\">\n", type).getBytes());

            if (jsxSrv.exists(file + ".css"))
                os.write(String.format("<link href=\"%s.css\" rel=\"stylesheet\">\n", file).getBytes());

            if (jsxSrv.exists(file + ".head"))
                os.write(Disk.load(jsxSrv.getFile(file + ".head")));

            if (jsxSrv.exists(file + ".js"))
            {
                os.write(String.format("<script src=\"%s.js\"></script>\n", file).getBytes());
            }
            else if (jsxSrv.exists(file + ".jsx"))
            {
                os.write("<script src=\"https://static.zhongan.com/website/health/iyb/resource/activity/gpo/common/js/react/react.js\"></script>\n".getBytes());
                os.write("<script src=\"https://static.zhongan.com/website/health/iyb/resource/activity/gpo/common/js/react/react-dom.js\"></script>\n".getBytes());
                os.write("<script src=\"https://static.zhongan.com/website/health/iyb/resource/activity/gpo/common/js/browser/browser.min.js\"></script>\n".getBytes());

                os.write("<script type=\"text/babel\">".getBytes());
                os.write(jsxSrv.loadJsx(file + ".jsx").getBytes("utf-8"));
                os.write("</script>".getBytes());
            }

            os.write(jsxSrv.getCache(type, "center"));

            if (jsxSrv.exists(file + ".html"))
                os.write(Disk.load(jsxSrv.getFile(file + ".html")));

            os.write(jsxSrv.getCache(type, "footer"));
        }
        catch (Exception e)
        {
            Log.debug(e);
        }
    }
}
