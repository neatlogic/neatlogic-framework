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

package neatlogic.module.framework.form.attribute.handler;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.common.constvalue.ParamType;
import neatlogic.framework.exception.type.ParamIrregularException;
import neatlogic.framework.form.attribute.core.FormHandlerBase;
import neatlogic.framework.form.constvalue.DateRange;
import neatlogic.framework.form.constvalue.FormConditionModel;
import neatlogic.framework.form.constvalue.FormHandler;
import neatlogic.framework.form.dto.AttributeDataVo;
import neatlogic.framework.form.exception.AttributeValidException;
import neatlogic.framework.worktime.dao.mapper.WorktimeMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@Component
public class DateHandler extends FormHandlerBase {

    @Resource
    private WorktimeMapper worktimeMapper;

    private final static String DATE_FORMAT = "yyyy-MM-dd";
    private final static String DATETIME_FORMAT = "yyyy-MM-dd HH:mm";

    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATETIME_FORMAT);
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(DATE_FORMAT);

    @Override
    public String getHandler() {
        return FormHandler.FORMDATE.getHandler();
    }

    @Override
    public String getHandlerType(FormConditionModel model) {
        return "datetime";
    }

    @Override
    public JSONObject valid(AttributeDataVo attributeDataVo, JSONObject configObj) throws AttributeValidException {
        JSONObject resultObj = new JSONObject();
        resultObj.put("result", true);
        String worktimeUuid = configObj.getString("worktimeUuid");
        if (StringUtils.isNotBlank(worktimeUuid)) {
            JSONArray validTypeArray = configObj.getJSONArray("validType");
            if (CollectionUtils.isNotEmpty(validTypeArray)) {
                List<String> validTypeList = validTypeArray.toJavaList(String.class);
                if (validTypeList.contains("workdate")) {
                    String data = attributeDataVo.getData();
                    JSONObject detailedData = getMyDetailedData(attributeDataVo, configObj);
                    String format = detailedData.getString("format");
                    if (DATE_FORMAT.equals(format)) {
                        try {
                            SimpleDateFormat sdf = new SimpleDateFormat(format);
                            Date date = sdf.parse(data);
                            String dateStr = dateFormatter.format((date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()));
                            boolean result = worktimeMapper.checkIsWithinWorktime(worktimeUuid, dateStr) > 0;
                            resultObj.put("result", result);
                            return resultObj;
                        } catch (ParseException ex) {
                            throw new ParamIrregularException("data", format);
                        }
                    } else if (DATETIME_FORMAT.equals(format)) {
                        try {
                            SimpleDateFormat sdf = new SimpleDateFormat(format);
                            Date date = sdf.parse(data);
                            boolean result = worktimeMapper.checkIsWithinWorktimeRange(worktimeUuid, date.getTime()) > 0;
                            resultObj.put("result", result);
                            return resultObj;
                        } catch (ParseException ex) {
                            throw new ParamIrregularException("data", format);
                        }
                    }
                }
            }
        }
        return resultObj;
    }

    @Override
    public Object conversionDataType(Object source, String attributeLabel) {
        return convertToString(source, attributeLabel);
    }

    @Override
    public int getSort() {
        return 8;
    }

    @Override
    public Object valueConversionText(AttributeDataVo attributeDataVo, JSONObject configObj) {
        Object data = attributeDataVo.getDataObj();
        if (data == null) {
            return null;
        }
        // 下面代码逻辑用于工单中心
        JSONObject dataObj = null;
        if (data instanceof JSONObject) {
            dataObj = (JSONObject) data;
        } else if (data instanceof JSONArray) {
            // 下面代码逻辑用于工单中心
            JSONArray dataArray = (JSONArray) data;
            if (CollectionUtils.isNotEmpty(dataArray)) {
                dataObj = dataArray.getJSONObject(0);
            }
        }
        if (MapUtils.isNotEmpty(dataObj)) {
            Integer timeRange = dataObj.getInteger("timeRange");
            String timeUnit = dataObj.getString("timeUnit");
            if (timeRange != null && StringUtils.isNotBlank(timeUnit)) {
                return DateRange.getText(timeRange, timeUnit);
            } else {
                Long startTime = dataObj.getLong("startTime");
                Long endTime = dataObj.getLong("endTime");
                if (startTime != null && endTime != null) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    String startTimeStr = sdf.format(new Date(startTime));
                    String endTimeStr = sdf.format(new Date(endTime));
                    return startTimeStr + " - " + endTimeStr;
                }
            }
        }
        return data;
    }

    @Override
    public Object dataTransformationForEmail(AttributeDataVo attributeDataVo, JSONObject configObj) {
        return attributeDataVo.getDataObj();
    }

    @Override
    public Object textConversionValue(Object text, JSONObject config) {
        return text;
    }

    @Override
    public String getHandlerName() {
        return FormHandler.FORMDATE.getHandlerName();
    }

    @Override
    public String getIcon() {
        return "ts-calendar";
    }

    @Override
    public ParamType getParamType() {
        return ParamType.DATE;
    }

    @Override
    public String getDataType() {
        return "string";
    }

    @Override
    public boolean isConditionable() {
        return true;
    }

    @Override
    public boolean isShowable() {
        return true;
    }

    @Override
    public boolean isValueable() {
        return true;
    }

    @Override
    public boolean isFilterable() {
        return true;
    }

    @Override
    public boolean isExtendable() {
        return false;
    }

    @Override
    public String getModule() {
        return "framework";
    }

    @Override
    public boolean isForTemplate() {
        return true;
    }

    @Override
    public boolean isProcessTaskBatchSubmissionTemplateParam() {
        return true;
    }

    @Override
    public boolean isAudit() {
        return true;
    }

    @Override
    public Boolean isUseFormConfig() {
        return false;
    }

    /*
    表单组件配置信息
    {
        "handler": "formdate",
        "reaction": {
            "hide": {},
            "readonly": {},
            "setvalue": {},
            "disable": {},
            "display": {},
            "mask": {}
        },
        "override_config": {},
        "icon": "tsfont-calendar",
        "hasValue": true,
        "label": "日期_1",
        "type": "form",
        "category": "basic",
        "config": {
            "isRequired": false,
            "styleType": "-",
            "disableDefaultValue": true,
            "validType": [
                "workdate"
            ],
            "isMask": false,
            "isReadOnly": false,
            "width": "100%",
            "format": "yyyy-MM-dd HH:mm",
            "description": "",
            "isDisabled": false,
            "placeholder": "请选择日期",
            "isHide": false
        },
        "uuid": "668030e5a922463b9e73d2b51064b7b1"
    }
     */
    /*
    保存数据结构
    如果format是"yyyy-MM-dd HH:mm"，styleType是"-"，那么保存数值为2023-03-02 00:00；
    如果format是"yyyy/MM/dd HH:mm"，styleType是"/"，那么保存数值为2023/03/03 00:00；
    如果format是"yyyy-MM-dd HH:mm"，styleType是"|"，那么保存数值为20230304 00:00；
    如果format是"yyyy-MM-dd"，styleType是"-"，那么保存数值为2023-03-05；
     */
    /*
    返回数据结构
    {
        "format": "yyyy-MM-dd HH:mm",
        "value": "2023-03-02 00:00"
    }
    {
        "format": "yyyy/MM/dd HH:mm",
        "value": "2023/03/03 00:00"
    }
    {
        "format": "yyyyMMdd HH:mm",
        "value": "20230304 00:00"
    }
    {
        "format": "yyyy-MM-dd",
        "value": "2023-03-05"
    }
     */
    @Override
    protected JSONObject getMyDetailedData(AttributeDataVo attributeDataVo, JSONObject configObj) {
        JSONObject resultObj = new JSONObject();
        String format = configObj.getString("format");
        String styleType = configObj.getString("styleType");
        if (StringUtils.isNotBlank(styleType) && !"-".equals(styleType)) {
            if ("|".equals(styleType)) {
                //当styleType为“|”时，设置的是“yyyyMMdd HH:mm”格式
                styleType = "";
            }
            // 当styleType是"/"，showType="yyyy-MM-dd"，保存的日期值是2022/02/11，需要将yyyy-MM-dd转换成yyyy/MM/dd
            format = format.replace("-", styleType);
        }
        resultObj.put("format", format);
        resultObj.put("value", attributeDataVo.getDataObj());
        return resultObj;
    }

    @Override
    public Object dataTransformationForExcel(AttributeDataVo attributeDataVo, JSONObject configObj) {
        return attributeDataVo.getDataObj();
    }
}
