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
