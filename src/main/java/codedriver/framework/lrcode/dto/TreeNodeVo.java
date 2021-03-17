package codedriver.framework.lrcode.dto;

import codedriver.framework.common.dto.BasePageVo;

import java.io.Serializable;

/**
 * @Title: TreeNodeVo
 * @Package codedriver.framework.tree.dto
 * @Description: TODO
 * @Author: linbq
 * @Date: 2021/3/17 6:30
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 **/
public class TreeNodeVo {

    public final static String ROOT_UUID = "0";
    public final static String ROOT_PARENTUUID = "-1";

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
