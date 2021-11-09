/*
 * Copyright (c)  2021 TechSure Co.,Ltd.  All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.dto.runner;

import codedriver.framework.common.constvalue.ApiParamType;
import codedriver.framework.restful.annotation.EntityField;

/**
 * @author lvzk
 * @since 2021/4/12 14:54
 **/
public class GroupNetworkVo extends NetworkVo{
    @EntityField(name = "id", type = ApiParamType.LONG)
    private Long id;
    @EntityField(name = "runner id",type = ApiParamType.LONG)
    private Long runnerId;
    @EntityField(name = "runner 分组名", type = ApiParamType.STRING)
    private String name;
    @EntityField(name = "runner 组id", type = ApiParamType.LONG)
    private Long groupId;

    public Long getId() {
        return id;
    }

    public Long getRunnerId() {
        return runnerId;
    }

    public void setRunnerId(Long runnerId) {
        this.runnerId = runnerId;
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

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }
}
