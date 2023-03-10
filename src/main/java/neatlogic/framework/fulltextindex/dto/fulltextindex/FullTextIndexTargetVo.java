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

package neatlogic.framework.fulltextindex.dto.fulltextindex;

import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.restful.annotation.EntityField;

import java.util.HashSet;
import java.util.Set;

public class FullTextIndexTargetVo {
    @EntityField(name = "模块id", type = ApiParamType.STRING)
    private String moduleId;
    @EntityField(name = "目标id", type = ApiParamType.LONG)
    private Long targetId;
    @EntityField(name = "目标类型", type = ApiParamType.STRING)
    private String targetType;
    @EntityField(name = "查询命中次数", type = ApiParamType.INTEGER)
    private int hitCount;
    @EntityField(name = "用户点击次数", type = ApiParamType.INTEGER)
    private int clickCount;
    @EntityField(name = "异常", type = ApiParamType.INTEGER)
    private String error;
    private Set<FullTextIndexWordVo> wordList;
    private Set<FullTextIndexFieldWordVo> fieldList;

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public Long getTargetId() {
        return targetId;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }

    public Set<FullTextIndexWordVo> getWordList() {
        return wordList;
    }

    public void addWord(FullTextIndexWordVo wordVo) {
        if (wordList == null) {
            wordList = new HashSet<>();
        }
        wordList.add(wordVo);
    }

    public void adField(FullTextIndexFieldWordVo fieldVo) {
        if (fieldList == null) {
            fieldList = new HashSet<>();
        }
        fieldList.add(fieldVo);
    }

    public void setWordList(Set<FullTextIndexWordVo> wordList) {
        this.wordList = wordList;
    }

    public Set<FullTextIndexFieldWordVo> getFieldList() {
        return fieldList;
    }

    public void setFieldList(Set<FullTextIndexFieldWordVo> fieldList) {
        this.fieldList = fieldList;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public int getHitCount() {
        return hitCount;
    }

    public void setHitCount(int hitCount) {
        this.hitCount = hitCount;
    }

    public int getClickCount() {
        return clickCount;
    }

    public void setClickCount(int clickCount) {
        this.clickCount = clickCount;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
