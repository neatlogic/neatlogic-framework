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

package neatlogic.framework.fulltextindex.dto.globalsearch;

import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.common.dto.BasePageVo;
import neatlogic.framework.fulltextindex.core.FullTextIndexHandlerFactory;
import neatlogic.framework.fulltextindex.dto.fulltextindex.FullTextIndexTypeVo;
import neatlogic.framework.restful.annotation.EntityField;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Optional;

public class DocumentTypeVo extends BasePageVo {
    @EntityField(name = "类型", type = ApiParamType.STRING)
    private String type;
    @EntityField(name = "类型名称", type = ApiParamType.STRING)
    private String typeName;
    @EntityField(name = "结果列表", type = ApiParamType.JSONARRAY)
    private List<DocumentVo> documentList;
    //补充以下属性是因为需要饶过基础类的序列化禁用标记
    @EntityField(name = "每页条数", type = ApiParamType.INTEGER)
    private Integer pageSize = 20;
    @EntityField(name = "页数", type = ApiParamType.INTEGER)
    private Integer pageCount = 0;
    @EntityField(name = "总条数", type = ApiParamType.INTEGER)
    private Integer rowNum = 0;
    @EntityField(name = "当前页数", type = ApiParamType.INTEGER)
    private Integer currentPage = 1;


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTypeName() {
        if (StringUtils.isBlank(typeName) && StringUtils.isNotBlank(type)) {
            List<FullTextIndexTypeVo> fullTextIndexTypeList = FullTextIndexHandlerFactory.getAllTypeList();
            Optional<FullTextIndexTypeVo> op = fullTextIndexTypeList.stream().filter(d -> d.getType().equals(this.type)).findFirst();
            op.ifPresent(fullTextIndexTypeVo -> typeName = fullTextIndexTypeVo.getTypeName());
        }
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public List<DocumentVo> getDocumentList() {
        return documentList;
    }

    public void setDocumentList(List<DocumentVo> documentList) {
        this.documentList = documentList;
    }
}
