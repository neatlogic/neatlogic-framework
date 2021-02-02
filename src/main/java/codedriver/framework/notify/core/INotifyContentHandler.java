package codedriver.framework.notify.core;

import codedriver.framework.common.dto.ValueTextVo;
import codedriver.framework.notify.dto.NotifyVo;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.util.ClassUtils;

import java.util.List;

/**
 *@Title: 通知定时任务插件接口
 *@Package: codedriver.framework.notify.core
 *@Description:
 *@Author: laiwt
 *@Date: 2021/1/8 18:52
 *Copyright(c) ${YEAR} TechSure Co., Ltd. All Rights Reserved.
 *本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 **/
public interface INotifyContentHandler {

	public enum Type{
		STATIC("static","静态"),
		DYNAMIC("dynamic","动态");//动态插件，通知消息接收人不可指定
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
	 * @Description: 获取插件类别
	 * @Author: laiwt
	 * @Date: 2021/1/8 18:19
	 * @Params: []
	 * @Returns: java.lang.String
	**/
	public String getType();

	/**
	 * @Description: 获取插件自带的条件，例如【待我处理的工单】插件自带【处理组】条件
	 * @Author: laiwt
	 * @Date: 2021/1/8 18:20
	 * @Params: []
	 * @Returns: com.alibaba.fastjson.JSONArray
	**/
	public JSONArray getConditionOptionList();

	/**
	 * @Description: 根据通知方式插件获取消息相关表单属性，例如【通知消息标题】、【通知消息内容】、【接收人】、【抄送人】
	 * @Author: laiwt
	 * @Date: 2021/1/8 18:20
	 * @Params: [handler]
	 * @Returns: com.alibaba.fastjson.JSONArray
	**/
	public JSONArray getMessageAttrList(String handler);

	/**
	 * @Description: 获取通知内容插件的相关数据列，例如【待我处理的工单】包含工单中心的可显示列
	 * @Author: laiwt
	 * @Date: 2021/1/8 18:21
	 * @Params: []
	 * @Returns: java.util.List<codedriver.framework.common.dto.ValueTextVo>
	**/
	public List<ValueTextVo> getDataColumnList();

	/**
	 * @Description: 组装待发送数据
	 * @Author: Aienao
	 * @Date: 2021/1/8 18:21
	 * @Params: [id] notify_job主键
	 * @Returns: java.util.List<codedriver.framework.notify.dto.NotifyVo>
	**/
	public List<NotifyVo> getNotifyData(Long id);

	/**
	 * @Description: 插件预览
	 * @Author: laiwt
	 * @Date: 2021/1/8 18:15
	 * @Params: [config]
	 * @Returns: java.lang.String
	**/
	public String preview(JSONObject config,String notifyHandler);

	public default String getClassName() {
		return ClassUtils.getUserClass(this.getClass()).getName();
	}

}
