package codedriver.framework.matrix.dto;

import codedriver.framework.file.dto.FileVo;
import codedriver.framework.matrix.core.MatrixTypeFactory;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import codedriver.framework.common.constvalue.ApiParamType;
import codedriver.framework.common.dto.BaseEditorVo;
import codedriver.framework.restful.annotation.EntityField;

/**
 * @program: codedriver
 * @description:
 * @create: 2020-03-26 18:59
 **/
public class MatrixVo extends BaseEditorVo {
    @EntityField( name = "自增ID", type = ApiParamType.LONG)
    private Long id;
    @EntityField( name = "唯一主键uuid", type = ApiParamType.STRING)
    private String uuid;
    @EntityField( name = "数据源名称", type = ApiParamType.STRING)
    private String name;
    @EntityField( name = "唯一标识", type = ApiParamType.STRING)
    private String label;
    @EntityField( name = "类型",  type = ApiParamType.STRING)
    private String type;
    @EntityField( name = "类型名称", type = ApiParamType.STRING)
    private String typeName;
    @EntityField( name = "被引用次数", type = ApiParamType.INTEGER)
    private int referenceCount;
    @EntityField( name = "集成设置uuid", type = ApiParamType.STRING)
    private String integrationUuid;
    @EntityField( name = "视图配置文件id", type = ApiParamType.LONG)
    private Long fileId;
    @EntityField( name = "视图配置文件信息", type = ApiParamType.JSONOBJECT)
    private FileVo fileVo;
    @EntityField( name = "ci模型id", type = ApiParamType.LONG)
    private Long ciId;
    @EntityField( name = "配置信息", type = ApiParamType.JSONOBJECT)
    private JSONObject config;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public String getTypeName() {
        if (StringUtils.isNotBlank(type)){
            return MatrixTypeFactory.getName(type);
        }
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public int getReferenceCount() {
        return referenceCount;
    }

    public void setReferenceCount(int referenceCount) {
        this.referenceCount = referenceCount;
    }

    public String getIntegrationUuid() {
        return integrationUuid;
    }

    public void setIntegrationUuid(String integrationUuid) {
        this.integrationUuid = integrationUuid;
    }

    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    public FileVo getFileVo() {
        return fileVo;
    }

    public void setFileVo(FileVo fileVo) {
        this.fileVo = fileVo;
    }

    public Long getCiId() {
        return ciId;
    }

    public void setCiId(Long ciId) {
        this.ciId = ciId;
    }

    public JSONObject getConfig() {
        return config;
    }

    public void setConfig(JSONObject config) {
        this.config = config;
    }
}
