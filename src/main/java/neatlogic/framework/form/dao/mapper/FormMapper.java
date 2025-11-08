/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */

package neatlogic.framework.form.dao.mapper;

import neatlogic.framework.common.dto.BasePageVo;
import neatlogic.framework.common.dto.ValueTextVo;
import neatlogic.framework.form.dto.*;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component("processFormMapper")
public interface FormMapper  {
    int checkFormCustomItemNameIsExists(FormCustomItemVo formCustomItemVo);

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

    FormVo getFormByName(String formName);

    FormVersionVo getFormVersionByUuid(String formVersionUuid);

    FormVersionVo getFormActiveVersionByFormUuid(String formUuid);

    List<FormVersionVo> getFormVersionByFormUuid(String formUuid);

    List<FormVersionVo> getFormVersionSimpleByFormUuid(String formUuid);

    Integer getMaxVersionByFormUuid(String formUuid);

    int checkFormIsExists(String uuid);

    int checkFormNameIsRepeat(FormVo formVo);

    int checkFormVersionIsExists(String uuid);

    FormAttributeVo getFormAttributeByUuid(String uuid);

    List<FormAttributeVo> getFormAttributeList(FormAttributeVo formAttributeVo);

    List<FormAttributeVo> getFormAttributeListByFormUuidList(List<String> formUuidList);

    List<FormAttributeVo> getFormExtendAttributeListByFormUuidAndFormVersionUuid(@Param("formUuid") String formUuid, @Param("formVersionUuid") String formVersionUuid);

    int getFormAttributeMatrixCount();

    List<Map<String, Object>> getFormAttributeMatrixList(BasePageVo searchVo);

    List<FormVersionVo> getFormVersionListByFormConfigLikeKeyword(String value);

    List<FormVersionVo> getFormVersionList();

    List<AttributeDataVo> getFormAttributeDataListByProcessTaskId(Long processTaskId);

    List<AttributeDataVo> getFormAttributeDataListByIdList(List<Long> idList);

    int insertForm(FormVo formVo);

    int resetFormVersionIsActiveByFormUuid(String formUuid);

    int updateFormVersion(FormVersionVo formVersionVo);

    int updateFormVersionConfigByUuid(FormVersionVo formVersionVo);

    void updateForm(FormVo formVo);

    void updateFormCustomItem(FormCustomItemVo formCustomItemVo);

    void insertFormCustomItem(FormCustomItemVo formCustomItemVo);

    int insertFormVersion(FormVersionVo formVersionVo);

    int insertFormAttribute(FormAttributeVo formAttributeVo);

    int insertFormExtendAttribute(FormAttributeVo formAttributeVo);

    int insertFormAttributeData(AttributeDataVo attributeDataVo);

    int insertFormAttributeDataList(List<? extends AttributeDataVo> attributeDataVo);

    int insertFormExtendAttributeData(AttributeDataVo attributeDataVo);

    int insertFormExtendAttributeDataList(List<? extends AttributeDataVo> attributeDataList);

    int deleteFormAttributeByFormUuid(String formUuid);

    int deleteFormByUuid(String uuid);

    int deleteFormVersionByFormUuid(String formUuid);

    int deleteFormVersionByUuid(String uuid);

    void deleteFormCustomItem(Long id);

    int deleteFormAttributeDataByIdList(List<Long> idList);

    int deleteFormExtendAttributeDataByIdList(List<Long> idList);

    int deleteFormExtendAttributeByFormUuidAndFormVersionUuid(@Param("formUuid") String formUuid, @Param("formVersionUuid") String currentVersionUuid);
}
