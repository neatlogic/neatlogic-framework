/*
Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package neatlogic.framework.form.attribute.core;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.form.dto.AttributeDataVo;
import neatlogic.framework.form.exception.AttributeValidException;
import neatlogic.framework.matrix.dao.mapper.MatrixMapper;
import neatlogic.framework.util.$;
import neatlogic.framework.util.I18n;
import org.apache.commons.collections4.CollectionUtils;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

public abstract class FormHandlerBase implements IFormAttributeHandler, IFormAttributeDataConversionHandler {

    public enum ConversionType {

        TOVALUE("toValue", new I18n("text转换成value")),
        TOTEXT("toText", new I18n("value转换成text"));

        private String value;
        private I18n text;

        public String getValue() {
            return value;
        }

        public String getText() {
            return $.t(text.toString());
        }

        private ConversionType(String _value, I18n _text) {
            value = _value;
            text = _text;
        }
    }

    protected MatrixMapper matrixMapper;

    @Resource
    public void setMatrixMapper(MatrixMapper _matrixMapper) {
        this.matrixMapper = _matrixMapper;
    }

    @Override
    public final String getType() {
        return "form";
    }

    @Override
    public List<String> indexFieldContentList(String data) {
        List<String> contentList = myIndexFieldContentList(data);
        if (CollectionUtils.isEmpty(contentList)) {
            contentList = Collections.singletonList(data);
        }
        return contentList;
    }

    protected List<String> myIndexFieldContentList(String data) {
        return null;
    }

    @Override
    public Boolean isNeedSliceWord() {
        return true;
    }

    @Override
    public Boolean isUseFormConfig() {
        return true;
    }

    @Override
    public JSONObject getDetailedData(AttributeDataVo attributeDataVo, JSONObject configObj) {
        return getMyDetailedData(attributeDataVo, configObj);
    }

    protected abstract JSONObject getMyDetailedData(AttributeDataVo attributeDataVo, JSONObject configObj);

    @Override
    public String getHandlerName() {
        return null;
    }

    @Override
    public String getIcon() {
        return null;
    }

    @Override
    public String getDataType() {
        return null;
    }

    @Override
    public boolean isShowable() {
        return false;
    }

    @Override
    public boolean isValueable() {
        return false;
    }

    @Override
    public boolean isFilterable() {
        return false;
    }

    @Override
    public boolean isExtendable() {
        return false;
    }

    @Override
    public boolean isForTemplate() {
        return false;
    }

    @Override
    public String getModule() {
        return null;
    }

    @Override
    public int getSort() {
        return 100;
    }

    protected String convertToString(Object source, String attributeLabel) {
        if (source == null) {
            return null;
        }
        if (source instanceof String) {
            return (String) source;
        }
        throw new AttributeValidException(attributeLabel);
    }

    protected JSONArray convertToJSONArray(Object source, String attributeLabel) {
        if (source == null) {
            return null;
        }
        if (source instanceof String) {
            try {
                return JSONObject.parseArray((String) source);
            } catch (Exception e) {
                throw new AttributeValidException(attributeLabel);
            }
        } else if (source instanceof JSONArray) {
            return (JSONArray) source;
        }
        throw new AttributeValidException(attributeLabel);
    }
}
