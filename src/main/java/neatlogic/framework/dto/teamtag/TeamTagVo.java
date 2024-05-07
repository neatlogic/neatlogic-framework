/*
 * Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package neatlogic.framework.dto.teamtag;

import com.alibaba.fastjson.annotation.JSONField;
import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.common.dto.BasePageVo;
import neatlogic.framework.restful.annotation.EntityField;
import neatlogic.framework.util.SnowflakeUtil;

import java.util.List;

public class TeamTagVo extends BasePageVo {

    @EntityField(name = "id", type = ApiParamType.LONG)
    private Long id;
    @EntityField(name = "矩阵uuid", type = ApiParamType.STRING)
    private String matrixUuid;
    @EntityField(name = "矩阵名", type = ApiParamType.STRING)
    private String matrixName;
    @EntityField(name = "矩阵列唯一标识", type = ApiParamType.STRING)
    private String matrixAttr;
    @EntityField(name = "标签名（矩阵列的值）", type = ApiParamType.STRING)
    private String matrixAttrValue;
    @EntityField(name = "是否包含子分组", type = ApiParamType.INTEGER)
    private Integer checkedChildren;
    @JSONField(serialize = false)
    List<Long> idList;

    public Long getId() {
        if (id == null) {
            id = SnowflakeUtil.uniqueLong();
        }
        return id;
    }

    public String getMatrixUuid() {
        return matrixUuid;
    }

    public void setMatrixUuid(String matrixUuid) {
        this.matrixUuid = matrixUuid;
    }

    public String getMatrixName() {
        return matrixName;
    }

    public void setMatrixName(String matrixName) {
        this.matrixName = matrixName;
    }

    public String getMatrixAttr() {
        return matrixAttr;
    }

    public void setMatrixAttr(String matrixAttr) {
        this.matrixAttr = matrixAttr;
    }

    public String getMatrixAttrValue() {
        return matrixAttrValue;
    }

    public void setMatrixAttrValue(String matrixAttrValue) {
        this.matrixAttrValue = matrixAttrValue;
    }

    public Integer getCheckedChildren() {
        return checkedChildren;
    }

    public void setCheckedChildren(Integer checkedChildren) {
        this.checkedChildren = checkedChildren;
    }

    public List<Long> getIdList() {
        return idList;
    }

    public void setIdList(List<Long> idList) {
        this.idList = idList;
    }
}
