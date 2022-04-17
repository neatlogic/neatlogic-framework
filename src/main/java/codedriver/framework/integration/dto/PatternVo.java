/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.integration.dto;

import codedriver.framework.common.constvalue.ApiParamType;
import codedriver.framework.restful.annotation.EntityField;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PatternVo implements Serializable {

    private static final long serialVersionUID = 8687343787716050495L;

    public PatternVo() {

    }

    public PatternVo(String _name, String _mode, ApiParamType _type, Integer _isRequired, String _description) {
        name = _name;
        type = _type.getValue();
        mode = _mode;
        isRequired = _isRequired;
        description = _description;
    }

    public PatternVo(String _name, String _mode, ApiParamType _type) {
        name = _name;
        mode = _mode;
        type = _type.getValue();
        isRequired = 0;
    }

    @EntityField(name = "参数名", type = ApiParamType.STRING)
    private String name;
    @EntityField(name = "唯一键，名称和类型的组合", type = ApiParamType.STRING)
    private String key;
    @EntityField(name = "数据类型", type = ApiParamType.STRING)
    private String type;
    @EntityField(name = "参数类型", type = ApiParamType.STRING)
    private String mode;
    @EntityField(name = "参数类型名称", type = ApiParamType.STRING)
    private String typeName;
    @EntityField(name = "子参数", type = ApiParamType.JSONOBJECT)
    private List<PatternVo> children;
    @EntityField(name = "是否必填", type = ApiParamType.INTEGER)
    private Integer isRequired;
    @EntityField(name = "描述", type = ApiParamType.STRING)
    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTypeName() {
        if (StringUtils.isBlank(typeName) && StringUtils.isNotBlank(type)) {
            typeName = ApiParamType.getText(type);
        }
        return typeName;
    }

    public String getKey() {
        return this.name + "_" + this.mode;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public List<PatternVo> getChildren() {
        return children;
    }

    public void setChildren(List<PatternVo> childPatternList) {
        this.children = childPatternList;
    }

    public void addChild(PatternVo patternVo) {
        if (this.children == null) {
            this.children = new ArrayList<>();
        }
        this.children.add(patternVo);
    }

    public Integer getIsRequired() {
        return isRequired;
    }

    public void setIsRequired(Integer isRequired) {
        this.isRequired = isRequired;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

}
