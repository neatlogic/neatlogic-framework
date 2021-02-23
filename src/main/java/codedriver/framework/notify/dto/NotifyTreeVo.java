package codedriver.framework.notify.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * @Title: TreeVo
 * @Package codedriver.framework.dto
 * @Description: 通知消息分类树
 * @Author: linbq
 * @Date: 2021/2/22 15:50
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 **/
public class NotifyTreeVo {
    private String uuid;
    private String name;
    private List<NotifyTreeVo> children;

    private int count;
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

    public int getCount() {
        if(this.children != null){
            int sum = 0;
            for(NotifyTreeVo child : this.children){
                sum += child.getCount();
            }
            return sum;
        }
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
