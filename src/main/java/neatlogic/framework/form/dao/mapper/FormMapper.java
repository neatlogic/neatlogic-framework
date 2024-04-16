/*Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

package neatlogic.framework.form.dao.mapper;

import neatlogic.framework.common.dto.BasePageVo;
import neatlogic.framework.common.dto.ValueTextVo;
import neatlogic.framework.form.dto.*;
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

    int insertFormAttributeData(AttributeDataVo attributeDataVo);

    int deleteFormAttributeByFormUuid(String formUuid);

    int deleteFormByUuid(String uuid);

    int deleteFormVersionByFormUuid(String formUuid);

    int deleteFormVersionByUuid(String uuid);

    void deleteFormCustomItem(Long id);

    int deleteFormAttributeDataByIdList(List<Long> idList);
}
