package codedriver.framework.notify.core;

import java.util.ArrayList;
import java.util.List;

import codedriver.framework.notify.dto.NotifyTriggerTemplateVo;
import codedriver.framework.notify.dto.NotifyTriggerVo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;

import codedriver.framework.common.constvalue.ParamType;
import codedriver.framework.common.config.Config;
import codedriver.framework.common.constvalue.Expression;
import codedriver.framework.common.constvalue.GroupSearch;
import codedriver.framework.common.dto.ValueTextVo;
import codedriver.framework.dto.ConditionParamVo;
import codedriver.framework.dto.ExpressionVo;

public abstract class NotifyPolicyHandlerBase implements INotifyPolicyHandler{

	@Override
	public List<NotifyTriggerVo> getNotifyTriggerList() {
		return myNotifyTriggerList();
	}

	protected abstract List<NotifyTriggerVo> myNotifyTriggerList();

	/** 获取通知触发点模版列表 */
	@Override
	public List<NotifyTriggerTemplateVo> getNotifyTriggerTemplateList(NotifyHandlerType type) {
		return myNotifyTriggerTemplateList(type);
	}

	protected abstract List<NotifyTriggerTemplateVo> myNotifyTriggerTemplateList(NotifyHandlerType type);

	@Override
	public List<ValueTextVo> getParamTypeList() {
		List<ValueTextVo> resultList = new ArrayList<>();
		for(ParamType type : ParamType.values()) {
			resultList.add(new ValueTextVo(type.getName(), type.getText()));
		}
		return resultList;
	}

	@Override
	public List<ConditionParamVo> getSystemParamList() {
	    List<ConditionParamVo> resultList = new ArrayList<>();
	    if(StringUtils.isNotBlank(Config.HOME_URL())) {
	        ConditionParamVo param = new ConditionParamVo();
	        param.setName("homeUrl");
	        param.setLabel("域名");
	        param.setController("input");

	        param.setType("common");
	        param.setParamType(ParamType.STRING.getName());
	        param.setParamTypeName(ParamType.STRING.getText());
	        param.setDefaultExpression(ParamType.STRING.getDefaultExpression().getExpression());
	        for(Expression expression : ParamType.STRING.getExpressionList()) {
	            param.getExpressionList().add(new ExpressionVo(expression.getExpression(), expression.getExpressionName()));
	        }        
	        param.setIsEditable(0);
	        param.setFreemarkerTemplate("<a href=\"${homeUrl}\" target=\"_blank\"></a>");
	        resultList.add(param);
	    }	    
	    List<ConditionParamVo> mySystemParamList = mySystemParamList();
	    if(CollectionUtils.isNotEmpty(mySystemParamList)) {
	        resultList .addAll(mySystemParamList);
	    }
		return resultList;
	}
	
	protected abstract List<ConditionParamVo> mySystemParamList();

	@Override
	public final List<String> getParamNameList() {
		return getMyParamNameList();
	}

	protected abstract List<String> getMyParamNameList();

    @Override
    public List<ConditionParamVo> getSystemConditionOptionList() {
        return mySystemConditionOptionList();
    }

    protected abstract List<ConditionParamVo> mySystemConditionOptionList();
    
	@Override
	public JSONObject getAuthorityConfig() {
		JSONObject config = new JSONObject();
		List<String> excludeList = new ArrayList<>();
		config.put("excludeList", excludeList);
		List<String> includeList = new ArrayList<>();
		config.put("includeList", includeList);
		List<String> groupList = new ArrayList<>();
		groupList.add(GroupSearch.USER.getValue());
		groupList.add(GroupSearch.TEAM.getValue());
		groupList.add(GroupSearch.ROLE.getValue());
		config.put("groupList", groupList);
		myAuthorityConfig(config);
		return config;
	}
	
	protected abstract void myAuthorityConfig(JSONObject config);
}
