/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.form.constvalue;

import codedriver.framework.common.constvalue.Expression;
import codedriver.framework.common.constvalue.FormHandlerType;
import codedriver.framework.common.constvalue.ParamType;

/**
 * @Author:chenqiwei
 * @Time:2020年11月10日
 * @ClassName: IProcessFormHandler
 * @Description: 表单组件类型枚举接口
 */
public interface IFormHandlerType {
    /**
     * @param @return
     * @return String
     * @Author: chenqiwei
     * @Time:2020年11月10日
     * @Description: 控件
     */
    public String getHandler();

    /**
     * @param @return
     * @return String
     * @Author: chenqiwei
     * @Time:2020年11月10日
     * @Description: 中文名
     */
    public String getHandlerName();

    /**
     * @param @return
     * @return String
     * @Author: chenqiwei
     * @Time:2020年11月10日
     * @Description:
     */
    public String getDataType();

    /**
     * @param @return
     * @return String
     * @Author: chenqiwei
     * @Time:2020年11月10日
     * @Description: 图标
     */
    public String getIcon();

    /**
     * @param @return
     * @return String
     * @Author: chenqiwei
     * @Time:2020年11月10日
     * @Description: 组件类型，表单组件还是控制组件
     */
    public String getType();

    public FormHandlerType getHandlerType();

    public Expression getExpression();

    public ParamType getParamType();

    /**
     * @param @return
     * @return boolean
     * @Author: chenqiwei
     * @Time:2020年11月10日
     * @Description: 是否可设置为条件
     */
    public boolean isConditionable();

    /**
     * @param @return
     * @return boolean
     * @Author: chenqiwei
     * @Time:2020年11月10日
     * @Description: 是否可设置显示隐藏
     */
    public boolean isShowable();

    /**
     * @param @return
     * @return boolean
     * @Author: chenqiwei
     * @Time:2020年11月10日
     * @Description: 是否可设置赋值
     */
    public boolean isValueable();

    /**
     * @param @return
     * @return boolean
     * @Author: chenqiwei
     * @Time:2020年11月10日
     * @Description: 是否可设置过滤
     */
    public boolean isFilterable();

    /**
     * @param @return
     * @return boolean
     * @Author: chenqiwei
     * @Time:2020年11月10日
     * @Description: 是否有拓展属性
     */
    public boolean isExtendable();

    /**
     * @param @return
     * @return String
     * @Author: chenqiwei
     * @Time:2020年11月10日
     * @Description: 所属模块
     */
    public String getModule();
}
