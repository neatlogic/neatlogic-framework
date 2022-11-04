/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.form.dao.mapper;

import codedriver.framework.common.dto.ValueTextVo;
import codedriver.framework.form.dto.*;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("processFormMapper")
public interface FormMapper {

    List<FormCustomItemVo> searchFormCustomItem(FormCustomItemVo formCustomItemVo);

    int searchFormCustomItemCount(FormCustomItemVo formCustomItemVo);

    FormCustomItemVo getFormCustomItemByName(String name);

    FormCustomItemVo getFormCustomItemById(Long id);

    FormVersionVo getActionFormVersionByFormUuid(String formUuid);

    List<FormVo> searchFormList(FormVo formVo);

    List<FormVo> getFormListByUuidList(List<String> uuid);

    List<ValueTextVo> searchFormListForSelect(FormVo formVo);

    int searchFormCount(FormVo formVo);

    FormVo getFormByUuid(String formUuid);

    FormVersionVo getFormVersionByUuid(String formVersionUuid);

    List<FormVersionVo> getFormVersionByFormUuid(String formUuid);

    List<FormVersionVo> getFormVersionSimpleByFormUuid(String formUuid);

    Integer getMaxVersionByFormUuid(String formUuid);

    int checkFormIsExists(String uuid);

    int checkFormNameIsRepeat(FormVo formVo);

    int checkFormVersionIsExists(String uuid);

    List<FormAttributeVo> getFormAttributeList(FormAttributeVo formAttributeVo);

    List<FormAttributeVo> getFormAttributeListByFormUuidList(List<String> formUuidList);

    List<FormAttributeMatrixVo> getFormAttributeMatrixByMatrixUuid(@Param("matrixUuid") String matrixUuid, @Param("startNum") int startNum, @Param("pageSize") int pageSize);

    List<FormVersionVo> getFormVersionListByFormConfigLikeKeyword(String value);

    List<FormVersionVo> getFormVersionList();

    int insertForm(FormVo formVo);

    int insertFormAttributeMatrix(FormAttributeMatrixVo componentVo);

    int resetFormVersionIsActiveByFormUuid(String formUuid);

    int updateFormVersion(FormVersionVo formVersionVo);

    int updateFormVersionConfigByUuid(FormVersionVo formVersionVo);

    void updateForm(FormVo formVo);

    int updateFormAttributeConfig(FormAttributeVo formAttributeVo);

    int insertFormVersion(FormVersionVo formVersionVo);

    int insertFormAttribute(FormAttributeVo formAttributeVo);

    int deleteFormAttributeByFormUuid(String formUuid);

    int deleteFormByUuid(String uuid);

    int deleteFormVersionByFormUuid(String formUuid);

    int deleteFormVersionByUuid(String uuid);

    int deleteFormAttributeMatrixByFormVersionUuid(String formVersionUuid);
}
