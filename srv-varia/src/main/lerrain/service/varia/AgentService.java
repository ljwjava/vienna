package lerrain.service.varia;

import com.alibaba.fastjson.JSONObject;
import lerrain.service.common.Log;
import lerrain.tool.Common;
import lerrain.tool.Disk;
import lerrain.tool.Network;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;

@Service
public class AgentService
{
	@Autowired
	AgentDao agentDao;

	OCR ocr = new OCR();

	static int t1 = 0, t2 = 0;

//	String url = "http://100.112.33.94:4846/iircirc";
	String url = "http://iir.circ.gov.cn/web";
	String cookie = "UM_distinctid=163fd40f676288-0c73c58445e894-17356953-1aeaa0-163fd40f678d11; CNZZDATA1619462=cnzz_eid%3D1132355749-1528959915-%26ntime%3D1529476557; AlteonP=ANb/I8gTFAob0dYT/x75SQ$$; JSESSIONID=TTTTTT:148amfs12";
	//String cookie2 = "UM_distinctid=163fd40f676288-1c73c58445e894-17356953-1aeaa0-163fd40f678d11; CNZZDATA1619462=cnzz_eid%3D1132355749-2528959915-%26ntime%3D1529476557; AlteonP=CNb/I8gTFAob0dYT/x75SQ$$; JSESSIONID=0001uvrf0eg6ogEZBDyyDtytdg0:148amfs12";

	RequestConfig requestConfig = RequestConfig.custom()
			.setConnectTimeout(5000).setConnectionRequestTimeout(5000)
			.setSocketTimeout(5000).build();


	public Map find(String certNo, String name)
	{
		certNo = certNo.substring(certNo.length() - 4);
		Map m = agentDao.find(certNo, name);

		if (m == null)
		{
			String res = null;

			try
			{
				m = check(certNo, name);
			}
			catch (Exception e)
			{
				Log.info(e.toString());

				try
				{
					Thread.sleep(500);
					m = check(certNo, name);
				}
				catch (Exception e1)
				{
					Log.info(e.toString());

					try
					{
						Thread.sleep(500);
						m = check(certNo, name);
					}
					catch (Exception e2)
					{
						res = e.getMessage();
					}
				}
			}

			Map m1 = null;
			try
			{
				m1 = check2(certNo, name);
			}
			catch (Exception e)
			{
				Log.info(e.toString());

				try
				{
					Thread.sleep(500);
					m1 = check2(certNo, name);
				}
				catch (Exception e1)
				{
					Log.info(e.toString());

					try
					{
						Thread.sleep(500);
						m1 = check2(certNo, name);
					}
					catch (Exception e2)
					{
						res = e.getMessage();
					}
				}
			}

			if (m1 != null)
			{
				if (m == null)
				{
					m = m1;
				}
				else if (m1 != null)
				{
					m.putAll(m1);
				}

				if (Common.isEmpty(m.get("certfiNo")))
					m.put("certfiNo", m.get("certfiNo2"));
			}

			if (m != null)
			{
				agentDao.save(m, 1);
			}
			else
			{
				Map mm = new HashMap();
				mm.put("certNo", certNo);
				mm.put("name", name);
				mm.put("file", res);

				agentDao.save(mm, 0);
			}
		}

		return m;
	}

