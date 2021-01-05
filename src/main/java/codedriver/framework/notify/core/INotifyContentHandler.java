package codedriver.framework.notify.core;

import codedriver.framework.common.dto.ValueTextVo;
import codedriver.framework.notify.dto.NotifyVo;
import com.alibaba.fastjson.JSONArray;
import org.springframework.util.ClassUtils;

import java.util.List;

/**
 * 通知定时任务插件接口
 */
public interface INotifyContentHandler {

	public enum Type{
		STATIC("static","静态"),
		DYNAMIC("dynamic","动态");
		private String value;
		private String text;
		private Type(String value, String text) {
			this.value = value;
			this.text = text;
		}
		public String getValue() {
			return value;
		}
		public String getText() {
			return text;
		}

		public static Type getType(String _value) {
			for(Type e : values()) {
				if(e.value.equals(_value)) {
					return e;
				}
			}
			return null;
		}
	}


	public String getName();

	/**
	 * 获取插件类别，动态插件，通知内容接收人不可指定
	 * @return
	 */
	public String getType();

	/**
	 * 获取插件自带的条件，例如【待我处理的工单】插件自带【处理组】条件
	 * @return
	 */
	public JSONArray getConditionOptionList();

	/**
	 * 根据通知方式插件获取消息相关表单属性，例如【通知消息标题】、【通知消息内容】、【接收人】、【抄送人】
	 * @param handler
	 * @return
	 */
	public JSONArray getMessageAttrList(String handler);

	/**
	 * 获取通知内容插件的相关数据列，例如【待我处理的工单】包含工单中心的可显示列
	 * @return
	 */
	public List<ValueTextVo> getDataColumnList();

	/**
	 * 组装待发送数据
	 * @param id notify_job主键
	 */
	public List<NotifyVo> getNotifyData(Long id);

	public default String getClassName() {
		return ClassUtils.getUserClass(this.getClass()).getName();
	}

}
