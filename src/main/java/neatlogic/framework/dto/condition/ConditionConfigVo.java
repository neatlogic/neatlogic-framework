/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.framework.dto.condition;

import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.common.dto.BaseEditorVo;
import neatlogic.framework.restful.annotation.EntityField;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
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
            conditionGroupMap = conditionGroupList.stream().collect(Collectors.toMap(e -> e.getUuid(), e -> e));
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
}
