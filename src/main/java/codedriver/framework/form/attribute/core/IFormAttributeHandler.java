/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.form.attribute.core;

import codedriver.framework.common.constvalue.ParamType;
import codedriver.framework.form.constvalue.FormConditionModel;
import codedriver.framework.form.dto.AttributeDataVo;
import codedriver.framework.form.dto.FormAttributeVo;
import codedriver.framework.form.exception.AttributeValidException;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

/**
 * @Author:chenqiwei
 * @Time:2020年11月19日
 * @ClassName: IFormAttributeHandler
 * @Description: 表单组件接口，新增表单组件必须实现此接口
 */
public interface IFormAttributeHandler {
    /**
     * 下拉列表value和text列的组合连接符
     **/
    String SELECT_COMPOSE_JOINER = "&=&";

    /**
     * @param @return
     * @return String
     * @Author: chenqiwei
     * @Time:2020年11月19日
     * @Description: 获取组件英文名
     */
    String getHandler();

    /**
     * @param @return
     * @return String
     * @Author: chenqiwei
     * @Time:2020年11月19日
     * @Description: 获取组件中文名
     */
    String getHandlerName();

    /**
     * @param @return
     * @return String
     * @Author: chenqiwei
     * @Time:2020年11月19日
     * @Description: 获取表单真实控件，不同模式不一样
     */
    String getHandlerType(FormConditionModel model);

    /**
     * @param @return
     * @return String
     * @Author: chenqiwei
     * @Time:2020年11月19日
     * @Description: 获取组件图标
     */
    String getIcon();

    /**
     * @param @return
     * @return String
     * @Author: chenqiwei
     * @Time:2020年11月10日
     * @Description: 组件类型，表单组件还是控制组件，form|control
     */
    String getType();

    /**
     * @param @return
     * @return ParamType
     * @Author: chenqiwei
     * @Time:2020年11月19日
     * @Description: 组件参数的数据类型
     */
    ParamType getParamType();

    /**
     * @param @return
     * @return String
     * @Author: chenqiwei
     * @Time:2020年11月19日
     * @Description: 组件返回的数据类型
     */
    String getDataType();

    /**
     * @param @return
     * @return boolean
     * @Author: chenqiwei
     * @Time:2020年11月19日
     * @Description: 是否需要审计，需要会出现在操作记录列表里
     */
    boolean isAudit();

    /**
     * @param @return
     * @return boolean
     * @Author: chenqiwei
     * @Time:2020年11月10日
     * @Description: 是否可设置为条件
     */
    boolean isConditionable();

    /**
     * @param @return
     * @return boolean
     * @Author: chenqiwei
     * @Time:2020年11月10日
     * @Description: 是否可设置显示隐藏
     */
    boolean isShowable();

    /**
     * @param @return
     * @return boolean
     * @Author: chenqiwei
     * @Time:2020年11月10日
     * @Description: 是否可设置赋值
     */
    boolean isValueable();

    /**
     * @param @return
     * @return boolean
     * @Author: chenqiwei
     * @Time:2020年11月10日
     * @Description: 是否可设置过滤
     */
    boolean isFilterable();

    /**
     * @param @return
     * @return boolean
     * @Author: chenqiwei
     * @Time:2020年11月10日
     * @Description: 是否有拓展属性
     */
    boolean isExtendable();

    /**
     * @param @return
     * @return boolean
     * @Author: chenqiwei
     * @Time:2020年11月19日
     * @Description: 能否作为模板参数
     */
    boolean isForTemplate();

    /**
     * @param @return
     * @return String
     * @Author: chenqiwei
     * @Time:2020年11月10日
     * @Description: 所属模块
     */
    String getModule();

    /**
     * @param @param  attributeDataVo
     * @param @param  configObj
     * @param @return
     * @param @throws AttributeValidException
     * @return boolean
     * @Author: chenqiwei
     * @Time:2020年11月19日
     * @Description: 验证组件数据完整性
     */
    JSONObject valid(AttributeDataVo attributeDataVo, JSONObject configObj) throws AttributeValidException;

    /**
     * @param @return
     * @return int
     * @Author: chenqiwei
     * @Time:2020年11月19日
     * @Description: 排序
     */
    int getSort();

    /**
     * 用于创建索引，不同的表单需根据自身规则分拆成多个field content
     *
     * @param data
     * @return 返回contentList
     */
    List<String> indexFieldContentList(String data);

    /**
     * 如果无需分词将会作为md5保存
     *
     * @return 返回是否需要分词
     * @author lvzk
     */
    Boolean isNeedSliceWord();

    /**
     * 供前端渲染时判断，如果为false则前端页面需使用默认config,true则使用表单管理编辑保存的config
     *
     * @return 是否需要表单管理编辑保存的config
     */
    Boolean isUseFormConfig();

    default void makeupFormAttribute(FormAttributeVo formAttributeVo) {

    }
}
