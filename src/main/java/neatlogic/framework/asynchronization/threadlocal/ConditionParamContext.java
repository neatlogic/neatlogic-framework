/*
 * Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package neatlogic.framework.asynchronization.threadlocal;

import org.apache.commons.collections4.MapUtils;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

public class ConditionParamContext {

	private static final ThreadLocal<ConditionParamContext> instance = new ThreadLocal<ConditionParamContext>();
	/**
	 * 参数数据
	 **/
	private JSONObject paramData = new JSONObject();
	/**
	 * 表单配置信息
	 **/
	private JSONObject formConfig;
	/**
	 * 是否需要将参数名称、表达式、值的value翻译成对应text，目前条件步骤生成活动时用到
	 **/
	private boolean translate = false;

	public static ConditionParamContext init(JSONObject _paramData) {
		ConditionParamContext context = new ConditionParamContext();
		if (MapUtils.isNotEmpty(_paramData)) {
			context.paramData.putAll(_paramData);
			String formConfigStr = _paramData.getString("formConfig");
			if (StringUtils.isNotBlank(formConfigStr)) {
				context.formConfig = JSONObject.parseObject(formConfigStr);
			}
		}
		instance.set(context);
		return context;
	}

	public static ConditionParamContext get() {
		return instance.get();
	}

	public void release() {
		instance.remove();
	}

	private ConditionParamContext() {

	}

	public JSONObject getParamData() {
		return paramData;
	}

	public ConditionParamContext setParamData(JSONObject paramData) {
		this.paramData = paramData;
		return this;
	}

	public JSONObject getFormConfig() {
		return formConfig;
	}

	public ConditionParamContext setFormConfig(JSONObject formConfig) {
		this.formConfig = formConfig;
		return this;
	}

	public boolean isTranslate() {
		return translate;
	}

	public ConditionParamContext setTranslate(boolean translate) {
		this.translate = translate;
		return this;
	}
}
