/*
 * Copyright (c)  2021 TechSure Co.,Ltd.  All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.module.framework.form.attribute.handler;

import neatlogic.framework.common.constvalue.ParamType;
import neatlogic.framework.exception.type.ParamIrregularException;
import neatlogic.framework.form.attribute.core.FormHandlerBase;
import neatlogic.framework.form.constvalue.DateRange;
import neatlogic.framework.form.constvalue.FormConditionModel;
import neatlogic.framework.form.constvalue.FormHandler;
import neatlogic.framework.form.dto.AttributeDataVo;
import neatlogic.framework.form.exception.AttributeValidException;
import neatlogic.framework.worktime.dao.mapper.WorktimeMapper;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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
    public JSONObject valid(AttributeDataVo attributeDataVo, JSONObject jsonObj) throws AttributeValidException {
        JSONObject resultObj = new JSONObject();
        resultObj.put("result", true);
        JSONObject configObj = jsonObj.getJSONObject("attributeConfig");
        List<String> validTypeList = JSON.parseArray(configObj.getString("validType"), String.class);
        if (CollectionUtils.isNotEmpty(validTypeList)) {
            if (validTypeList.contains("workdate")) {
                String worktimeUuid = jsonObj.getString("worktimeUuid");
                String data = attributeDataVo.getData();
                JSONObject detailedData = getMyDetailedData(attributeDataVo, configObj);
                String format = detailedData.getString("format");
                String showType = configObj.getString("showType");
                if (DATE_FORMAT.equals(showType)) {
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
                } else if (DATETIME_FORMAT.equals(showType)) {
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
        return resultObj;
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
    public Object textConversionValue(List<String> values, JSONObject config) {
        if (CollectionUtils.isNotEmpty(values)) {
            return values.get(0);
        }
        return null;
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
    public boolean isAudit() {
        return true;
    }

    @Override
    public Boolean isUseFormConfig() {
        return false;
    }

    //表单组件配置信息
//{
//	"handler": "formdate",
//	"label": "日期_4",
//	"type": "form",
//	"uuid": "a261982383e64f5ca45437cc025a1b72",
//	"config": {
//		"isRequired": false,
//		"validType": [],
//		"defaultValueList": "2022-02-21 00:00",
//		"ruleList": [],
//		"validValueList": [],
//		"validList": [],
//		"quoteUuid": "",
//		"styleType": "-",
//		"nowValue": "",
//		"nowUnit": "",
//		"width": "100%",
//		"showType": "yyyy-MM-dd HH:mm",
//		"defaultValueType": "self",
//		"placeholder": "请选择日期",
//		"authorityConfig": [
//			"common#alluser"
//		],
//		"nowExpression": "others"
//	}
//}
    //保存数据结构
//    "2022-02-21 00:00"
    //返回数据结构
//{
//	"format": "yyyy-MM-dd HH:mm",
//	"value": "2022-02-21 00:00"
//}
    @Override
    protected JSONObject getMyDetailedData(AttributeDataVo attributeDataVo, JSONObject configObj) {
        JSONObject resultObj = new JSONObject();
        String format = configObj.getString("showType");
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