	public Map check(String certNo, String name) throws Exception
	{
		String cookie = this.cookie.replace("TTTTTT", UUID.randomUUID().toString().replaceAll("-", ""));
		Log.info("checking -- <" + certNo + "> " + name + " -- " + t1 + " -- " + String.format("%.2f", t2 * 100.0f / t1) + "%");

		HttpClient client = HttpClients.createDefault();

		HttpGet httpGet = new HttpGet(url + "/servlet/ValidateCode?time=123");
		httpGet.setHeader("Cookie", cookie);

		httpGet.setConfig(requestConfig);

		HttpResponse response =  client.execute(httpGet);
		HttpEntity entity1 = response.getEntity();

		String code = "";
		try (InputStream io1 =  entity1.getContent())
		{
			code = ocr.scan(io1);
		}

		HttpPost httpPost = new HttpPost(url + "/baoxyx!searchInfoBaoxyx.html");
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		list.add(new BasicNameValuePair("id_card", certNo.trim()));
		list.add(new BasicNameValuePair("name", name.trim()));
		list.add(new BasicNameValuePair("certificate_code", ""));
		list.add(new BasicNameValuePair("evelop_code", ""));
		list.add(new BasicNameValuePair("valCode", code));

		UrlEncodedFormEntity uyrlEntity = new UrlEncodedFormEntity(list, "GB2312");
		httpPost.setEntity(uyrlEntity);
		httpPost.setHeader("Cookie", cookie);

		httpPost.setConfig(requestConfig);

		response =  client.execute(httpPost);

		byte[] bb = null;
		entity1 = response.getEntity();
		try (InputStream io1 =  entity1.getContent())
		{
			bb = Common.byteOf(io1);
		}

		String s = new String(bb, "GB2312");

		if (s.indexOf("apologise") >= 0)
			s = new String(bb, "utf-8");

		t1++;
		t2++;

		if (s.indexOf("没有找到符合条件的记录") >= 0)
			return null;

//		File dir = new File("x:/1/agent/" + certNo);
//		dir.mkdirs();
//
//		try (FileOutputStream fos = new FileOutputStream(new File("x:/1/agent/" + certNo + "/" + name + ".html")))
//		{
//			fos.write(string1.getBytes("GBK"));
//		}

		if (s.indexOf("验证码") >= 0)
		{
			t2--;
		}

		JSONObject res = new JSONObject();

		int p1 = s.indexOf("<th>姓");
		int p4 = s.indexOf("名</th>");
		if (p1 < 0 || p4 < 0)
			throw new RuntimeException(s);

		int p2 = s.indexOf("<td>", p1);
		int p3 = s.indexOf("</td>", p2);
		res.put("name", s.substring(p2 + 4, p3).trim());

		p1 = p3;
		p2 = s.indexOf("<td>", p1);
		p3 = s.indexOf("</td>", p2);
		res.put("gender", s.substring(p2 + 4, p3).trim());

		p1 = p3;
		p2 = s.indexOf("<td>", p1);
		p3 = s.indexOf("</td>", p2);
		res.put("certNo", s.substring(p2 + 4, p3).trim());

		p1 = p3;
		p2 = s.indexOf("<td>", p1);
		p3 = s.indexOf("</td>", p2);
		res.put("certfiNo", s.substring(p2 + 4, p3).trim());

		p1 = p3;
		p2 = s.indexOf("<td>", p1);
		p3 = s.indexOf("</td>", p2);
		res.put("certfiStatus", s.substring(p2 + 4, p3).trim());

		p1 = p3;
		p2 = s.indexOf("<td>", p1);
		p3 = s.indexOf("</td>", p2);

		p1 = p3;
		p2 = s.indexOf("<td>", p1);
		p3 = s.indexOf("</td>", p2);
		res.put("bizNo", s.substring(p2 + 4, p3).trim());

		p1 = p3;
		p2 = s.indexOf("<td>", p1);
		p3 = s.indexOf("</td>", p2);
		res.put("bizStatus", s.substring(p2 + 4, p3).trim());

		p1 = p3;
		p2 = s.indexOf("<td>", p1);
		p3 = s.indexOf("</td>", p2);
		res.put("validDate", s.substring(p2 + 4, p3).trim());

		p1 = p3;
		p2 = s.indexOf("<td>", p1);
		p3 = s.indexOf("</td>", p2);
		res.put("bizScope", s.substring(p2 + 4, p3).trim());

		p1 = p3;
		p2 = s.indexOf("<td>", p1);
		p3 = s.indexOf("</td>", p2);
		res.put("bizArea", s.substring(p2 + 4, p3).trim());

		p1 = p3;
		p2 = s.indexOf("<td>", p1);
		p3 = s.indexOf("</td>", p2);
		res.put("company", s.substring(p2 + 4, p3).trim());

		res.put("file", s);

		return res;
	}

