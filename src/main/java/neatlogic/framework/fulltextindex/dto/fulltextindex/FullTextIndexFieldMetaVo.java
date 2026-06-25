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

package neatlogic.framework.fulltextindex.dto.fulltextindex;

import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.restful.annotation.EntityField;

public class FullTextIndexFieldMetaVo {
    @EntityField(name = "字段名称", type = ApiParamType.STRING)
    private String name;
    @EntityField(name = "字段路径", type = ApiParamType.STRING)
    private String path;
    @EntityField(name = "字段类型", type = ApiParamType.STRING)
    private String type;
    @EntityField(name = "分词器", type = ApiParamType.STRING)
    private String analyzer;
    @EntityField(name = "搜索分词器", type = ApiParamType.STRING)
    private String searchAnalyzer;
    @EntityField(name = "标准化器", type = ApiParamType.STRING)
    private String normalizer;
    @EntityField(name = "日期格式", type = ApiParamType.STRING)
    private String format;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAnalyzer() {
        return analyzer;
    }

    public void setAnalyzer(String analyzer) {
        this.analyzer = analyzer;
    }

    public String getSearchAnalyzer() {
        return searchAnalyzer;
    }

    public void setSearchAnalyzer(String searchAnalyzer) {
        this.searchAnalyzer = searchAnalyzer;
    }

    public String getNormalizer() {
        return normalizer;
    }

    public void setNormalizer(String normalizer) {
        this.normalizer = normalizer;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }
}
