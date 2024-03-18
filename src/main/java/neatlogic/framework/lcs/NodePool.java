/*Copyright (C) 2023  深圳极向量科技有限公司 All Rights Reserved.

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

package neatlogic.framework.lcs;

import java.util.ArrayList;
import java.util.List;

/**
 * 
* @Time:2020年11月2日
* @ClassName: NodePool 
* @Description: 节点池，用于代替原来的二维数组，使得空间复杂度由O(mn)变成O(2n)，时间复杂度还是O(mn)
 */
public class NodePool {
    /** 旧字符串长度 **/
    private final int oldLength;
    /** 新字符串长度 **/
    private final int newLength;
    /** 数组最大长度 **/
    private final int maxArrayLength = 1024;
    /** newLength是否小于数组最大长度maxArrayLength **/
    private final boolean lessThanMaxArrayLength;
    /* 存储偶数行的对比结果 */
    private Node[] evenRowsArray;
    private List<Node[]> evenRowsList;
    /* 存储奇数行的对比结果 */
    private Node[] oddRowsArray;
    private List<Node[]> oddRowsList;
    public NodePool(int oldLength, int newLength) {
        this.oldLength = oldLength;
        this.newLength = newLength;
        if(newLength <= this.maxArrayLength){
            this.lessThanMaxArrayLength = true;
            evenRowsArray = new Node[newLength];
            oddRowsArray = new Node[newLength];
        }else{
            this.lessThanMaxArrayLength = false;
            int remainder = newLength % this.maxArrayLength;
            int quotient = newLength / this.maxArrayLength;
            quotient = remainder == 0 ? quotient : quotient + 1;
            evenRowsList = new ArrayList<>(quotient);
            oddRowsList = new ArrayList<>(quotient);
            for (int i = 0; i < quotient - 1; i++) {
                evenRowsList.add(new Node[this.maxArrayLength]);
                oddRowsList.add(new Node[this.maxArrayLength]);
            }
            if(remainder == 0){
                evenRowsList.add(new Node[this.maxArrayLength]);
                oddRowsList.add(new Node[this.maxArrayLength]);
            }else{
                evenRowsList.add(new Node[remainder]);
                oddRowsList.add(new Node[remainder]);
            }
        }
    }
    private boolean rangeCheck(int oldIndex, int newIndex){
        if(oldIndex < 0 || oldIndex >= oldLength){
            return false;
        }
        if(newIndex < 0 || newIndex >= newLength){
            return false;
        }
        return true;
    }
    public Node getOldNode(int oldIndex, int newIndex) {
        if(rangeCheck(oldIndex, newIndex)) {
            if(oldIndex % 2 == 0){
                if(lessThanMaxArrayLength){
                    return evenRowsArray[newIndex];
                }else{
                    int remainder = newIndex % this.maxArrayLength;
                    int quotient = newIndex / this.maxArrayLength;
                    return evenRowsList.get(quotient)[remainder];
                }
            }else{
                if(lessThanMaxArrayLength){
                    return oddRowsArray[newIndex];
                }else{
                    int remainder = newIndex % this.maxArrayLength;
                    int quotient = newIndex / this.maxArrayLength;
                    return oddRowsList.get(quotient)[remainder];
                }
            }
        }
        return null;
    }
    public void addNode(Node node) {
        int oldIndex = node.getOldIndex();
        int newIndex = node.getNewIndex();
        if(rangeCheck(oldIndex, newIndex)) {
            if(oldIndex % 2 == 0){
                if(lessThanMaxArrayLength){
                    evenRowsArray[newIndex] = node;
                }else{
                    int remainder = newIndex % this.maxArrayLength;
                    int quotient = newIndex / this.maxArrayLength;
                    evenRowsList.get(quotient)[remainder] = node;
                }
            }else{
                if(lessThanMaxArrayLength){
                    oddRowsArray[newIndex] = node;
                }else{
                    int remainder = newIndex % this.maxArrayLength;
                    int quotient = newIndex / this.maxArrayLength;
                    oddRowsList.get(quotient)[remainder] = node;
                }
            }
        }
    }
}
