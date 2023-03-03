package neatlogic.framework.lrcode.dto;

import neatlogic.framework.common.dto.BasePageVo;

import java.io.Serializable;

/**
 * @Title: TreeNodeVo
 * @Package neatlogic.framework.tree.dto
 * @Description: TODO
 * @Author: linbq
 * @Date: 2021/3/17 6:30
Copyright(c) $today.year NeatLogic Co., Ltd. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 **/
public class TreeNodeVo {

    public final static String ROOT_UUID = "0";

    private String idKey;
    private String parentIdKey;
    private Object idValue;
    private Object parentIdValue;
    private String tableName;
    private Integer lft;
    private Integer rht;
    private int childrenCount;

    public String getIdKey() {
        return idKey;
    }

    public void setIdKey(String idKey) {
        this.idKey = idKey;
    }

    public String getParentIdKey() {
        return parentIdKey;
    }

    public void setParentIdKey(String parentIdKey) {
        this.parentIdKey = parentIdKey;
    }

    public Object getIdValue() {
        return idValue;
    }

    public void setIdValue(Object idValue) {
        this.idValue = idValue;
    }

    public Object getParentIdValue() {
        if(parentIdValue == null && idValue != null){
            if(idValue instanceof Long){
                parentIdValue = 0;
            }else if(idValue instanceof Integer){
                parentIdValue = 0;
            }else if(idValue instanceof String){
                parentIdValue = "0";
            }
        }
        return parentIdValue;
    }

    public void setParentIdValue(Object parentIdValue) {
        this.parentIdValue = parentIdValue;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Integer getLft() {
        return lft;
    }

    public void setLft(Integer lft) {
        this.lft = lft;
    }

    public Integer getRht() {
        return rht;
    }

    public void setRht(Integer rht) {
        this.rht = rht;
    }

    public int getChildrenCount() {
        return childrenCount;
    }

    public void setChildrenCount(int childrenCount) {
        this.childrenCount = childrenCount;
    }
}
