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

package neatlogic.framework.worktime.dto;

import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.common.dto.BasePageVo;
import neatlogic.framework.restful.annotation.EntityField;
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
