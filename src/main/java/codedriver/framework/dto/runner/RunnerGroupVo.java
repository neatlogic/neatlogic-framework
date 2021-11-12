/*
 * Copyright (c)  2021 TechSure Co.,Ltd.  All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.dto.runner;

import codedriver.framework.common.constvalue.ApiParamType;
import codedriver.framework.common.dto.BasePageVo;
import codedriver.framework.restful.annotation.EntityField;
import codedriver.framework.util.SnowflakeUtil;
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
    @EntityField(name = "组内runner个数", type = ApiParamType.INTEGER)
    private Integer runnerCount = 0;

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

    public Integer getRunnerCount() {
        if (runnerCount == 0 && CollectionUtils.isNotEmpty(runnerList)) {
            runnerCount = runnerList.size();
        }
        return runnerCount;
    }

    public void setRunnerCount(Integer runnerCount) {
        this.runnerCount = runnerCount;
    }
}
