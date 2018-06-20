package lerrain.service.varia;

import com.alibaba.fastjson.JSONObject;
import lerrain.tool.Common;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FormTest
{
    public static void main(String[] arg) throws Exception
    {
        AgentService as = new AgentService();
        Map m = as.check2("0627", "宋惠玲");
        System.out.println(m);
    }
}
