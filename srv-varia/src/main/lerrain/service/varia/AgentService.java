package lerrain.service.varia;

import com.alibaba.fastjson.JSONObject;
import lerrain.service.common.Log;
import lerrain.tool.Common;
import lerrain.tool.Disk;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AgentService
{
	@Autowired
	AgentDao agentDao;

	OCR ocr = new OCR();

	static int t1 = 0, t2 = 0;

	String url = "http://100.112.33.94:4846/iircirc";

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

				if (m.get("certfiNo") == null)
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
		Log.info("checking -- <" + certNo + "> " + name + " -- " + t1 + " -- " + String.format("%.2f", t2 * 100.0f / t1) + "%");

		HttpClient client = HttpClients.createDefault();

		HttpPost httpPost = new HttpPost(url + "/servlet/ValidateCode?time=123");
		httpPost.setHeader("Cookie", "UM_distinctid=163fd40a6ac9e4-053dbfa72ae554-c343567-1aeaa0-163fd40a6ad49e; AlteonP=ArxhTcgTFAqX2roZmBZ4Dg$$; JSESSIONID=0000xYdv733KExpb-SW1EaOb-jO:148amfs12; CNZZDATA1619462=cnzz_eid%3D932756250-1528961309-%26ntime%3D1528982943");

		HttpResponse response =  client.execute(httpPost);
		HttpEntity entity1 = response.getEntity();

		String code = "";
		try (InputStream io1 =  entity1.getContent())
		{
			code = ocr.scan(io1);
		}

		httpPost = new HttpPost(url + "/baoxyx!searchInfoBaoxyx.html");
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		list.add(new BasicNameValuePair("id_card", certNo.trim()));
		list.add(new BasicNameValuePair("name", name.trim()));
		list.add(new BasicNameValuePair("certificate_code", ""));
		list.add(new BasicNameValuePair("evelop_code", ""));
		list.add(new BasicNameValuePair("valCode", code));

		UrlEncodedFormEntity uyrlEntity = new UrlEncodedFormEntity(list, "GB2312");
		httpPost.setEntity(uyrlEntity);
		httpPost.setHeader("Cookie", "UM_distinctid=163fd40a6ac9e4-053dbfa72ae554-c343567-1aeaa0-163fd40a6ad49e; AlteonP=ArxhTcgTFAqX2roZmBZ4Dg$$; JSESSIONID=0000xYdv733KExpb-SW1EaOb-jO:148amfs12; CNZZDATA1619462=cnzz_eid%3D932756250-1528961309-%26ntime%3D1528982943");

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

	public Map check2(String certNo, String name) throws Exception
	{
		Log.info("checking2 -- <" + certNo + "> " + name + " -- " + t1 + " -- " + String.format("%.2f", t2 * 100.0f / t1) + "%");

		HttpClient client = HttpClients.createDefault();

		HttpPost httpPost = new HttpPost(url + "/web/servlet/ValidateCode?time=123");
		httpPost.setHeader("Cookie", "UM_distinctid=163fd40a6ac9e4-053dbfa72ae554-c343567-1aeaa0-163fd40a6ad49e; AlteonP=ArxhTcgTFAqX2roZmBZ4Dg$$; JSESSIONID=0000xYdv733KExpb-SW1EaOb-jO:148amfs12; CNZZDATA1619462=cnzz_eid%3D932756250-1528961309-%26ntime%3D1528982943");

		HttpResponse response =  client.execute(httpPost);
		HttpEntity entity1 = response.getEntity();

		String code = "";
		try (InputStream io1 =  entity1.getContent())
		{
			code = ocr.scan(io1);
		}

		httpPost = new HttpPost(url  + "/web/baoxyx!searchInfo.html");
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		list.add(new BasicNameValuePair("id_card", certNo.trim()));
		list.add(new BasicNameValuePair("name", name.trim()));
		list.add(new BasicNameValuePair("certificate_code", ""));
		list.add(new BasicNameValuePair("valCode", code));

		UrlEncodedFormEntity uyrlEntity = new UrlEncodedFormEntity(list, "GB2312");
		httpPost.setEntity(uyrlEntity);
		httpPost.setHeader("Cookie", "UM_distinctid=163fd40a6ac9e4-053dbfa72ae554-c343567-1aeaa0-163fd40a6ad49e; AlteonP=ArxhTcgTFAqX2roZmBZ4Dg$$; JSESSIONID=0000xYdv733KExpb-SW1EaOb-jO:148amfs12; CNZZDATA1619462=cnzz_eid%3D932756250-1528961309-%26ntime%3D1528982943");

		response =  client.execute(httpPost);

		byte[] bb;
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
