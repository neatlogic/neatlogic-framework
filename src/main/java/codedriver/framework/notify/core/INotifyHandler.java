package codedriver.framework.notify.core;

import org.springframework.util.ClassUtils;

import codedriver.framework.notify.dto.NotifyVo;

public interface INotifyHandler {

	public enum RecipientType{
		TO("to","接收人"),
		CC("cc","抄送人");
		private String value;
		private String text;
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
	public void execute(NotifyVo notifyVo);
	
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
