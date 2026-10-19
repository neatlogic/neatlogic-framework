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

package neatlogic.framework.integration.dto;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.restful.annotation.EntityField;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PatternVo implements Serializable {

    private static final long serialVersionUID = 8687343787716050495L;

    public PatternVo() {

    }

    /** 直接复制字段避免触发 typeName 懒加载，子结构及默认值也与处理器单例隔离。 */
    public PatternVo(PatternVo source) {
        name = source.name;
        key = source.key;
        type = source.type;
        mode = source.mode;
        typeName = source.typeName;
        isRequired = source.isRequired;
        description = source.description;
        isShow = source.isShow;
        if (source.children != null) {
            children = new ArrayList<>();
            for (PatternVo child : source.children) {
                children.add(child == null ? null : new PatternVo(child));
            }
        }
        if (source.defaultConfig != null) {
            defaultConfig = JSONObject.parseObject(JSONObject.toJSONString(source.defaultConfig, SerializerFeature.WriteMapNullValue));
        }
        if (source.options != null) {
            options = JSONArray.parseArray(JSONArray.toJSONString(source.options, SerializerFeature.WriteMapNullValue));
        }
    }

    public PatternVo(String _name, String _mode, ApiParamType _type, Integer _isRequired, String _description) {
        name = _name;
        type = _type.getValue();
        mode = _mode;
        isRequired = _isRequired;
        description = _description;
    }

    public PatternVo(String _name, String _mode, ApiParamType _type, Integer _isRequired, String _description, Integer _isShow) {
        name = _name;
        type = _type.getValue();
        mode = _mode;
        isRequired = _isRequired;
        description = _description;
        isShow = _isShow;
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
    @EntityField(name = "是否显示", type = ApiParamType.INTEGER)
    private Integer isShow = 1;
    // 默认值属于当前集成，固定处理器定义仅声明可用的枚举选项和结构。
    @EntityField(name = "当前集成的默认值配置", type = ApiParamType.JSONOBJECT)
    private JSONObject defaultConfig;
    @EntityField(name = "固定枚举选项", type = ApiParamType.JSONARRAY)
    private JSONArray options;

    public JSONObject getDefaultConfig() {
        return defaultConfig;
    }

    public void setDefaultConfig(JSONObject defaultConfig) {
        this.defaultConfig = defaultConfig;
    }

    public JSONArray getOptions() {
        return options;
    }

    public void setOptions(JSONArray options) {
        this.options = options;
    }

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

    public Integer getIsShow() {
        return isShow;
    }

    public void setIsShow(Integer isShow) {
        this.isShow = isShow;
    }
}
