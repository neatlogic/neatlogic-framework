/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.notify.core;

import org.springframework.util.ClassUtils;

import codedriver.framework.notify.dto.NotifyVo;

public interface INotifyHandler {

	enum RecipientType {
		TO("to", "接收人"),
		CC("cc", "抄送人");
		private final String value;
		private final String text;

		private RecipientType(String value, String text) {
			this.value = value;
			this.text = text;
		}

		public String getValue() {
			return value;
		}

		public String getText() {
			return text;
		}

		public static RecipientType getType(String _value) {
			for(RecipientType e : values()) {
				if(e.value.equals(_value)) {
					return e;
				}
			}
			return null;
		}
	}

	/**
	 * @Description: 处理通知
	 * @Param: [informVo]
	 * @return: void
	 */
	public void execute(NotifyVo notifyVo) throws Exception;
	
	public String getType();

	/**
	 * @Author: chenqiwei
	 * @Time:Jan 25, 2020
	 * @Description: 插件名称
	 * @param @return
	 * @return String
	 */
	public String getName();

	/**
	 * 
	 * @Author: chenqiwei
	 * @Time:Feb 12, 2020
	 * @param @return
	 * @return String
	 */
	public default String getClassName() {
		return ClassUtils.getUserClass(this.getClass()).getName();
	}

}
