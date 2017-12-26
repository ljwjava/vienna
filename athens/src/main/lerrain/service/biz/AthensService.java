package lerrain.service.biz;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lerrain.service.common.Log;
import lerrain.service.common.ServiceMgr;
import lerrain.tool.script.Script;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.ImportResource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class AthensService
{
	@Autowired
	EnvService envSrv;

	@Autowired
	GatewayService gatewaySrv;

	@Autowired
	KeyValService kvSrv;

	@Value("${env}")
	String srvEnv;

	@Autowired
	ServiceMgr sv;

	@PostConstruct
	public void reset()
	{
		sv.setLog("sale", 2);
		sv.setLog("lifeins", 2);
		sv.setLog("proposal", 2);

		Script.STACK_MESSAGE = !("prd".equalsIgnoreCase(srvEnv) || "uat".equalsIgnoreCase(srvEnv));
		Log.info("ENV: " + srvEnv + ", log of formula stack: " + Script.STACK_MESSAGE);
		Log.EXCEPTION_STACK = false;

		if ("prd".equalsIgnoreCase(srvEnv))
		{
			Log.resetWriteLevel("info,error,alert");
		}

		JSON.DEFAULT_GENERATE_FEATURE |= SerializerFeature.DisableCircularReferenceDetect.getMask();

		gatewaySrv.reset();
		envSrv.reset();

		kvSrv.restore();
	}

	public void onClose()
	{
		kvSrv.store();
	}
}
