package neatlogic.framework.notify.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * @Title: TreeVo
 * @Package neatlogic.framework.dto
 * @Description: 通知消息分类树
 * @Author: linbq
 * @Date: 2021/2/22 15:50
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
