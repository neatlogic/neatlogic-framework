package codedriver.framework.notify.core;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import codedriver.framework.common.dto.ValueTextVo;
import codedriver.framework.notify.dto.NotifyPolicyVo;

public class NotifyPolicyFactory {
	public final static Map<Long, NotifyPolicyVo> notifyPolicyMap = new HashMap<>();
	static {
		Date currentDate = new Date();
		for(int i = 0; i < 100; i++) {
			NotifyPolicyVo notifyPolicyVo = new NotifyPolicyVo("test" + i, "");
			notifyPolicyVo.setInvokerCount(i%10);
			if(i % 2 == 0) {
				notifyPolicyVo.setHandler("codedriver.module.process.notify.handler.ProcessNotifyPolicyHandler");
				notifyPolicyVo.setFcu("linbq");
				notifyPolicyVo.setFcd(new Date(currentDate.getTime() + ((i - 99) * 1000)));
				notifyPolicyVo.setFcuName("林邦泉");
				notifyPolicyVo.setLcu("lvzk");
				notifyPolicyVo.setLcd(new Date(currentDate.getTime() + ((i - 99) * 1000)));
				notifyPolicyVo.setLcuName("吕佐康");
			}else {
				notifyPolicyVo.setHandler("codedriver.module.process.notify.handler.TestNotifyPolicyHandler");
				notifyPolicyVo.setFcu("linbq");
				notifyPolicyVo.setFcd(new Date(currentDate.getTime() + ((i - 99) * 1000)));
				notifyPolicyVo.setFcuName("林邦泉");
			}
			JSONObject configObj = new JSONObject();
			JSONArray triggerList = new JSONArray();
			for (ValueTextVo notifyTrigger : NotifyPolicyHandlerFactory.getHandler("codedriver.module.process.notify.handler.ProcessNotifyPolicyHandler").getNotifyTriggerList()) {
				JSONObject triggerObj = new JSONObject();
				triggerObj.put("trigger", notifyTrigger.getValue());
				triggerObj.put("triggerName", notifyTrigger.getText());
				triggerObj.put("notifyList", new JSONArray());
				triggerList.add(triggerObj);
			}
			configObj.put("triggerList", triggerList);
			configObj.put("paramList", new JSONArray());
			configObj.put("templateList", new JSONArray());
			notifyPolicyVo.setConfig(configObj.toJSONString());
			notifyPolicyMap.put(notifyPolicyVo.getId(), notifyPolicyVo);
		}
	}
}
