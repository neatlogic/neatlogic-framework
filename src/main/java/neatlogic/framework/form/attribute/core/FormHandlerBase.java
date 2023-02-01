/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.framework.form.attribute.core;

import neatlogic.framework.common.constvalue.FormHandlerType;
import neatlogic.framework.common.constvalue.GroupSearch;
import neatlogic.framework.common.dto.ValueTextVo;
import neatlogic.framework.form.dto.AttributeDataVo;
import neatlogic.framework.matrix.core.IMatrixDataSourceHandler;
import neatlogic.framework.matrix.core.MatrixDataSourceHandlerFactory;
import neatlogic.framework.matrix.dao.mapper.MatrixMapper;
import neatlogic.framework.matrix.dto.MatrixDataVo;
import neatlogic.framework.matrix.dto.MatrixVo;
import neatlogic.framework.matrix.exception.MatrixDataSourceHandlerNotFoundException;
import neatlogic.framework.matrix.exception.MatrixNotFoundException;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public abstract class FormHandlerBase implements IFormAttributeHandler, IFormAttributeDataConversionHandler {

    public enum ConversionType {

        TOVALUE("toValue", "text转换成value"),
        TOTEXT("toText", "value转换成text");

        private String value;
        private String text;

        public String getValue() {
            return value;
        }

        public String getText() {
            return text;
        }

        private ConversionType(String _value, String _text) {
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

    protected String getValue(String matrixUuid, ValueTextVo mapping, String value) {
        if (StringUtils.isBlank(value)) {
            return value;
        }
        try {
            MatrixVo matrixVo = matrixMapper.getMatrixByUuid(matrixUuid);
            if (matrixVo == null) {
                throw new MatrixNotFoundException(matrixUuid);
            }
            IMatrixDataSourceHandler matrixDataSourceHandler = MatrixDataSourceHandlerFactory.getHandler(matrixVo.getType());
            if (matrixDataSourceHandler == null) {
                throw new MatrixDataSourceHandlerNotFoundException(matrixVo.getType());
            }
            MatrixDataVo dataVo = new MatrixDataVo();
            dataVo.setMatrixUuid(matrixUuid);
            List<String> columnList = new ArrayList<>();
            columnList.add((String) mapping.getValue());
            columnList.add(mapping.getText());
            dataVo.setColumnList(columnList);
            List<Map<String, JSONObject>> tbodyList = matrixDataSourceHandler.searchTableColumnData(dataVo);
            for (Map<String, JSONObject> firstObj : tbodyList) {
                JSONObject valueObj = firstObj.get(mapping.getValue());
                /** 当text与value字段相同时，不同类型的矩阵字段，拼接value的逻辑不同，下拉、用户、组、角色，按uuid&=&text拼接，其余按value&=&value拼接 **/
                if (mapping.getValue().equals(mapping.getText())
                        && (GroupSearch.USER.getValue().equals(valueObj.getString("type"))
                        || GroupSearch.ROLE.getValue().equals(valueObj.getString("type"))
                        || GroupSearch.TEAM.getValue().equals(valueObj.getString("type"))
                        || FormHandlerType.SELECT.toString().equals(valueObj.getString("type")))
                        && value.equals(valueObj.getString("text"))) {
                    return valueObj.getString("value") + IFormAttributeHandler.SELECT_COMPOSE_JOINER + valueObj.getString("text");
                } else if (mapping.getValue().equals(mapping.getText()) && value.equals(valueObj.getString("text"))) {
                    return valueObj.getString("value") + IFormAttributeHandler.SELECT_COMPOSE_JOINER + valueObj.getString("value");
                }
                if (valueObj.getString("compose").contains(value)) {
                    return valueObj.getString("compose");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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
}