package neatlogic.module.framework.form.attribute.handler;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.common.constvalue.ParamType;
import neatlogic.framework.form.attribute.core.FormAttributeDataConversionHandlerFactory;
import neatlogic.framework.form.attribute.core.FormHandlerBase;
import neatlogic.framework.form.attribute.core.IFormAttributeDataConversionHandler;
import neatlogic.framework.form.constvalue.FormConditionModel;
import neatlogic.framework.form.constvalue.FormHandler;
import neatlogic.framework.form.dto.AttributeDataVo;
import neatlogic.framework.form.dto.FormAttributeVo;
import neatlogic.framework.form.exception.AttributeValidException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class SubassemblyHandler extends FormHandlerBase {
    @Override
    protected JSONObject getMyDetailedData(AttributeDataVo attributeDataVo, JSONObject configObj) {
        return null;
    }

    @Override
    public Object valueConversionText(AttributeDataVo attributeDataVo, JSONObject configObj) {
        if (!attributeDataVo.dataIsEmpty()) {
            return "已更新";
        } else {
            return "";
        }
    }

    @Override
    public Object dataTransformationForEmail(AttributeDataVo attributeDataVo, JSONObject configObj) {
        return null;
    }

    @Override
    public Object textConversionValue(Object text, JSONObject config) {
        return null;
    }

    @Override
    public Object dataTransformationForExcel(AttributeDataVo attributeDataVo, JSONObject configObj) {
        return null;
    }

    @Override
    public int getExcelHeadLength(JSONObject configObj) {
        return super.getExcelHeadLength(configObj);
    }

    @Override
    public int getExcelRowCount(AttributeDataVo attributeDataVo, JSONObject configObj) {
        return super.getExcelRowCount(attributeDataVo, configObj);
    }

    @Override
    public String getHandler() {
        return FormHandler.FORMSUBASSEMBLY.getHandler();
    }

    @Override
    public String getHandlerType(FormConditionModel model) {
        return null;
    }

    @Override
    public ParamType getParamType() {
        return null;
    }

    @Override
    public boolean isAudit() {
        return true;
    }

    @Override
    public boolean isConditionable() {
        return false;
    }

    @Override
    public boolean isProcessTaskBatchSubmissionTemplateParam() {
        return false;
    }

    @Override
    public JSONObject valid(AttributeDataVo attributeDataVo, JSONObject configObj) throws AttributeValidException {
        return null;
    }

    @Override
    public Object conversionDataType(Object source, String attributeLabel) {
        return convertToJSONArray(source, attributeLabel);
    }

    @Override
    public void makeupFormAttribute(FormAttributeVo formAttributeVo) {
        super.makeupFormAttribute(formAttributeVo);
    }

    @Override
    public Object passwordEncryption(Object source, JSONObject configObj) {
        JSONArray dataArray = null;
        if (source instanceof JSONArray) {
            dataArray = (JSONArray) source;
        }
        if (CollectionUtils.isEmpty(dataArray)) {
            return source;
        }
        JSONObject formData = configObj.getJSONObject("formData");
        if (MapUtils.isNotEmpty(formData)) {
            JSONObject formConfig = formData.getJSONObject("formConfig");
            if (MapUtils.isNotEmpty(formConfig)) {
                JSONArray tableList = formConfig.getJSONArray("tableList");
                if (CollectionUtils.isNotEmpty(tableList)) {
                    for (int i = 0; i < tableList.size(); i++) {
                        JSONObject tableObj = tableList.getJSONObject(i);
                        if (MapUtils.isNotEmpty(tableObj)) {
                            JSONObject component = tableObj.getJSONObject("component");
                            if (MapUtils.isNotEmpty(component)) {
                                String handler = component.getString("handler");
                                if (Objects.equals(handler, FormHandler.FORMPASSWORD.getHandler())
                                        || Objects.equals(handler, FormHandler.FORMTABLEINPUTER.getHandler())
                                        || Objects.equals(handler, FormHandler.FORMSUBASSEMBLY.getHandler())) {
                                    String uuid = component.getString("uuid");
                                    JSONObject config = component.getJSONObject("config");
                                    IFormAttributeDataConversionHandler formAttributeDataConversionHandler = FormAttributeDataConversionHandlerFactory.getHandler(handler);
                                    if (Objects.equals(handler, FormHandler.FORMSUBASSEMBLY.getHandler())) {
                                        JSONObject formData1 = component.getJSONObject("formData");
                                        config.put("formData", formData1);
                                    }
                                    for (int j = 0; j < dataArray.size(); j++) {
                                        JSONObject dataObj = dataArray.getJSONObject(j);
                                        if (MapUtils.isNotEmpty(dataObj)) {
                                            Object data = dataObj.get(uuid);
                                            if (data != null) {
                                                dataObj.put(uuid, formAttributeDataConversionHandler.passwordEncryption(data, config));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return dataArray;
    }

    @Override
    public String passwordDecryption(Object source, JSONObject configObj, String attributeUuid, JSONObject otherParamConfig) {
        JSONArray dataArray = null;
        if (source instanceof JSONArray) {
            dataArray = (JSONArray) source;
        }
        if (CollectionUtils.isEmpty(dataArray)) {
            return null;
        }
        JSONObject formData = configObj.getJSONObject("formData");
        if (MapUtils.isNotEmpty(formData)) {
            JSONObject formConfig = formData.getJSONObject("formConfig");
            if (MapUtils.isNotEmpty(formConfig)) {
                JSONArray tableList = formConfig.getJSONArray("tableList");
                if (CollectionUtils.isNotEmpty(tableList)) {
                    for (int i = 0; i < tableList.size(); i++) {
                        JSONObject tableObj = tableList.getJSONObject(i);
                        if (MapUtils.isNotEmpty(tableObj)) {
                            JSONObject component = tableObj.getJSONObject("component");
                            if (MapUtils.isNotEmpty(component)) {
                                String handler = component.getString("handler");
                                if (Objects.equals(handler, FormHandler.FORMPASSWORD.getHandler())
                                        || Objects.equals(handler, FormHandler.FORMTABLEINPUTER.getHandler())
                                        || Objects.equals(handler, FormHandler.FORMSUBASSEMBLY.getHandler())) {
                                    String uuid = component.getString("uuid");
                                    JSONObject config = component.getJSONObject("config");
                                    IFormAttributeDataConversionHandler formAttributeDataConversionHandler = FormAttributeDataConversionHandlerFactory.getHandler(handler);
                                    if (Objects.equals(handler, FormHandler.FORMPASSWORD.getHandler())) {
                                        if (Objects.equals(uuid, attributeUuid)) {
                                            String rowUuid = otherParamConfig.getString("rowUuid");
                                            for (int j = 0; j < dataArray.size(); j++) {
                                                JSONObject dataObj = dataArray.getJSONObject(j);
                                                if (MapUtils.isNotEmpty(dataObj)) {
                                                    if (Objects.equals(dataObj.getString("uuid"), rowUuid)) {
                                                        Object data = dataObj.get(uuid);
                                                        if (data != null) {
                                                            String result = formAttributeDataConversionHandler.passwordDecryption(data, config, attributeUuid, otherParamConfig);
                                                            if (result != null) {
                                                                return result;
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                    } else {
                                        if (Objects.equals(handler, FormHandler.FORMSUBASSEMBLY.getHandler())) {
                                            JSONObject formData1 = component.getJSONObject("formData");
                                            config.put("formData", formData1);
                                        }
                                        for (int j = 0; j < dataArray.size(); j++) {
                                            JSONObject dataObj = dataArray.getJSONObject(j);
                                            if (MapUtils.isNotEmpty(dataObj)) {
                                                Object data = dataObj.get(uuid);
                                                if (data != null) {
                                                    String result = formAttributeDataConversionHandler.passwordDecryption(data, config, attributeUuid, otherParamConfig);
                                                    if (result != null) {
                                                        return result;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
}
