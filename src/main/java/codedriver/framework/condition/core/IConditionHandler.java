package codedriver.framework.condition.core;

import codedriver.framework.common.constvalue.Expression;
import codedriver.framework.common.constvalue.ParamType;
import com.alibaba.fastjson.JSONObject;

import java.util.List;
/**
 * 
* @Time:2020年7月10日
* @ClassName: IConditionHandler 
* @Description: 条件处理器类
 */
public interface IConditionHandler {

	/**
	 * @Description: 条件英文名
	 * @Param:
	 * @return: java.lang.String
	 * @Date: 2020/2/11
	 */
	public String getName();
	
	/**
	 * @Description: 条件显示名
	 * @Param:
	 * @return: java.lang.String
	 * @Date: 2020/2/11
	 */
	public String getDisplayName();

	/**
	 * @Description: 获取控件类型，
	 * @Param: simple:目前用于用于工单中心条件过滤简单模式
	 *         custom:目前用于用于工单中心条件过自定义模式、条件分流和sla条件
	 * @return: java.lang.String
	 * @Date: 2020/2/11
	 */
	public String getHandler(String processWorkcenterConditionType);
	
	/**
	 * @Description: 获取类型
	 * @Param: 
	 * @return: java.lang.String
	 * @Date: 2020/2/11
	 */
	public String getType();
	
	/**
	 * @description 获取控件配置
	 * @param  type 根据不同类型获取配置
	 * @return com.alibaba.fastjson.JSONObject
	 * @since 2020/2/11
	 */
	public JSONObject getConfig(Enum<?> type);

	/**
	 * @Description: 获取控件配置
	 * @Param:
	 * @return: com.alibaba.fastjson.JSONObject
	 * @Date: 2020/2/11
	 */
	public JSONObject getConfig();
	
	/**
	 * @Description: 获取控件页面显示排序，越小越靠前
	 * @Param: 
	 * @return: java.lang.Integer
	 * @Date: 2020/2/11
	 */
	public Integer getSort();
	
	/**
	 * @Description: 基本类型（表达式）
	 * @Param: 
	 * @return: java.lang.Integer
	 * @Date: 2020/2/11
	 */
	public ParamType getParamType();
	
	/**
	 * 
	* @Time:2020年7月9日
	* @Description: 将条件组合中表达式右边值转换成对应的文本，条件步骤流转生成活动中需要展示对应文本,比如用户uuid转换成userName,下拉框的value转换成对应的text 
	* @param value 值
	* @param config 额外信息，如工单信息，表单信息
	* @return Object 对应的文本
	 */
	public Object valueConversionText(Object value, JSONObject config);
	
	public default Expression getExpression() {
	    if(getParamType() != null) {
	        return getParamType().getDefaultExpression();
	    }
	    return null;
	}
	
	public default List<Expression> getExpressionList() {
	    if(getParamType() != null) {
	        return getParamType().getExpressionList();
	    }
        return null;
	}
}
