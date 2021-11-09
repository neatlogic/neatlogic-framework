/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.module.framework.matrix.service;

import codedriver.framework.exception.core.ApiRuntimeException;
import codedriver.framework.exception.util.FreemarkerTransformException;
import codedriver.framework.integration.dto.IntegrationResultVo;
import codedriver.framework.integration.dto.IntegrationVo;
import codedriver.framework.matrix.dto.MatrixAttributeVo;
import codedriver.framework.matrix.dto.MatrixDataVo;
import com.alibaba.fastjson.JSONObject;

import java.util.List;
import java.util.Map;

public interface MatrixService {

	public List<MatrixAttributeVo> getExternalMatrixAttributeList(String matrixUuid, IntegrationVo integrationVo);
    
    public List<Map<String, Object>> matrixTableDataValueHandle(List<MatrixAttributeVo> attributeVoList, List<Map<String, String>> valueList);
    
    public JSONObject matrixAttributeValueHandle(MatrixAttributeVo matrixAttributeVo, Object value);
    
    public JSONObject matrixAttributeValueHandle(Object value);
    
    public List<Map<String, String>> matrixAttributeValueKeyWordSearch(MatrixAttributeVo matrixAttributeVo, MatrixDataVo dataVo);
    
    public List<Map<String, JSONObject>> getExternalDataTbodyList(IntegrationResultVo resultVo, List<String> columnList);
    /**
     * 
    * @Time:2020年7月8日
    * @Description: 将arrayColumnList包含的属性值转成数组
    * @param arrayColumnList 需要将值转化成数组的属性集合
    * @param tbodyList 表格数据
    * @return void
     */
    public void arrayColumnDataConversion(List<String> arrayColumnList, List<Map<String, JSONObject>> tbodyList);
    /**
     * 
    * @Time:2020年12月1日
    * @Description: 矩阵属性值合法性校验 
    * @param matrixAttributeVo
    * @param value
    * @return boolean
     */
    public boolean matrixAttributeValueVerify(MatrixAttributeVo matrixAttributeVo, String value);

    /**
     * 校验集成接口数据是否符合矩阵格式
     * @param integrationUuid 集成配置uuid
     * @throws ApiRuntimeException
     */
    public void validateMatrixExternalData(String integrationUuid) throws ApiRuntimeException;

    List<MatrixAttributeVo> buildView(String matrixUuid, String matrixName, String xml);
}
