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
/**
 * 
* @Time:2020年10月22日
* @ClassName: SegmentRange 
* @Description: 分段范围类，两份不同的数据对比后，分成相同数量的段，完全匹配的连续单元归为一段，不完全匹配的连续单元归为一段
 */
public class SegmentRange {
    /** 开始下标 **/
    private final int beginIndex;
    /** 结束下标（不包含该下标）**/
    private int endIndex;
    /** 这段是否匹配 **/
    private boolean match;

    public SegmentRange(int beginIndex, boolean match) {
        this.beginIndex = beginIndex;
        this.match = match;
    }
    public SegmentRange(int beginIndex, int endIndex, boolean match) {
        this.beginIndex = beginIndex;
        this.endIndex = endIndex;
        this.match = match;
    }
    public int getBeginIndex() {
        return beginIndex;
    }
    public int getEndIndex() {
        return endIndex;
    }
    public void setEndIndex(int endIndex) {
        this.endIndex = endIndex;
    }
    public int getSize() {
        return endIndex - beginIndex;
    }
    public boolean isMatch() {
        return match;
    }
    public void setMatch(boolean match) {
        this.match = match;
    }
    @Override
    public String toString() {
        return "[" + beginIndex + ", " + endIndex + "]";
    }
}
