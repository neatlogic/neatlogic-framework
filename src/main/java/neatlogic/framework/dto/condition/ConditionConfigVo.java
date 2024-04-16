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

package neatlogic.framework.dto.condition;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.common.dto.BaseEditorVo;
import neatlogic.framework.restful.annotation.EntityField;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ConditionConfigVo extends BaseEditorVo implements Serializable {

    private static final long serialVersionUID = 5439300427812355573L;

    protected List<ConditionGroupVo> conditionGroupList = new ArrayList<>();

    @JSONField(serialize = false)
    private Map<String, ConditionGroupVo> conditionGroupMap;
    protected List<ConditionGroupRelVo> conditionGroupRelList = new ArrayList<>();

    @EntityField(name = "搜索模式：value|text，默认搜索value", type = ApiParamType.STRING)
    private String searchMode;


    public ConditionConfigVo() {
    }

    public ConditionConfigVo(JSONObject jsonObj) {
        init(jsonObj);
    }

    public void init(JSONObject jsonObj) {
        if (jsonObj != null) {
            conditionGroupList.clear();
            conditionGroupRelList.clear();
            JSONArray conditionGroupArray = jsonObj.getJSONArray("conditionGroupList");
            if (CollectionUtils.isNotEmpty(conditionGroupArray)) {
                conditionGroupMap = new HashMap<>();
                for (int i = 0; i < conditionGroupArray.size(); i++) {
                    JSONObject conditionGroupJson = conditionGroupArray.getJSONObject(i);
                    ConditionGroupVo conditionGroupVo = new ConditionGroupVo(conditionGroupJson);
                    conditionGroupList.add(conditionGroupVo);
                    conditionGroupMap.put(conditionGroupVo.getUuid(), conditionGroupVo);
                }
                JSONArray conditionGroupRelArray = jsonObj.getJSONArray("conditionGroupRelList");
                if (CollectionUtils.isNotEmpty(conditionGroupRelArray)) {
                    for (int i = 0; i < conditionGroupRelArray.size(); i++) {
                        JSONObject conditionRelGroup = conditionGroupRelArray.getJSONObject(i);
                        conditionGroupRelList.add(new ConditionGroupRelVo(conditionRelGroup));
                    }
                }
            }
        }
    }


    public List<ConditionGroupVo> getConditionGroupList() {
        return conditionGroupList;
    }

    public void setConditionGroupList(List<ConditionGroupVo> conditionGroupList) {
        this.conditionGroupList = conditionGroupList;
    }

    public Map<String, ConditionGroupVo> getConditionGroupMap() {
        if (MapUtils.isEmpty(conditionGroupMap) && CollectionUtils.isNotEmpty(conditionGroupList)) {
            conditionGroupMap = conditionGroupList.stream().collect(Collectors.toMap(ConditionGroupVo::getUuid, e -> e));
        }
        return conditionGroupMap;
    }

    public List<ConditionGroupRelVo> getConditionGroupRelList() {
        return conditionGroupRelList;
    }

    public void setConditionGroupRelList(List<ConditionGroupRelVo> conditionGroupRelList) {
        this.conditionGroupRelList = conditionGroupRelList;
    }

    public String buildScript() {
        if (CollectionUtils.isNotEmpty(conditionGroupRelList)) {
            StringBuilder script = new StringBuilder();
            script.append("(");
            String toUuid = null;
            for (ConditionGroupRelVo conditionGroupRelVo : conditionGroupRelList) {
                script.append(getConditionGroupMap().get(conditionGroupRelVo.getFrom()).buildScript());
                script.append("and".equals(conditionGroupRelVo.getJoinType()) ? " && " : " || ");
                toUuid = conditionGroupRelVo.getTo();
            }
            script.append(getConditionGroupMap().get(toUuid).buildScript());
            script.append(")");
            return script.toString();
        } else {
            ConditionGroupVo conditionGroupVo = conditionGroupList.get(0);
            return conditionGroupVo.buildScript();
        }
    }

    public void buildConditionWhereSql(StringBuilder sqlSb, ConditionConfigVo conditionConfigVo) {
        // 将group 以连接表达式 存 Map<fromUuid_toUuid,joinType>
        Map<String, String> groupRelMap = new HashMap<>();
        List<ConditionGroupRelVo> groupRelList = conditionConfigVo.getConditionGroupRelList();
        if (CollectionUtils.isNotEmpty(groupRelList)) {
            for (ConditionGroupRelVo groupRel : groupRelList) {
                groupRelMap.put(groupRel.getFrom() + "_" + groupRel.getTo(), groupRel.getJoinType());
            }
        }
        List<ConditionGroupVo> groupList = conditionConfigVo.getConditionGroupList();
        if (CollectionUtils.isNotEmpty(groupList)) {
            String fromGroupUuid = null;
            String toGroupUuid = groupList.get(0).getUuid();
            boolean isAddedAnd = false;
            for (ConditionGroupVo groupVo : groupList) {
                // 将condition 以连接表达式 存 Map<fromUuid_toUuid,joinType>
                Map<String, String> conditionRelMap = new HashMap<>();
                List<ConditionRelVo> conditionRelList = groupVo.getConditionRelList();
                if (CollectionUtils.isNotEmpty(conditionRelList)) {
                    for (ConditionRelVo conditionRel : conditionRelList) {
                        conditionRelMap.put(conditionRel.getFrom() + "_" + conditionRel.getTo(),
                                conditionRel.getJoinType());
                    }
                }
                //append joinType
                if (fromGroupUuid != null) {
                    toGroupUuid = groupVo.getUuid();
                    sqlSb.append(groupRelMap.get(fromGroupUuid + "_" + toGroupUuid));
                }
                List<ConditionVo> conditionVoList = groupVo.getConditionList();
                if (!isAddedAnd && CollectionUtils.isNotEmpty((conditionVoList))) {
                    //补充整体and 结束左括号
                    sqlSb.append(" and (");
                    isAddedAnd = true;
                }
                sqlSb.append(" ( ");
                String fromConditionUuid = null;
                String toConditionUuid;
                for (int i = 0; i < conditionVoList.size(); i++) {
                    ConditionVo conditionVo = conditionVoList.get(i);
                    //append joinType
                    toConditionUuid = conditionVo.getUuid();
                    if (MapUtils.isNotEmpty(conditionRelMap) && StringUtils.isNotBlank(fromConditionUuid)) {
                        sqlSb.append(conditionRelMap.get(fromConditionUuid + "_" + toConditionUuid));
                    }
                    //append condition
                    String handler = conditionVo.getName();
                    //如果是form
                    buildMyConditionWhereSql(sqlSb, handler, conditionVoList, i,conditionConfigVo.getSearchMode());
                    fromConditionUuid = toConditionUuid;
                }
                sqlSb.append(" ) ");
                fromGroupUuid = toGroupUuid;

            }
            //补充整体and 结束右括号
            if (isAddedAnd) {
                sqlSb.append(" ) ");
            }
        }
    }

    /**
     *
     * @param sqlSb sql stringBuilder
     * @param handler 条件处理器
     * @param conditionVoList 所有条件
     * @param conditionIndex 当前条件下标
     * @param searchMode 搜索模式：value|text，默认搜索value
     */
    public void buildMyConditionWhereSql(StringBuilder sqlSb, String handler, List<ConditionVo> conditionVoList, int conditionIndex, String searchMode) {

    }

    public String getSearchMode() {
        if(StringUtils.isBlank(searchMode)){
            searchMode = "value";
        }
        return searchMode;
    }

    public void setSearchMode(String searchMode) {
        this.searchMode = searchMode;
    }

    /**
     * 将过滤器高级模式的结构转换自然语言表达式
     * @return
     */
    public String getBuildNaturalLanguageExpressions() {
        List<ConditionGroupVo> conditionGroupList = this.getConditionGroupList();
        Map<String, ConditionGroupVo> conditionGroupMap = this.getConditionGroupMap();
        List<ConditionGroupRelVo> conditionGroupRelList = this.getConditionGroupRelList();
        if (CollectionUtils.isNotEmpty(conditionGroupRelList)) {
            StringBuilder script = new StringBuilder();
            script.append("(");
            String toUuid = null;
            for (ConditionGroupRelVo conditionGroupRelVo : conditionGroupRelList) {
                script.append(getBuildNaturalLanguageExpressions(conditionGroupMap.get(conditionGroupRelVo.getFrom())));
                script.append("and".equals(conditionGroupRelVo.getJoinType()) ? "并且" : " 或者 ");
                toUuid = conditionGroupRelVo.getTo();
            }
            script.append(getBuildNaturalLanguageExpressions(conditionGroupMap.get(toUuid)));
            script.append(")");
            return script.toString();
        } else if (CollectionUtils.isNotEmpty(conditionGroupList)) {
            ConditionGroupVo conditionGroupVo = conditionGroupList.get(0);
            return conditionGroupVo.buildScript();
        }
        return StringUtils.EMPTY;
    }

    private String getBuildNaturalLanguageExpressions(ConditionGroupVo conditionGroupVo) {
        List<ConditionVo> conditionList = conditionGroupVo.getConditionList();
        Map<String, ConditionVo> conditionMap = conditionGroupVo.getConditionMap();
        List<ConditionRelVo> conditionRelList = conditionGroupVo.getConditionRelList();
        if (CollectionUtils.isNotEmpty(conditionRelList)) {
            StringBuilder script = new StringBuilder();
            script.append("(");
            String toUuid = null;
            for (ConditionRelVo conditionRelVo : conditionRelList) {
                script.append(getBuildNaturalLanguageExpressions(conditionMap.get(conditionRelVo.getFrom())));
                script.append("and".equals(conditionRelVo.getJoinType()) ? " 并且 " : " 或者 ");
                toUuid = conditionRelVo.getTo();
            }
            script.append(getBuildNaturalLanguageExpressions(conditionMap.get(toUuid)));
            script.append(")");
            return script.toString();
        } else if (CollectionUtils.isNotEmpty(conditionList)) {
            ConditionVo conditionVo = conditionList.get(0);
            return getBuildNaturalLanguageExpressions(conditionVo);
        }
        return StringUtils.EMPTY;
    }

    protected String getBuildNaturalLanguageExpressions(ConditionVo conditionVo) {
        return StringUtils.EMPTY;
    }
}
