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
    public final static String SELECT_COMPOSE_JOINER = "&=&";

    /**
     * @param @return
     * @return String
     * @Author: chenqiwei
     * @Time:2020年11月19日
     * @Description: 获取组件英文名
     */
    public String getHandler();

    /**
     * @param @return
     * @return String
     * @Author: chenqiwei
     * @Time:2020年11月19日
     * @Description: 获取组件中文名
     */
    public String getHandlerName();

    /**
     * @param @return
     * @return String
     * @Author: chenqiwei
     * @Time:2020年11月19日
     * @Description: 获取表单真实控件，不同模式不一样
     */
    public String getHandlerType(FormConditionModel model);

    /**
     * @param @return
     * @return String
     * @Author: chenqiwei
     * @Time:2020年11月19日
     * @Description: 获取组件图标
     */
    public String getIcon();

    /**
     * @param @return
     * @return String
     * @Author: chenqiwei
     * @Time:2020年11月10日
     * @Description: 组件类型，表单组件还是控制组件，form|control
     */
    public String getType();

    /**
     * @param @return
     * @return ParamType
     * @Author: chenqiwei
     * @Time:2020年11月19日
     * @Description: 组件参数的数据类型
     */
    public ParamType getParamType();

    /**
     * @param @return
     * @return String
     * @Author: chenqiwei
     * @Time:2020年11月19日
     * @Description: 组件返回的数据类型
     */
    public String getDataType();

    /**
     * @param @return
     * @return boolean
     * @Author: chenqiwei
     * @Time:2020年11月19日
     * @Description: 是否需要审计，需要会出现在操作记录列表里
     */
    public boolean isAudit();

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
     * @return boolean
     * @Author: chenqiwei
     * @Time:2020年11月19日
     * @Description: 能否作为模板参数
     */
    public boolean isForTemplate();

    /**
     * @param @return
     * @return String
     * @Author: chenqiwei
     * @Time:2020年11月10日
     * @Description: 所属模块
     */
    public String getModule();

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
    public JSONObject valid(AttributeDataVo attributeDataVo, JSONObject configObj) throws AttributeValidException;

    /**
     * @param attributeDataVo
     * @param configObj
     * @return Object
     * @Time:2020年7月10日
     * @Description: 将表单属性值转换成对应的text
     */
    public Object valueConversionText(AttributeDataVo attributeDataVo, JSONObject configObj);

    /**
     * 数据转换，用于邮件模板展示表单信息
     *
     * @param attributeDataVo
     * @param configObj
     * @return
     */
    public Object dataTransformationForEmail(AttributeDataVo attributeDataVo, JSONObject configObj);

    /**
     * @Description: 将text转换成表单属性值，暂时用于批量导入工单
     * @Author: laiwt
     * @Date: 2021/1/28 17:06
     * @Params: [values, config]
     * @Returns: java.lang.Object
     **/
    public Object textConversionValue(List<String> values, JSONObject config);

    /**
     * @param @return
     * @return int
     * @Author: chenqiwei
     * @Time:2020年11月19日
     * @Description: 排序
     */
    public default int getSort() {
        return 0;
    }

    /**
     * 用于创建索引，不同的表单需根据自身规则分拆成多个field content
     *
     * @param data
     * @return 返回contentList
     */
    public List<String> indexFieldContentList(String data);

    /**
     * 如果无需分词将会作为md5保存
     *
     * @return 返回是否需要分词
     * @author lvzk
     */
    public Boolean isNeedSliceWord();

    /**
     * 供前端渲染时判断，如果为false则前端页面需使用默认config,true则使用表单管理编辑保存的config
     *
     * @return 是否需要表单管理编辑保存的config
     */
    public Boolean isUseFormConfig();

    JSONObject getDetailedData(AttributeDataVo attributeDataVo, JSONObject configObj);

    /**
     * 数据转换，用户工单导出
     * @param attributeDataVo
     * @param configObj
     * @return
     */
    Object dataTransformationForExcel(AttributeDataVo attributeDataVo, JSONObject configObj);

    /**
     * 导出excel时，当前表单组件需要占据的表头单元格长度
     * 对于普通组件，如文本框、日期等，默认占一格
     * 对于表格输入组件等表格类组件，占据的单元格长度视具体配置而定
     *
     * @param configObj
     * @return
     */
    default int getExcelHeadLength(JSONObject configObj) {
        return 1;
    }

    /**
     * 导出excel时，当前表单组件数据需要占据的行数
     * 对于普通组件，如文本框、日期等，默认占一行
     * 对于表格输入组件等表格类组件，占据的行数视数据而定
     *
     * @param attributeDataVo
     * @param configObj
     * @return
     */
    default int getExcelRowCount(AttributeDataVo attributeDataVo, JSONObject configObj) {
        return 1;
    }

    default void makeupFormAttribute(FormAttributeVo formAttributeVo) {

    }
}
