/*Copyright (C) $today.year  深圳极向量科技有限公司 All Rights Reserved.

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

package neatlogic.framework.dto.runner;

import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.common.dto.BasePageVo;
import neatlogic.framework.restful.annotation.EntityField;
import neatlogic.framework.util.SnowflakeUtil;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

/**
 * @author lvzk
 * @since 2021/4/12 14:54
 **/
public class RunnerGroupVo extends BasePageVo {
    @EntityField(name = "id", type = ApiParamType.LONG)
    private Long id;
    @EntityField(name = "runner 分组名", type = ApiParamType.STRING)
    private String name;
    @EntityField(name = "描述", type = ApiParamType.STRING)
    private String description;
    @EntityField(name = "授权类型", type = ApiParamType.STRING)
    private String authType;
    @EntityField(name = "runner名称", type = ApiParamType.STRING)
    private String runnerName;
    @EntityField(name = "网段列表", type = ApiParamType.JSONARRAY)
    private List<GroupNetworkVo> groupNetworkList;
    @EntityField(name = "runner列表", type = ApiParamType.JSONARRAY)
    private List<RunnerVo> runnerList;
    @EntityField(name = "runner id 列表", type = ApiParamType.JSONARRAY)
    private List<Long> runnerIdList;
    @EntityField(name = "组内runner个数", type = ApiParamType.INTEGER)
    private Integer runnerCount = 0;
    @EntityField(name = "标签", type = ApiParamType.STRING)
    private List<String> tagList;

    private List<GroupNetworkVo> networkList;

    private List<RunnerMapVo> runnerMapList;

    public synchronized Long getId() {
        if (id == null) {
            id = SnowflakeUtil.uniqueLong();
        }
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public List<GroupNetworkVo> getNetworkList() {
        return networkList;
    }

    public void setNetworkList(List<GroupNetworkVo> networkList) {
        this.networkList = networkList;
    }

    public List<RunnerMapVo> getRunnerMapList() {
        return runnerMapList;
    }

    public void setRunnerMapList(List<RunnerMapVo> runnerMapList) {
        this.runnerMapList = runnerMapList;
    }

    public String getAuthType() {
        return authType;
    }

    public void setAuthType(String authType) {
        this.authType = authType;
    }

    public String getRunnerName() {
        return runnerName;
    }

    public void setRunnerName(String runnerName) {
        this.runnerName = runnerName;
    }

    public List<GroupNetworkVo> getGroupNetworkList() {
        return groupNetworkList;
    }

    public void setGroupNetworkList(List<GroupNetworkVo> groupNetworkList) {
        this.groupNetworkList = groupNetworkList;
    }

    public List<RunnerVo> getRunnerList() {
        return runnerList;
    }

    public void setRunnerList(List<RunnerVo> runnerList) {
        this.runnerList = runnerList;
    }

    public List<Long> getRunnerIdList() {
        return runnerIdList;
    }

    public void setRunnerIdList(List<Long> runnerIdList) {
        this.runnerIdList = runnerIdList;
    }

    public Integer getRunnerCount() {
        if (runnerCount == 0 && CollectionUtils.isNotEmpty(runnerIdList)) {
            runnerCount = runnerIdList.size();
        }
        return runnerCount;
    }

    public void setRunnerCount(Integer runnerCount) {
        this.runnerCount = runnerCount;
    }

    public List<String> getTagList() {
        return tagList;
    }

    public void setTagList(List<String> tagList) {
        this.tagList = tagList;
    }
}
