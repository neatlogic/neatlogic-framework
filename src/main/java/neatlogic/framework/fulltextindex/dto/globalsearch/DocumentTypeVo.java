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
