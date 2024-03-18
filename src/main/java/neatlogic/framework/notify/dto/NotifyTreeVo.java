package neatlogic.framework.notify.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * @Title: TreeVo
 * @Package neatlogic.framework.dto
 * @Description: 通知消息分类树
 * @Author: linbq
 * @Date: 2021/2/22 15:50

 **/
public class NotifyTreeVo {
    private String uuid;
    private String name;
    private List<NotifyTreeVo> children;

    private int unreadCount;
    private int total;
    public NotifyTreeVo(){}

    public NotifyTreeVo(String uuid, String name){
        this.uuid = uuid;
        this.name = name;
    }


    public String getUuid() {
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

    public List<NotifyTreeVo> getChildren() {
        return children;
    }

    public void setChildren(List<NotifyTreeVo> children) {
        this.children = children;
    }

    public void addChildren(NotifyTreeVo treeVo){
        if(this.children == null){
            this.children = new ArrayList<>();
        }
        this.children.add(treeVo);
    }

    public int getUnreadCount() {
        if(this.children != null){
            int sum = 0;
            for(NotifyTreeVo child : this.children){
                sum += child.getUnreadCount();
            }
            return sum;
        }
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    public int getTotal() {
        if(this.children != null){
            int sum = 0;
            for(NotifyTreeVo child : this.children){
                sum += child.getTotal();
            }
            return sum;
        }
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