	public synchronized Map check2(String certNo, String name) throws Exception
	{
		String cookie2 = this.cookie.replace("TTTTTT", UUID.randomUUID().toString().replaceAll("-", ""));
		Log.info("checking2 -- <" + certNo + "> " + name + " -- " + t1 + " -- " + String.format("%.2f", t2 * 100.0f / t1) + "%");

		HttpClient client = HttpClients.createDefault();

		HttpGet HttpGet = new HttpGet(url + "/servlet/ValidateCode?time=123");
		HttpGet.setHeader("Cookie", cookie2);
//		HttpGet.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
//		HttpGet.setHeader("Accept-Encoding", "gzip, deflate");
//		HttpGet.setHeader("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8");
//		HttpGet.setHeader("Cache-Control", "max-age=0");
//		HttpGet.setHeader("Connection", "keep-alive");
//		HttpGet.setHeader("Host", "iir.circ.gov.cn");
//		HttpGet.setHeader("Upgrade-Insecure-Requests", "1");
//		HttpGet.setHeader("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 11_0 like Mac OS X) AppleWebKit/604.1.38 (KHTML, like Gecko) Version/11.0 Mobile/15A372 Safari/604.1");

		HttpGet.setConfig(requestConfig);

		HttpResponse response =  client.execute(HttpGet);
		HttpEntity entity1 = response.getEntity();

//		String str = Network.request(url + "/servlet/ValidateCode?time=123");
//		System.out.println(str);

		String code = "";
		try (InputStream io1 =  entity1.getContent())
		{
//			byte[] bb = Common.byteOf(io1);
//			System.out.println(new String(bb));
			code = ocr.scan(io1);
		}

		HttpPost httpPost = new HttpPost(url  + "/baoxyx!searchInfo.html?time=" + System.currentTimeMillis());
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		list.add(new BasicNameValuePair("id_card", certNo.trim()));
		list.add(new BasicNameValuePair("name", name.trim()));
		list.add(new BasicNameValuePair("certificate_code", ""));
		list.add(new BasicNameValuePair("valCode", code));

		UrlEncodedFormEntity uyrlEntity = new UrlEncodedFormEntity(list, "GB2312");
		httpPost.setEntity(uyrlEntity);
		httpPost.setHeader("Cookie", cookie2);

		httpPost.setConfig(requestConfig);

		response =  client.execute(httpPost);

		byte[] bb;
		entity1 = response.getEntity();
		try (InputStream io1 =  entity1.getContent())
		{
			bb = Common.byteOf(io1);
		}

		String s = new String(bb, "GB2312");

//		System.out.println(code);
//		System.out.println(s);

		if (s.indexOf("apologise") >= 0)
			s = new String(bb, "utf-8");

		t1++;
		t2++;

		if (s.indexOf("没有找到符合条件的记录") >= 0)
			return null;

//		File dir = new File("x:/1/agent/" + certNo);
//		dir.mkdirs();
//
//		try (FileOutputStream fos = new FileOutputStream(new File("x:/1/agent/" + certNo + "/" + name + ".html")))
//		{
//			fos.write(string1.getBytes("GBK"));
//		}

		if (s.indexOf("验证码") >= 0)
		{
			t2--;
		}

		JSONObject res = new JSONObject();

		int p1 = s.indexOf("<th>姓");
		int p4 = s.indexOf("名</th>");
		if (p1 < 0 || p4 < 0)
			throw new RuntimeException(s);

		int p2 = s.indexOf("<td>", p1);
		int p3 = s.indexOf("</td>", p2);
		res.put("name", s.substring(p2 + 4, p3).trim());

		p1 = p3;
		p2 = s.indexOf("<td>", p1);
		p3 = s.indexOf("</td>", p2);
		res.put("gender", s.substring(p2 + 4, p3).trim());

		p1 = p3;
		p2 = s.indexOf("<td>", p1);
		p3 = s.indexOf("</td>", p2);
		res.put("certNo", s.substring(p2 + 4, p3).trim());

		p1 = p3;
		p2 = s.indexOf("<td>", p1);
		p3 = s.indexOf("</td>", p2);
		res.put("certfiType", s.substring(p2 + 4, p3).trim());

		p1 = p3;
		p2 = s.indexOf("<td>", p1);
		p3 = s.indexOf("</td>", p2);
		res.put("certfiScope", s.substring(p2 + 4, p3).trim());

		p1 = p3;
		p2 = s.indexOf("<td>", p1);
		p3 = s.indexOf("</td>", p2);
		res.put("certfiNo2", s.substring(p2 + 4, p3).trim());

		p1 = p3;
		p2 = s.indexOf("<td>", p1);
		p3 = s.indexOf("</td>", p2);

		p1 = p3;
		p2 = s.indexOf("<td>", p1);
		p3 = s.indexOf("</td>", p2);
		res.put("certfiValidDate", s.substring(p2 + 4, p3).trim());

		res.put("file2", s);

		return res;
	}

}
