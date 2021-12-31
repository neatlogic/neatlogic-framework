/*
 * Copyright (c)  2021 TechSure Co.,Ltd.  All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.module.framework.form.attribute.handler;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.util.List;

import codedriver.framework.form.constvalue.FormConditionModel;
import codedriver.framework.worktime.dao.mapper.WorktimeMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import codedriver.framework.common.constvalue.ParamType;
import codedriver.framework.exception.type.ParamIrregularException;
import codedriver.framework.form.dto.AttributeDataVo;
import codedriver.framework.form.exception.AttributeValidException;
import codedriver.framework.form.attribute.core.FormHandlerBase;

import javax.annotation.Resource;

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
        return "formdate";
    }

    @Override
    public String getHandlerType(FormConditionModel model) {
        return "date";
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
                String styleType = configObj.getString("styleType");
                String showType = configObj.getString("showType");
                if (DATE_FORMAT.equals(showType)) {
                    String date = data.replace(styleType, "-");
                    try {
                        dateFormatter.parse(date);
                        boolean result = worktimeMapper.checkIsWithinWorktime(worktimeUuid, date) > 0;
                        resultObj.put("result", result);
                        return resultObj;
                    } catch (DateTimeParseException ex) {
                        String format = DATE_FORMAT.replace("-", styleType);
                        throw new ParamIrregularException("data", format);
                    }
                } else if (DATETIME_FORMAT.equals(showType)) {
                    String dateTime = data.replace(styleType, "-");
                    try {
                        TemporalAccessor temporalAccessor = dateTimeFormatter.parse(dateTime);
                        LocalDateTime endLocalDateTime = LocalDateTime.from(temporalAccessor);
                        long datetime = endLocalDateTime.toInstant(OffsetDateTime.now().getOffset()).toEpochMilli();
                        boolean result = worktimeMapper.checkIsWithinWorktimeRange(worktimeUuid, datetime) > 0;
                        resultObj.put("result", result);
                        return resultObj;
                    } catch (DateTimeParseException ex) {
                        String format = DATETIME_FORMAT.replace("-", styleType);
                        throw new ParamIrregularException("data", format);
                    }
                }
            }
        }
        return resultObj;
    }

    @Override
    public Object valueConversionText(AttributeDataVo attributeDataVo, JSONObject configObj) {
        return attributeDataVo.getDataObj();
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
        return "日期";
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

    @Override
    protected JSONObject getMyDetailedData(AttributeDataVo attributeDataVo, JSONObject configObj) {
        JSONObject resultObj = new JSONObject();
        String format = configObj.getString("showType");
        String styleType = configObj.getString("styleType");
        if (StringUtils.isNotBlank(styleType) && !"-".equals(styleType)) {
            format = format.replace("-", styleType);
        }
        resultObj.put("format", format);
        resultObj.put("value", attributeDataVo.getDataObj());
        return resultObj;
    }

}
