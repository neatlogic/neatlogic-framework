/*
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
 */

package neatlogic.framework.lcs;
/**
 * 
* @Time:2020年10月22日
* @ClassName: Node 
* @Description: 节点类，两份不同数据（A、B）对比时，会先将数据分隔成不同的单元（字符串、字符等），A数据的每一个单元都会跟B数据的每一个单元比较一次，节点对象就是用来保存某一次比较的结果信息的。
 */
public class Node {
    /** 旧数据的单元下标 **/
    private int oldIndex;
    /** 新数据的单元下标 **/
    private int newIndex;
    /** 统计最大匹配长度 **/
    private int totalMatchLength;
    /** 最小编辑距离 **/
    private int minEditDistance;
    /** 记录这次比较是否匹配 **/
    private boolean match;
    /** 下一个节点 **/
    private Node next;

    private Node anotherNext;
    public Node(int oldIndex, int newIndex) {
        this.oldIndex = oldIndex;
        this.newIndex = newIndex;
    }
    public int getOldIndex() {
        return oldIndex;
    }
    public int getNewIndex() {
        return newIndex;
    }
    public Node setOldIndex(int oldIndex) {
        this.oldIndex = oldIndex;
        return this;
    }
    public Node setNewIndex(int newIndex) {
        this.newIndex = newIndex;
        return this;
    }
    public int getTotalMatchLength() {
        return totalMatchLength;
    }
    public Node setTotalMatchLength(int totalMatchLength) {
        this.totalMatchLength = totalMatchLength;
        return this;
    }

    public int getMinEditDistance() {
        return minEditDistance;
    }

    public void setMinEditDistance(int minEditDistance) {
        this.minEditDistance = minEditDistance;
    }

    public boolean isMatch() {
        return match;
    }
    public Node setMatch(boolean match) {
        this.match = match;
        return this;
    }

    public Node getNext() {
        return next;
    }

    public void setNext(Node next) {
        this.next = next;
    }

    public Node getAnotherNext() {
        return anotherNext;
    }

    public void setAnotherNext(Node anotherNext) {
        this.anotherNext = anotherNext;
    }
    
    public void reset() {
        this.oldIndex = 0;
        this.newIndex = 0;
        this.totalMatchLength = 0;
        this.match = false;
        this.next = null;
    }
    @Override
    public String toString() {
        return "[" + oldIndex + "][" + newIndex + "]=" + totalMatchLength + "," + (match ? "T" : "F" + "," + minEditDistance);
    }
}
