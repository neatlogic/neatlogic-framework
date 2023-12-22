/*
 * Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package neatlogic.framework.form.dao.mapper;

import neatlogic.framework.common.dto.BasePageVo;
import neatlogic.framework.common.dto.ValueTextVo;
import neatlogic.framework.form.dto.FormAttributeVo;
import neatlogic.framework.form.dto.FormCustomItemVo;
import neatlogic.framework.form.dto.FormVersionVo;
import neatlogic.framework.form.dto.FormVo;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component("processFormMapper")
public interface FormMapper {
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

    List<FormVersionVo> getFormVersionByFormUuid(String formUuid);

    List<FormVersionVo> getFormVersionSimpleByFormUuid(String formUuid);

    Integer getMaxVersionByFormUuid(String formUuid);

    int checkFormIsExists(String uuid);

    int checkFormNameIsRepeat(FormVo formVo);

    int checkFormVersionIsExists(String uuid);

    List<FormAttributeVo> getFormAttributeList(FormAttributeVo formAttributeVo);

    List<FormAttributeVo> getFormAttributeListByFormUuidList(List<String> formUuidList);

    int getFormAttributeMatrixCount();

    List<Map<String, Object>> getFormAttributeMatrixList(BasePageVo searchVo);

    List<FormVersionVo> getFormVersionListByFormConfigLikeKeyword(String value);

    List<FormVersionVo> getFormVersionList();

    int insertForm(FormVo formVo);

    int resetFormVersionIsActiveByFormUuid(String formUuid);

    int updateFormVersion(FormVersionVo formVersionVo);

    int updateFormVersionConfigByUuid(FormVersionVo formVersionVo);

    void updateForm(FormVo formVo);

    void updateFormCustomItem(FormCustomItemVo formCustomItemVo);

    void insertFormCustomItem(FormCustomItemVo formCustomItemVo);

    int insertFormVersion(FormVersionVo formVersionVo);

    int insertFormAttribute(FormAttributeVo formAttributeVo);

    int deleteFormAttributeByFormUuid(String formUuid);

    int deleteFormByUuid(String uuid);

    int deleteFormVersionByFormUuid(String formUuid);

    int deleteFormVersionByUuid(String uuid);

    void deleteFormCustomItem(Long id);
}
