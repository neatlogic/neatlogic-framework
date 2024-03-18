/*Copyright (C) $today.year  深圳极向量科技有限公司 All Rights Reserved.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

package neatlogic.framework.form.attribute.core;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.common.constvalue.ParamType;
import neatlogic.framework.form.constvalue.FormConditionModel;
import neatlogic.framework.form.dto.AttributeDataVo;
import neatlogic.framework.form.dto.FormAttributeVo;
import neatlogic.framework.form.exception.AttributeValidException;

import java.util.List;

/**
 * @Author:chenqiwei
 * @Time:2020年11月19日
 * @ClassName: IFormAttributeHandler
 * @Description: 表单组件接口，新增表单组件必须实现此接口
 */
public interface IFormAttributeHandler {

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
     * 能否作为工单批量上报模板参数
     * @return
     */
    boolean isProcessTaskBatchSubmissionTemplateParam();

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
     * 转换数据类型，传入的数据转换成当前组件对应的数据类型
     * @param source
     * @param attributeLabel
     * @return
     */
    default Object conversionDataType(Object source, String attributeLabel) {
        return source;
    }
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
