/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.notify.core;

import codedriver.framework.common.constvalue.GroupSearch;
import codedriver.framework.common.constvalue.ParamType;
import codedriver.framework.common.dto.ValueTextVo;
import codedriver.framework.dto.ConditionParamVo;
import codedriver.framework.notify.constvalue.CommonNotifyParam;
import codedriver.framework.notify.dto.NotifyTriggerTemplateVo;
import codedriver.framework.notify.dto.NotifyTriggerVo;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

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
		for (CommonNotifyParam param : CommonNotifyParam.values()) {
			ConditionParamVo paramVo = new ConditionParamVo();
			paramVo.setName(param.getValue());
			paramVo.setLabel(param.getText());
			paramVo.setController("input");

			paramVo.setType("common");
			ParamType paramType = param.getParamType();
			paramVo.setParamType(paramType.getName());
			paramVo.setParamTypeName(paramType.getText());
//			paramVo.setDefaultExpression(paramType.getDefaultExpression().getExpression());
//			for(Expression expression : paramType.getExpressionList()) {
//				paramVo.getExpressionList().add(new ExpressionVo(expression.getExpression(), expression.getExpressionName()));
//			}
			paramVo.setIsEditable(0);
			paramVo.setFreemarkerTemplate(param.getFreemarkerTemplate());
			resultList.add(paramVo);
		}
//	    if(StringUtils.isNotBlank(Config.HOME_URL())) {
//	        ConditionParamVo param = new ConditionParamVo();
//	        param.setName("homeUrl");
//	        param.setLabel("域名");
//	        param.setController("input");
//
//	        param.setType("common");
//	        param.setParamType(ParamType.STRING.getName());
//	        param.setParamTypeName(ParamType.STRING.getText());
//	        param.setDefaultExpression(ParamType.STRING.getDefaultExpression().getExpression());
//	        for(Expression expression : ParamType.STRING.getExpressionList()) {
//	            param.getExpressionList().add(new ExpressionVo(expression.getExpression(), expression.getExpressionName()));
//	        }
//	        param.setIsEditable(0);
//	        param.setFreemarkerTemplate("<a href=\"${homeUrl}\" target=\"_blank\"></a>");
//	        resultList.add(param);
//	    }
	    List<ConditionParamVo> mySystemParamList = mySystemParamList();
	    if(CollectionUtils.isNotEmpty(mySystemParamList)) {
	        resultList.addAll(mySystemParamList);
	    }
		return resultList;
	}
	
	protected abstract List<ConditionParamVo> mySystemParamList();

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
