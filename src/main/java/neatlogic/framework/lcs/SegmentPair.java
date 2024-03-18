/*Copyright (C) $today.year  深圳极向量科技有限公司 All Rights Reserved.

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
/**
 * 
* @Time:2020年10月22日
* @ClassName: SegmentMapping 
* @Description: 分段对类，用于保存新旧数据的对应的两段信息
 */
public class SegmentPair {
    /** 旧数据的段 **/
    private int oldBeginIndex;
    private int oldEndIndex;
    /** 新数据的段 **/
    private int newBeginIndex;
    private int newEndIndex;
    /** 这两段是否匹配 **/
    private boolean match;
    
    public SegmentPair(int oldBeginIndex, int newBeginIndex, boolean match) {
        this.oldBeginIndex = oldBeginIndex;
        this.newBeginIndex = newBeginIndex;
        this.match = match;
    }

    public SegmentPair(int oldBeginIndex, int oldEndIndex, int newBeginIndex, int newEndIndex, boolean match) {
        this.oldBeginIndex = oldBeginIndex;
        this.oldEndIndex = oldEndIndex;
        this.newBeginIndex = newBeginIndex;
        this.newEndIndex = newEndIndex;
        this.match = match;
    }

    public boolean isMatch() {
        return match;
    }
    public void setMatch(boolean match) {
        this.match = match;
    }

    public void setEndIndex(int oldEndIndex, int newEndIndex) {
        this.oldEndIndex = oldEndIndex;
        this.newEndIndex = newEndIndex;
    }

    public int getOldBeginIndex() {
        return oldBeginIndex;
    }

    public int getOldEndIndex() {
        return oldEndIndex;
    }

    public int getNewBeginIndex() {
        return newBeginIndex;
    }

    public int getNewEndIndex() {
        return newEndIndex;
    }

    @Override
    public String toString() {
        return "["  + oldBeginIndex +
                ", " + oldEndIndex +
                "], [" + newBeginIndex +
                ", " + newEndIndex +
                "], " + match;
    }

    public void moveRight(int offset) {
        this.oldBeginIndex += offset;
        this.oldEndIndex += offset;
        this.newBeginIndex += offset;
        this.newEndIndex += offset;
    }
}
