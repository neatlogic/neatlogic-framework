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
