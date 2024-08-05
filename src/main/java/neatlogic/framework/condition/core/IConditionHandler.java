package neatlogic.framework.condition.core;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.common.constvalue.Expression;
import neatlogic.framework.common.constvalue.ParamType;
import neatlogic.framework.form.constvalue.FormConditionModel;

import java.util.List;

/**
 * @Time:2020年7月10日
 * @ClassName: IConditionHandler
 * @Description: 条件处理器类
 */
public interface IConditionHandler {

    /**
     * 参数归属
     */
    String getBelong();

    /**
     * @Description: 条件英文名
     * @Param:
     * @return: java.lang.String
     * @Date: 2020/2/11
     */
    String getName();

    /**
     * @Description: 条件显示名
     * @Param:
     * @return: java.lang.String
     * @Date: 2020/2/11
     */
    String getDisplayName();

    /**
     * @Description: 获取控件类型，
     * @Param: simple:目前用于用于工单中心条件过滤简单模式
     * custom:目前用于用于工单中心条件过自定义模式、条件分流和sla条件
     * @return: java.lang.String
     * @Date: 2020/2/11
     */
    String getHandler(FormConditionModel processWorkcenterConditionType);

    /**
     * @Description: 获取类型
     * @Param:
     * @return: java.lang.String
     * @Date: 2020/2/11
     */
    String getType();

    /**
     * @param type 根据不同类型获取配置
     * @return com.alibaba.fastjson.JSONObject
     * @description 获取控件配置
     * @since 2020/2/11
     */
    JSONObject getConfig(Enum<?> type);

    /**
     * @Description: 获取控件配置
     * @Param:
     * @return: com.alibaba.fastjson.JSONObject
     * @Date: 2020/2/11
     */
    JSONObject getConfig();

    /**
     * @Description: 获取控件页面显示排序，越小越靠前
     * @Param:
     * @return: java.lang.Integer
     * @Date: 2020/2/11
     */
    Integer getSort();

    /**
     * @Description: 基本类型（表达式）
     * @Param:
     * @return: java.lang.Integer
     * @Date: 2020/2/11
     */
    ParamType getParamType();

    /**
     * @param value  值
     * @param config 额外信息，如工单信息，表单信息
     * @return Object 对应的文本
     * @Time:2020年7月9日
     * @Description: 将条件组合中表达式右边值转换成对应的文本，条件步骤流转生成活动中需要展示对应文本,比如用户uuid转换成userName,下拉框的value转换成对应的text
     */
    Object valueConversionText(Object value, JSONObject config);

    default Expression getExpression() {
        if (getParamType() != null) {
            return getParamType().getDefaultExpression();
        }
        return null;
    }

    default List<Expression> getExpressionList() {
        if (getParamType() != null) {
            return getParamType().getExpressionList();
        }
        return null;
    }

    /**
     * 条件帮助描述
     */
    default String getDesc(){
        return null;
    }

    /**
     * 是否显示，目前控制前端条件在页面中是否显示
     *
     * @param jsonObj 参数
     * @param type 类型，如workcenter
     */
    default boolean isShow(JSONObject jsonObj,String type) {
        return true;
    }

}
