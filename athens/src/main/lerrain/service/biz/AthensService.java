package lerrain.service.biz;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lerrain.service.common.Log;
import lerrain.service.common.ServiceMgr;
import lerrain.service.env.EnvService;
import lerrain.service.env.KeyValService;
import lerrain.service.task.TaskService;
import lerrain.tool.script.Script;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

	@Autowired
	TaskService taskSrv;

	@PostConstruct
	public void reset()
	{
		sv.setLog("sale", 2);
		sv.setLog("lifeins", 2);
		sv.setLog("proposal", 2);
		sv.setLog("commission", 2);

		Script.STACK_MESSAGE = true; //!("prd".equalsIgnoreCase(srvEnv));
		Log.EXCEPTION_STACK = false;
		Log.info("ENV: " + srvEnv + ", log of formula stack: " + Script.STACK_MESSAGE);

		if ("prd".equalsIgnoreCase(srvEnv))
		{
			Log.resetWriteLevel("info,error,alert");
		}

		JSON.DEFAULT_GENERATE_FEATURE |= SerializerFeature.DisableCircularReferenceDetect.getMask();

		envSrv.reset();
		gatewaySrv.reset(envSrv);

		taskSrv.reset(envSrv);
	}

	public void onClose()
	{
		kvSrv.store();
	}
}
