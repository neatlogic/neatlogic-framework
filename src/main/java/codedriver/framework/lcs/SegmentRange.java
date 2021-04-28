/*
 * Copyright(c) 2021. TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.lcs;
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
