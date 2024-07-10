package neatlogic.framework.lrcode.dto;

/**
 * @Title: TreeNodeVo
 * @Package neatlogic.framework.tree.dto
 * @Description: TODO
 * @Author: linbq
 * @Date: 2021/3/17 6:30

 **/
public class TreeNodeVo {

    public final static String ROOT_UUID = "0";

    private String idKey;
    private String parentIdKey;
    private String nameKey;
    private Object idValue;
    private Object parentIdValue;
    private String tableName;
    private Integer lft;
    private Integer rht;
    private int childrenCount;
    public TreeNodeVo() {
    }
    public TreeNodeVo(String tableName, int lft, int rht) {
        this.tableName = tableName;
        this.lft = lft;
        this.rht = rht;
    }

    public TreeNodeVo(String tableName) {
        this.tableName = tableName;
    }

    public TreeNodeVo(String tableName, String idKey, String nameKey, String parentIdKey, Integer lft, Integer rht) {
        this.tableName = tableName;
        this.idKey = idKey;
        this.nameKey = nameKey;
        this.parentIdKey = parentIdKey;
        this.lft = lft;
        this.rht = rht;
    }

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

    public String getNameKey() {
        return nameKey;
    }

    public void setNameKey(String nameKey) {
        this.nameKey = nameKey;
    }
}
