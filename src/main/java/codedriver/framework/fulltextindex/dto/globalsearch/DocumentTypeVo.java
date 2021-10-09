/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.fulltextindex.dto.globalsearch;

import codedriver.framework.common.constvalue.ApiParamType;
import codedriver.framework.common.dto.BasePageVo;
import codedriver.framework.fulltextindex.core.FullTextIndexHandlerFactory;
import codedriver.framework.fulltextindex.dto.fulltextindex.FullTextIndexTypeVo;
import codedriver.framework.restful.annotation.EntityField;
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
