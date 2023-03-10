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

package neatlogic.framework.worktime.dto;

import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.common.dto.BasePageVo;
import neatlogic.framework.restful.annotation.EntityField;
import com.alibaba.fastjson.annotation.JSONField;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class WorktimeVo extends BasePageVo implements Serializable {
    private static final long serialVersionUID = 3223914468345456530L;
    @EntityField(name = "工作时间窗口uuid", type = ApiParamType.STRING)
    private String uuid;
    @EntityField(name = "工作时间窗口名称", type = ApiParamType.STRING)
    private String name;
    @EntityField(name = "是否激活", type = ApiParamType.INTEGER)
    private Integer isActive;
    @EntityField(name = "最后一次修改用户", type = ApiParamType.STRING)
    private String lcu;
    @EntityField(name = "最后一次修改时间", type = ApiParamType.LONG)
    private Date lcd;
    /**
     * {
     * "monday":[{"startTime":"9:00","endTime":"12:00"},{"startTime":"14:00","endTime":"18:00"}],
     * "tuesday":[{"startTime":"9:00","endTime":"12:00"},{"startTime":"14:00","endTime":"18:00"}],
     * "wednesday":[{"startTime":"9:00","endTime":"12:00"},{"startTime":"14:00","endTime":"18:00"}],
     * "thursday":[{"startTime":"9:00","endTime":"12:00"},{"startTime":"14:00","endTime":"18:00"}],
     * "friday":[{"startTime":"9:00","endTime":"12:00"},{"startTime":"14:00","endTime":"18:00"}],
     * "saturday":[{"startTime":"9:00","endTime":"12:00"},{"startTime":"14:00","endTime":"18:00"}],
     * "sunday":[{"startTime":"9:00","endTime":"12:00"},{"startTime":"14:00","endTime":"18:00"}]
     * }
     */
    @EntityField(name = "每周工作时段的定义", type = ApiParamType.STRING)
    private String config;

    @EntityField(name = "年份列表", type = ApiParamType.JSONARRAY)
    private List<Integer> yearList;

    @EntityField(name = "工作时段列表", type = ApiParamType.JSONARRAY)
    private Set<String> workingHoursSet;

    @EntityField(name = "被引用的次数", type = ApiParamType.INTEGER)
    private int referenceCount;

    @EntityField(name = "是否已经被删除", type = ApiParamType.INTEGER)
    private Integer isDelete;

    public synchronized String getUuid() {
        if (StringUtils.isBlank(uuid)) {
            uuid = UUID.randomUUID().toString().replace("-", "");
        }
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

    public Integer getIsActive() {
        return isActive;
    }

    public void setIsActive(Integer isActive) {
        this.isActive = isActive;
    }

    public String getLcu() {
        return lcu;
    }

    public void setLcu(String lcu) {
        this.lcu = lcu;
    }

    public Date getLcd() {
        return lcd;
    }

    public void setLcd(Date lcd) {
        this.lcd = lcd;
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public List<Integer> getYearList() {
        return yearList;
    }

    public void setYearList(List<Integer> yearList) {
        this.yearList = yearList;
    }

    public Set<String> getWorkingHoursSet() {
        return workingHoursSet;
    }

    public void setWorkingHoursSet(Set<String> workingHoursSet) {
        this.workingHoursSet = workingHoursSet;
    }

    public int getReferenceCount() {
        return referenceCount;
    }

    public void setReferenceCount(int referenceCount) {
        this.referenceCount = referenceCount;
    }

    public Integer getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }
}
