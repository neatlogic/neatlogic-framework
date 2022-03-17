/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.form.dao.mapper;

import java.util.List;

import codedriver.framework.common.dto.ValueTextVo;
import codedriver.framework.form.dto.FormAttributeMatrixVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import codedriver.framework.form.dto.FormAttributeVo;
import codedriver.framework.form.dto.FormVersionVo;
import codedriver.framework.form.dto.FormVo;

@Component("processFormMapper")
public interface FormMapper {
    public FormVersionVo getActionFormVersionByFormUuid(String formUuid);

    public List<FormVo> searchFormList(FormVo formVo);

    public List<ValueTextVo> searchFormListForSelect(FormVo formVo);

    public int searchFormCount(FormVo formVo);

    public FormVo getFormByUuid(String formUuid);

    public FormVersionVo getFormVersionByUuid(String formVersionUuid);

    public List<FormVersionVo> getFormVersionByFormUuid(String formUuid);

    public List<FormVersionVo> getFormVersionSimpleByFormUuid(String formUuid);

    public Integer getMaxVersionByFormUuid(String formUuid);

    public int checkFormIsExists(String uuid);

    public int checkFormNameIsRepeat(FormVo formVo);

    public int checkFormVersionIsExists(String uuid);

    public List<FormAttributeVo> getFormAttributeList(FormAttributeVo formAttributeVo);

    public List<FormAttributeVo> getFormAttributeListByFormUuidList(List<String> formUuidList);

    public List<FormAttributeMatrixVo> getFormAttributeMatrixByMatrixUuid(@Param("matrixUuid") String matrixUuid, @Param("startNum") int startNum, @Param("pageSize") int pageSize);

    List<FormVersionVo> getFormVersionListByFormConfigLikeKeyword(String value);

    public int insertForm(FormVo formVo);

    public int insertFormAttributeMatrix(FormAttributeMatrixVo componentVo);

    public int resetFormVersionIsActiveByFormUuid(String formUuid);

    public int updateFormVersion(FormVersionVo formVersionVo);

    int updateFormVersionConfigByUuid(FormVersionVo formVersionVo);

    public void updateForm(FormVo formVo);

    int updateFormAttributeConfig(FormAttributeVo formAttributeVo);

    public int insertFormVersion(FormVersionVo formVersionVo);

    public int insertFormAttribute(FormAttributeVo formAttributeVo);

    public int deleteFormAttributeByFormUuid(String formUuid);

    public int deleteFormByUuid(String uuid);

    public int deleteFormVersionByFormUuid(String formUuid);

    public int deleteFormVersionByUuid(String uuid);

    public int deleteFormAttributeMatrixByFormVersionUuid(String formVersionUuid);
}
