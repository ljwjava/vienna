package lerrain.service.env.function;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lerrain.service.common.Log;
import lerrain.service.common.ServiceMgr;
import lerrain.tool.Common;
import lerrain.tool.formula.Factors;
import lerrain.tool.formula.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * Created by lerrain on 2017/5/19.
 */
@Service
public class RequestPost implements Function
{
    @Autowired
    ServiceMgr serviceMgr;

    @Override
    public Object run(Object[] objects, Factors factors)
    {
        int time = objects.length > 2 ? Common.intOf(objects[2], 10000) : 10000;
        String method = objects.length > 3 ? ("GET".equalsIgnoreCase(Common.trimStringOf(objects[3])) ? "GET" : "POST") : "POST";
        String contentType = objects.length > 4 ? Common.trimStringOf(objects[4]) : "json";

        if("form".equalsIgnoreCase(contentType)){
            return requestForm((String)objects[0], (String)objects[1], time, method);
        }else{
            return request((String)objects[0], (String)objects[1], time, method);
        }
    }

//    public String request(String urlstr, String req, int timeout, String method)
//    {
//        JSONObject json = new JSONObject();
//        json.put("url", urlstr);
//        json.put("value", req);
//        json.put("timeout", timeout);
//        json.put("method", method);
//
//        return serviceMgr.reqStr("vienna", "request", json.toJSONString());
//    }

    public String request(String urlstr, String req, int timeout, String method)
    {
        String res = null;

        Log.info("req: " + urlstr + " << " + req);

        HttpURLConnection conn = null;
        try
        {
            URL url = new URL(urlstr);
            conn = (HttpURLConnection)url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setReadTimeout(timeout);
            conn.setConnectTimeout(timeout);
            conn.setRequestMethod(method);
            conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");

            if (conn instanceof HttpsURLConnection)
            {
                SSLContext sslContext = SSLContext.getInstance("SSL");
                TrustManager[] tm = {new MyX509TrustManager()};
                sslContext.init(null, tm, new java.security.SecureRandom());;
                SSLSocketFactory ssf = sslContext.getSocketFactory();
                ((HttpsURLConnection) conn).setSSLSocketFactory(ssf);
            }

            byte[] info = req == null ? null : req.getBytes("UTF-8");
            if (info != null)
                conn.setRequestProperty("Content-Length", String.valueOf(info.length));

            conn.connect();

            if(!"GET".equalsIgnoreCase(method)){
                try (OutputStream out = conn.getOutputStream())
                {
                    if (info != null)
                        out.write(info);
                    out.flush();
                }
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (InputStream in = conn.getInputStream())
            {
                byte[] b = new byte[1024];
                int c = 0;
                while ((c = in.read(b)) >= 0)
                {
                    baos.write(b, 0, c);
                }
            }
            baos.close();

            res = baos.toString("utf-8");

            Log.info("res: " + urlstr + " >> " + res);
        }
        catch (Exception e)
        {
            Log.alert("res: " + urlstr + " >> " + e.getMessage());

            throw new RuntimeException(e);
        }
        finally
        {
            if (conn != null)
                conn.disconnect();
        }

        return res;
    }

    public String requestForm(String urlstr, String req, int timeout, String method)
    {
        String res = null;

        Log.info("req: " + urlstr + " << " + req);

        HttpURLConnection conn = null;
        try
        {
            URL url = new URL(urlstr);
            conn = (HttpURLConnection)url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setReadTimeout(timeout);
            conn.setConnectTimeout(timeout);
            conn.setRequestMethod(method);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

            if (conn instanceof HttpsURLConnection)
            {
                SSLContext sslContext = SSLContext.getInstance("SSL");
                TrustManager[] tm = {new MyX509TrustManager()};
                sslContext.init(null, tm, new java.security.SecureRandom());;
                SSLSocketFactory ssf = sslContext.getSocketFactory();
                ((HttpsURLConnection) conn).setSSLSocketFactory(ssf);
            }

            StringBuffer sbParams = new StringBuffer();
            JSONObject jo = JSONObject.parseObject(req);
            if (jo != null && jo.size() > 0) {
                for (String k : jo.keySet()) {
                    sbParams.append(k);
                    sbParams.append("=");
                    sbParams.append(URLEncoder.encode(String.valueOf(jo.get(k)), "UTF-8"));
                    sbParams.append("&");
                }
            }

            byte[] info = sbParams.length() > 0 ? null : sbParams.toString().getBytes("UTF-8");
            if (info != null) {
                conn.setRequestProperty("Content-Length", String.valueOf(info.length));
            }
            conn.connect();

            if(!"GET".equalsIgnoreCase(method)){
                // 获取URLConnection对象对应的输出流
                try (OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream(), "UTF-8")) {
                    if (sbParams != null && sbParams.length() > 0) {
                        // 发送请求参数
                        osw.write(sbParams.substring(0, sbParams.length() - 1));
                        // flush输出流的缓冲
                        osw.flush();
                    }
                }
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (InputStream in = conn.getInputStream())
            {
                byte[] b = new byte[1024];
                int c = 0;
                while ((c = in.read(b)) >= 0)
                {
                    baos.write(b, 0, c);
                }
            }
            baos.close();
            res = baos.toString("utf-8");

            Log.info("res: " + urlstr + " >> " + res);
        }
        catch (Exception e)
        {
            Log.alert("res: " + urlstr + " >> " + e.getMessage());

            throw new RuntimeException(e);
        }
        finally
        {
            if (conn != null)
                conn.disconnect();
        }

        return res;
    }

    private static class MyX509TrustManager implements X509TrustManager
    {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType)  throws CertificateException
        {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException
        {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers()
        {
            return null;
        }
    }
}
