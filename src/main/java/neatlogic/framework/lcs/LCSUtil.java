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

import neatlogic.framework.lcs.exception.LineHandlerNotFoundException;
import neatlogic.framework.lcs.linehandler.core.ILineHandler;
import neatlogic.framework.lcs.linehandler.core.LineHandlerFactory;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Stack;

/**
 * @Time:2020年10月22日
 * @ClassName: LCSUtil
 * @Description: 求最长公共子序列（longest common sequence）算法工具类
 */
public class LCSUtil {

    public final static String SPAN_CLASS_INSERT = "<span class='insert'>";
    public final static String SPAN_CLASS_DELETE = "<span class='delete'>";
    public final static String SPAN_END = "</span>";

    /**
     * @param <T>
     * @param source 旧数据列表
     * @param target 新数据列表
     * @return Node 返回最后一次比较结果信息
     * @Time:2020年10月22日
     * @Description: LCS算法比较
     */
    public static <T> List<SegmentPair> LCSCompare(List<T> source, List<T> target) {
        List<SegmentPair> resultList = new ArrayList<>();
        /** 先判断，至少有一个数据列表为空的情况 **/
        if (CollectionUtils.isEmpty(source) && CollectionUtils.isEmpty(target)) {
            return resultList;
        }
        if (CollectionUtils.isEmpty(source)) {
            resultList.add(new SegmentPair(0, 0, 0, target.size(), false));
            return resultList;
        }
        if (CollectionUtils.isEmpty(target)) {
            resultList.add(new SegmentPair(0, source.size(), 0, 0, false));
            return resultList;
        }
        /** 两个数据列表是否相同 **/
        if (Objects.equals(source, target)) {
            resultList.add(new SegmentPair(0, source.size(), 0, target.size(), true));
            return resultList;
        }
        /** 再判断，两个数据列表是否有公共前缀和后缀 **/
        int prefixLength = getPrefixLength(source, target);
        if (prefixLength == source.size()) {
            /** source是target的子数据列表 **/
            resultList.add(new SegmentPair(0, prefixLength, 0, prefixLength, true));
            resultList.add(new SegmentPair(prefixLength, prefixLength, prefixLength, target.size(), false));
            return resultList;
        } else if (prefixLength == target.size()) {
            /** target是source的子数据列表 **/
            resultList.add(new SegmentPair(0, prefixLength, 0, prefixLength, true));
            resultList.add(new SegmentPair(prefixLength, source.size(), prefixLength, prefixLength, false));
            return resultList;
        }
        int suffixLength = getSuffixLength(source, target);
        int prefixSuffixLength = prefixLength + suffixLength;
        if (prefixSuffixLength > source.size()) {
            if (prefixLength < suffixLength) {
                prefixLength = source.size() - suffixLength;
            } else {
                suffixLength = source.size() - prefixLength;
                ;
            }
        } else if (prefixSuffixLength > target.size()) {
            if (prefixLength < suffixLength) {
                prefixLength = target.size() - suffixLength;
            } else {
                suffixLength = target.size() - prefixLength;
                ;
            }
        }
        int sourceCount = source.size() - prefixLength - suffixLength;
        int targetCount = target.size() - prefixLength - suffixLength;
        if (sourceCount == 0) {
            if (prefixLength > 0) {
                resultList.add(new SegmentPair(0, prefixLength, 0, prefixLength, true));
            }
            resultList.add(new SegmentPair(prefixLength, source.size() - suffixLength, prefixLength, target.size() - suffixLength, false));
            resultList.add(new SegmentPair(source.size() - suffixLength, source.size(), target.size() - suffixLength, target.size(), true));
            return resultList;
        }
        if (targetCount == 0) {
            if (prefixLength > 0) {
                resultList.add(new SegmentPair(0, prefixLength, 0, prefixLength, true));
            }
            resultList.add(new SegmentPair(prefixLength, source.size() - suffixLength, prefixLength, target.size() - suffixLength, false));
            resultList.add(new SegmentPair(source.size() - suffixLength, source.size(), target.size() - suffixLength, target.size(), true));
            return resultList;
        }
        /** 再判断，两个数据列表去掉公共前缀和后缀后，是否是包含关系 **/
        if (sourceCount > targetCount) {
            int index = indexOf(source, prefixLength, sourceCount, target, prefixLength, targetCount);
            if (index != -1) {
                if (prefixLength > 0) {
                    resultList.add(new SegmentPair(0, prefixLength, 0, prefixLength, true));
                }
                resultList.add(new SegmentPair(prefixLength, prefixLength + index, prefixLength, prefixLength, false));
                resultList.add(new SegmentPair(prefixLength + index, prefixLength + index + targetCount, prefixLength, prefixLength + targetCount, true));
                resultList.add(new SegmentPair(prefixLength + index + targetCount, source.size() - suffixLength, prefixLength + targetCount, target.size() - suffixLength, false));
                if (suffixLength > 0) {
                    resultList.add(new SegmentPair(source.size() - suffixLength, source.size(), target.size() - suffixLength, target.size(), true));
                }
                return resultList;
            }
        } else if (sourceCount < targetCount) {
            int index = indexOf(target, prefixLength, targetCount, source, prefixLength, sourceCount);
            if (index != -1) {
                if (prefixLength > 0) {
                    resultList.add(new SegmentPair(0, prefixLength, 0, prefixLength, true));
                }
                resultList.add(new SegmentPair(prefixLength, prefixLength, prefixLength, prefixLength + index, false));
                resultList.add(new SegmentPair(prefixLength, prefixLength + sourceCount, prefixLength + index, prefixLength + index + sourceCount, true));
                resultList.add(new SegmentPair(prefixLength + sourceCount, source.size() - suffixLength, prefixLength + index + sourceCount, target.size() - suffixLength, false));
                if (suffixLength > 0) {
                    resultList.add(new SegmentPair(source.size() - suffixLength, source.size(), target.size() - suffixLength, target.size(), true));
                }
                return resultList;
            }
        }
        if (prefixLength > 0) {
            resultList.add(new SegmentPair(0, prefixLength, 0, prefixLength, true));
        }
        /** 没有包含关系的情况下，通过LCS算法对两个数据列表去掉公共前缀和后缀后进行匹配 **/
        Node node = LCSCompare(source, prefixLength, sourceCount, target, prefixLength, targetCount);
        List<SegmentPair> segmentPairList = getSegmentPairList(node, sourceCount, targetCount);
        for (SegmentPair segmentpair : segmentPairList) {
            if (prefixLength > 0) {
                segmentpair.moveRight(prefixLength);
            }
            resultList.add(segmentpair);
        }
        if (suffixLength > 0) {
            resultList.add(new SegmentPair(source.size() - suffixLength, source.size(), target.size() - suffixLength, target.size(), true));
        }
        return resultList;
    }

    /**
     * @param <T>
     * @param source 旧数据列表
     * @param target 新数据列表
     * @return Node 返回最后一次比较结果信息
     * @Time:2020年10月22日
     * @Description: LCS算法比较
     */
    private static <T> Node LCSCompare(List<T> source, int sourceOffset, int sourceCount, List<T> target, int targetOffset, int targetCount) {
        NodePool nodePool = new NodePool(sourceCount, targetCount);
        for (int i = sourceCount - 1; i >= 0; i--) {
            for (int j = targetCount - 1; j >= 0; j--) {
                Node currentNode = new Node(i, j);
                if (Objects.equals(source.get(sourceOffset + i), target.get(targetOffset + j))) {
                    int totalMatchLength = 1;
                    currentNode.setMatch(true);
                    Node upperLeftNode = nodePool.getOldNode(i + 1, j + 1);
                    if (upperLeftNode != null) {
                        totalMatchLength = upperLeftNode.getTotalMatchLength() + 1;
                        currentNode.setNext(upperLeftNode);
                    }
                    currentNode.setTotalMatchLength(totalMatchLength);
                    Node leftNode = nodePool.getOldNode(i, j + 1);
                    if (leftNode != null) {
                        if (totalMatchLength == leftNode.getTotalMatchLength()) {
                            currentNode.setAnotherNext(leftNode);
                        }
                    } else {
                        Node topNode = nodePool.getOldNode(i + 1, j);
                        if (topNode != null) {
                            if (totalMatchLength == topNode.getTotalMatchLength()) {
                                currentNode.setAnotherNext(topNode);
                            }
                        }
                    }
                } else {
                    int left = 0;
                    int top = 0;
                    Node leftNode = nodePool.getOldNode(i, j + 1);
                    if (leftNode != null) {
                        left = leftNode.getTotalMatchLength();
                    }
                    Node topNode = nodePool.getOldNode(i + 1, j);
                    if (topNode != null) {
                        top = topNode.getTotalMatchLength();
                    }
                    if (top > left) {
                        currentNode.setTotalMatchLength(top).setNext(topNode);
                    } else if (left > top) {
                        currentNode.setTotalMatchLength(left).setNext(leftNode);
                    } else {
                        currentNode.setTotalMatchLength(left).setNext(leftNode);
                    }
                }
                nodePool.addNode(currentNode);
            }
        }
        return nodePool.getOldNode(0, 0);
    }

    /**
     * @param source 旧字符串
     * @param target 新字符串
     * @return Node 返回最后一次比较结果信息
     * @Time:2020年11月02日
     * @Description: LCS算法比较字符串
     */
    public static List<SegmentPair> LCSCompare(String source, String target) {
//        PrintSingeColorFormatUtil.println("-----------------------------------");
        List<SegmentPair> resultList = new ArrayList<>();
        /** 先判断，至少有一个字符串为空的情况 **/
        if (StringUtils.isEmpty(source) && StringUtils.isEmpty(target)) {
            return resultList;
        }
        if (StringUtils.isEmpty(source)) {
            resultList.add(new SegmentPair(0, 0, 0, target.length(), false));
            return resultList;
        }
        if (StringUtils.isEmpty(target)) {
            resultList.add(new SegmentPair(0, source.length(), 0, 0, false));
            return resultList;
        }
        /** 两个字符串是否相同 **/
        if (Objects.equals(source, target)) {
            resultList.add(new SegmentPair(0, source.length(), 0, target.length(), true));
            return resultList;
        }
        /** 再判断，两个字符串是否有公共前缀和后缀 **/
        int prefixLength = getPrefixLength(source, target);
        if (prefixLength == source.length()) {
            /** source是target的子字符串 **/
            resultList.add(new SegmentPair(0, prefixLength, 0, prefixLength, true));
            resultList.add(new SegmentPair(prefixLength, prefixLength, prefixLength, target.length(), false));
            return resultList;
        } else if (prefixLength == target.length()) {
            /** target是source的子字符串 **/
            resultList.add(new SegmentPair(0, prefixLength, 0, prefixLength, true));
            resultList.add(new SegmentPair(prefixLength, source.length(), prefixLength, prefixLength, false));
            return resultList;
        }
        int suffixLength = getSuffixLength(source, target);
        int prefixSuffixLength = prefixLength + suffixLength;
        if (prefixSuffixLength > source.length()) {
            if (prefixLength < suffixLength) {
                prefixLength = source.length() - suffixLength;
            } else {
                suffixLength = source.length() - prefixLength;
                ;
            }
        } else if (prefixSuffixLength > target.length()) {
            if (prefixLength < suffixLength) {
                prefixLength = target.length() - suffixLength;
            } else {
                suffixLength = target.length() - prefixLength;
                ;
            }
        }
        int sourceCount = source.length() - prefixLength - suffixLength;
        int targetCount = target.length() - prefixLength - suffixLength;
        if (sourceCount == 0) {
            if (prefixLength > 0) {
                resultList.add(new SegmentPair(0, prefixLength, 0, prefixLength, true));
            }
            resultList.add(new SegmentPair(prefixLength, source.length() - suffixLength, prefixLength, target.length() - suffixLength, false));
            resultList.add(new SegmentPair(source.length() - suffixLength, source.length(), target.length() - suffixLength, target.length(), true));
            return resultList;
        }
        if (targetCount == 0) {
            if (prefixLength > 0) {
                resultList.add(new SegmentPair(0, prefixLength, 0, prefixLength, true));
            }
            resultList.add(new SegmentPair(prefixLength, source.length() - suffixLength, prefixLength, target.length() - suffixLength, false));
            resultList.add(new SegmentPair(source.length() - suffixLength, source.length(), target.length() - suffixLength, target.length(), true));
            return resultList;
        }
        /** 再判断，两个字符串去掉公共前缀和后缀后，是否是包含关系 **/
        if (sourceCount > targetCount) {
            int index = indexOf(source, prefixLength, sourceCount, target, prefixLength, targetCount);
            if (index != -1) {
                if (prefixLength > 0) {
                    resultList.add(new SegmentPair(0, prefixLength, 0, prefixLength, true));
                }
                resultList.add(new SegmentPair(prefixLength, prefixLength + index, prefixLength, prefixLength, false));
                resultList.add(new SegmentPair(prefixLength + index, prefixLength + index + targetCount, prefixLength, prefixLength + targetCount, true));
                resultList.add(new SegmentPair(prefixLength + index + targetCount, source.length() - suffixLength, prefixLength + targetCount, target.length() - suffixLength, false));
                if (suffixLength > 0) {
                    resultList.add(new SegmentPair(source.length() - suffixLength, source.length(), target.length() - suffixLength, target.length(), true));
                }
                return resultList;
            }
        } else if (sourceCount < targetCount) {
            int index = indexOf(target, prefixLength, targetCount, source, prefixLength, sourceCount);
            if (index != -1) {
                if (prefixLength > 0) {
                    resultList.add(new SegmentPair(0, prefixLength, 0, prefixLength, true));
                }
                resultList.add(new SegmentPair(prefixLength, prefixLength, prefixLength, prefixLength + index, false));
                resultList.add(new SegmentPair(prefixLength, prefixLength + sourceCount, prefixLength + index, prefixLength + index + sourceCount, true));
                resultList.add(new SegmentPair(prefixLength + sourceCount, source.length() - suffixLength, prefixLength + index + sourceCount, target.length() - suffixLength, false));
                if (suffixLength > 0) {
                    resultList.add(new SegmentPair(source.length() - suffixLength, source.length(), target.length() - suffixLength, target.length(), true));
                }
                return resultList;
            }
        }
        if (prefixLength > 0) {
            resultList.add(new SegmentPair(0, prefixLength, 0, prefixLength, true));
        }
        /** 没有包含关系的情况下，通过LCS算法对两个字符串去掉公共前缀和后缀后进行匹配 **/
        Node node = LCSCompare(source, prefixLength, sourceCount, target, prefixLength, targetCount);
        List<SegmentPair> segmentPairList = getSegmentPairList(node, sourceCount, targetCount);
        for (SegmentPair segmentpair : segmentPairList) {
            if (prefixLength > 0) {
                segmentpair.moveRight(prefixLength);
            }
            resultList.add(segmentpair);
        }
        if (suffixLength > 0) {
            resultList.add(new SegmentPair(source.length() - suffixLength, source.length(), target.length() - suffixLength, target.length(), true));
        }
        return resultList;
    }

    private static Node LCSCompare(String source, int sourceOffset, int sourceCount, String target, int targetOffset, int targetCount) {
//        System.out.println(source.substring(sourceOffset, sourceOffset + sourceCount));
//        System.out.println(target.substring(targetOffset, targetOffset + targetCount));
//        Node[][] nodes = new Node[sourceCount][targetCount];
//        System.out.println("--------------------------------------------------------------------------");
        NodePool nodePool = new NodePool(sourceCount, targetCount);
        for (int i = sourceCount - 1; i >= 0; i--) {
            for (int j = targetCount - 1; j >= 0; j--) {
                Node currentNode = new Node(i, j);
                if (source.charAt(sourceOffset + i) == target.charAt(targetOffset + j)) {
                    int totalMatchLength = 1;
                    currentNode.setMatch(true);
                    Node upperLeftNode = nodePool.getOldNode(i + 1, j + 1);
                    if (upperLeftNode != null) {
                        totalMatchLength = upperLeftNode.getTotalMatchLength() + 1;
                        currentNode.setNext(upperLeftNode);
                    }
                    currentNode.setTotalMatchLength(totalMatchLength);
                    Node leftNode = nodePool.getOldNode(i, j + 1);
                    if (leftNode != null) {
                        if (totalMatchLength == leftNode.getTotalMatchLength()) {
                            currentNode.setAnotherNext(leftNode);
                        }
                    } else {
                        Node topNode = nodePool.getOldNode(i + 1, j);
                        if (topNode != null) {
                            if (totalMatchLength == topNode.getTotalMatchLength()) {
                                currentNode.setAnotherNext(topNode);
                            }
                        }
                    }
                } else {
                    int left = 0;
                    int top = 0;
                    Node leftNode = nodePool.getOldNode(i, j + 1);
                    if (leftNode != null) {
                        left = leftNode.getTotalMatchLength();
                    }
                    Node topNode = nodePool.getOldNode(i + 1, j);
                    if (topNode != null) {
                        top = topNode.getTotalMatchLength();
                    }
                    if (top > left) {
                        currentNode.setTotalMatchLength(top).setNext(topNode);
                    } else if (left > top) {
                        currentNode.setTotalMatchLength(left).setNext(leftNode);
                    } else {
                        currentNode.setTotalMatchLength(left).setNext(leftNode);
                    }
                }
//                nodes[currentNode.getOldIndex()][currentNode.getNewIndex()] = currentNode;\
                nodePool.addNode(currentNode);
            }
        }
//        for (int i = 0; i < sourceCount; i++) {
//            for (int j = 0; j < targetCount; j++) {
//                System.out.print(nodes[i][j]);
//                System.out.print("\t");
//            }
//            System.out.println();
//        }
//        System.out.println("===============================================================================");
        return nodePool.getOldNode(0, 0);
    }

    /**
     * @param str         字符串数据
     * @param segmentList 分段列表
     * @param startMark   开始标记
     * @param endMark     结束标记
     * @return String 返回一个已做标记的新字符串
     * @Time:2020年10月22日
     * @Description: 对字符串不匹配的地方做标记
     */
    public static String wrapChangePlace(String str, List<SegmentRange> segmentList, String startMark, String endMark) {
        int mismatchCount = 0;
        for (SegmentRange segmentRange : segmentList) {
            if (!segmentRange.isMatch() && segmentRange.getSize() > 0) {
                mismatchCount++;
            }
        }
        int capacity = str.length() + mismatchCount * (startMark.length() + endMark.length());
        StringBuilder stringBuilder = new StringBuilder(capacity);
        for (SegmentRange segmentRange : segmentList) {
            if (segmentRange.getSize() > 0) {
                if (!segmentRange.isMatch()) {
                    stringBuilder.append(startMark);
                }
                for (int i = segmentRange.getBeginIndex(); i < segmentRange.getEndIndex(); i++) {
                    stringBuilder.append(str.charAt(i));
                    if (segmentRange.isMatch()) {
                        PrintSingeColorFormatUtil.print(str.charAt(i));
                    } else {
                        PrintSingeColorFormatUtil.print(str.charAt(i), startMark);
                    }
                }
                if (!segmentRange.isMatch()) {
                    stringBuilder.append(endMark);
                }
            }
        }
//        PrintSingeColorFormatUtil.println();
        String result = stringBuilder.toString();
        if (result.length() != capacity) {
            System.out.println("wrapChangePlace:result.length()" + result.length() + " != " + capacity + "capacity");
        }
        return result;
    }

    public static List<SegmentPair> getSegmentPairList(Node firstNode, int sourceCount, int targetCount) {
        /** 在保证公共子序列匹配度最高的情况下，找出所有匹配方案 **/
        List<Node> matchNodeList = new ArrayList<>();
        Stack<Node> nodeStack = new Stack<>();
        Stack<Node> previousNodeStack = new Stack<>();
        Stack<List<Node>> matchNodeListStack = new Stack<>();
        Stack<Integer> segmentCountStack = new Stack<>();
        Node node = firstNode;
        Node previousNode = null;
        int minSegmentCount = Integer.MAX_VALUE;
        List<Node> minMatchNodeList = new ArrayList<>();
        int segmentCount = 0;
        while (true) {
            do {
                if (node.isMatch()) {
                    if (matchNodeList.isEmpty() || matchNodeList.get(matchNodeList.size() - 1).getTotalMatchLength() > node.getTotalMatchLength()) {
                        matchNodeList.add(node);
                    }
                }
                if (previousNode == null) {
                    segmentCount++;
                } else if (node.isMatch()) {
                    if (previousNode.isMatch()) {
                        if (matchNodeList.contains(previousNode) ^ matchNodeList.contains(node)) {
                            segmentCount++;
                        }
                    } else {
                        if (matchNodeList.contains(node)) {
                            segmentCount++;
                        }
                    }
                } else {
                    if (previousNode.isMatch()) {
                        if (matchNodeList.contains(previousNode)) {
                            segmentCount++;
                        }
                    }
                }
                if (segmentCount >= minSegmentCount) {
                    break;
                }
                if (node.getAnotherNext() != null) {
                    nodeStack.push(node.getAnotherNext());
                    previousNodeStack.push(node);
                    matchNodeListStack.push(new ArrayList<>(matchNodeList));
                    segmentCountStack.push(segmentCount);
                }
                previousNode = node;
                node = node.getNext();
            } while (node != null);
            if (segmentCount < minSegmentCount) {
                minSegmentCount = segmentCount;
                minMatchNodeList = matchNodeList;
            }
            if (nodeStack.empty()) {
                break;
            }
            node = nodeStack.pop();
            previousNode = previousNodeStack.pop();
            matchNodeList = matchNodeListStack.pop();
            segmentCount = segmentCountStack.pop();
        }
        /** 所有匹配方案中，找出分隔段数最小的方案作为最终匹配方案 **/
//        System.out.println("++++++++++++++++++++++++++++++++");
        int prevMatchOldIndex = 0;
        int prevMatchNewIndex = 0;
        SegmentPair segmentPair = null;
        int size = minMatchNodeList.size();
        List<SegmentPair> resultList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            node = minMatchNodeList.get(i);
            if (segmentPair == null) {
                if (prevMatchOldIndex != node.getOldIndex() || prevMatchNewIndex != node.getNewIndex()) {
                    resultList.add(new SegmentPair(prevMatchOldIndex, node.getOldIndex(), prevMatchNewIndex, node.getNewIndex(), false));
                }
                segmentPair = new SegmentPair(node.getOldIndex(), node.getOldIndex() + 1, node.getNewIndex(), node.getNewIndex() + 1, true);
            } else if (node.getOldIndex() == prevMatchOldIndex && node.getNewIndex() == prevMatchNewIndex) {
                segmentPair.setEndIndex(node.getOldIndex() + 1, node.getNewIndex() + 1);
            } else {
                resultList.add(segmentPair);
                resultList.add(new SegmentPair(prevMatchOldIndex, node.getOldIndex(), prevMatchNewIndex, node.getNewIndex(), false));
                segmentPair = new SegmentPair(node.getOldIndex(), node.getOldIndex() + 1, node.getNewIndex(), node.getNewIndex() + 1, true);
            }
            prevMatchOldIndex = node.getOldIndex() + 1;
            prevMatchNewIndex = node.getNewIndex() + 1;
        }
        if (segmentPair != null) {
            resultList.add(segmentPair);
        }
        if (CollectionUtils.isEmpty(resultList) || prevMatchOldIndex != sourceCount || prevMatchNewIndex != targetCount) {
            resultList.add(new SegmentPair(prevMatchOldIndex, sourceCount, prevMatchNewIndex, targetCount, false));
        }
//        System.out.println("++++++++++++++++++++++++++++++++");
        return resultList;
    }

    /**
     * @Description: 查找字符串source与target的公共前缀长度
     * @Author: linbq
     * @Date: 2021/2/28 14:29
     * @Params:[source, target]
     * @Returns:int
     **/
    private static int getPrefixLength(String source, String target) {
        return getPrefixLength(source, 0, source.length(), target, 0, target.length());
    }

    /**
     * @Description: 查找字符串A与B的公共前缀长度，其中字符串A是source从sourceOffset下标开始sourceCount长度的子串，字符串B是target从targetOffset下标开始targetCount长度的子串
     * @Author: linbq
     * @Date: 2021/2/28 14:29
     * @Params:[source, target]
     * @Returns:int
     **/
    private static int getPrefixLength(String source, int sourceOffset, int sourceCount, String target, int targetOffset, int targetCount) {
        int lim = Math.min(sourceCount, targetCount);

        int k = 0;
        while (k < lim) {
            char c1 = source.charAt(sourceOffset + k);
            char c2 = target.charAt(targetOffset + k);
            if (c1 != c2) {
                return k;
            }
            k++;
        }
        return k;
    }

    /**
     * @Description: 查找字符串source与target的公共后缀长度
     * @Author: linbq
     * @Date: 2021/2/28 14:29
     * @Params:[source, target]
     * @Returns:int
     **/
    private static int getSuffixLength(String source, String target) {
        return getSuffixLength(source, 0, source.length(), target, 0, target.length());
    }

    /**
     * @Description: 查找字符串A与B的公共后缀长度，其中字符串A是source从sourceOffset下标开始sourceCount长度的子串，字符串B是target从targetOffset下标开始targetCount长度的子串
     * @Author: linbq
     * @Date: 2021/2/28 14:29
     * @Params:[source, target]
     * @Returns:int
     **/
    private static int getSuffixLength(String source, int sourceOffset, int sourceCount, String target, int targetOffset, int targetCount) {
        int lim = Math.min(sourceCount, targetCount);

        int k = 0;
        while (k < lim) {
            char c1 = source.charAt(sourceCount - k - 1);
            char c2 = target.charAt(targetCount - k - 1);
            if (c1 != c2) {
                return k;
            }
            k++;
        }
        return k;
    }

    /**
     * @Description:判断字符串B是不是字符串A的子串，其中字符串A是source从sourceOffset下标开始sourceCount长度的子串，字符串B是target从targetOffset下标开始targetCount长度的子串
     * @Author: linbq
     * @Date: 2021/2/28 14:20
     * @Params:[source, sourceOffset, sourceCount, target, targetOffset, targetCount]
     * @Returns:int 返回开始下标
     **/
    private static int indexOf(String source, int sourceOffset, int sourceCount, String target, int targetOffset, int targetCount) {
        char first = target.charAt(targetOffset);
        int max = sourceOffset + (sourceCount - targetCount);

        for (int i = sourceOffset; i <= max; i++) {
            /* 寻找第一个字符 */
            if (source.charAt(i) != first) {
                while (++i <= max && source.charAt(i) != first) ;
            }

            /* 找到第一个字符，现在看看target其余部分 */
            if (i <= max) {
                int j = i + 1;
                int end = j + targetCount - 1;
                for (int k = targetOffset + 1; j < end && source.charAt(j)
                        == target.charAt(k); j++, k++)
                    ;

                if (j == end) {
                    /* 找到target整个字符串 */
                    return i - sourceOffset;
                }
            }
        }
        return -1;
    }

    /**
     * @Description: 查找列表source与target的公共前缀长度
     * @Author: linbq
     * @Date: 2021/2/28 14:29
     * @Params:[source, target]
     * @Returns:int
     **/
    private static <T> int getPrefixLength(List<T> source, List<T> target) {
        return getPrefixLength(source, 0, source.size(), target, 0, target.size());
    }

    /**
     * @Description: 查找列表A与B的公共前缀长度，其中列表A是source从sourceOffset下标开始sourceCount长度的子列表，列表B是target从targetOffset下标开始targetCount长度的子列表
     * @Author: linbq
     * @Date: 2021/2/28 14:29
     * @Params:[source, target]
     * @Returns:int
     **/
    private static <T> int getPrefixLength(List<T> source, int sourceOffset, int sourceCount, List<T> target, int targetOffset, int targetCount) {
        int lim = Math.min(sourceCount, targetCount);

        int k = 0;
        while (k < lim) {
            T c1 = source.get(sourceOffset + k);
            T c2 = target.get(targetOffset + k);
            if (!Objects.equals(c1, c2)) {
                return k;
            }
            k++;
        }
        return k;
    }

    /**
     * @Description: 查找列表source与target的公共后缀长度
     * @Author: linbq
     * @Date: 2021/2/28 14:29
     * @Params:[source, target]
     * @Returns:int
     **/
    private static <T> int getSuffixLength(List<T> source, List<T> target) {
        return getSuffixLength(source, 0, source.size(), target, 0, target.size());
    }

    /**
     * @Description: 查找列表A与B的公共后缀长度，其中列表A是source从sourceOffset下标开始sourceCount长度的子列表，列表B是target从targetOffset下标开始targetCount长度的子列表
     * @Author: linbq
     * @Date: 2021/2/28 14:29
     * @Params:[source, target]
     * @Returns:int
     **/
    private static <T> int getSuffixLength(List<T> source, int sourceOffset, int sourceCount, List<T> target, int targetOffset, int targetCount) {
        int lim = Math.min(sourceCount, targetCount);

        int k = 0;
        while (k < lim) {
            T c1 = source.get(sourceCount - k - 1);
            T c2 = target.get(targetCount - k - 1);
            if (!Objects.equals(c1, c2)) {
                return k;
            }
            k++;
        }
        return k;
    }

    /**
     * @Description:判断列表B是不是列表A的子列表，其中列表A是source从sourceOffset下标开始sourceCount长度的子列表，列表B是target从targetOffset下标开始targetCount长度的子列表
     * @Author: linbq
     * @Date: 2021/2/28 14:20
     * @Params:[source, sourceOffset, sourceCount, target, targetOffset, targetCount]
     * @Returns:int 返回开始下标
     **/
    private static <T> int indexOf(List<T> source, int sourceOffset, int sourceCount, List<T> target, int targetOffset, int targetCount) {
        T first = target.get(targetOffset);
        int max = sourceOffset + (sourceCount - targetCount);

        for (int i = sourceOffset; i <= max; i++) {
            /* 寻找第一个元素 */
            if (!Objects.equals(source.get(i), first)) {
                while (++i <= max && !Objects.equals(source.get(i), first)) ;
            }

            /* 找到第一个元素，现在看看target其余部分 */
            if (i <= max) {
                int j = i + 1;
                int end = j + targetCount - 1;
                for (int k = targetOffset + 1; j < end && Objects.equals(source.get(j), target.get(k)); j++, k++) ;

                if (j == end) {
                    /* 找到target整个列表 */
                    return i - sourceOffset;
                }
            }
        }
        return -1;
    }

    public static List<SegmentPair> differenceBestMatch(List<String> source, List<String> target) {
        int sourceCount = source.size();
        int targetCount = target.size();
        Node[][] nodes = new Node[sourceCount][targetCount];
        NodePool nodePool = new NodePool(sourceCount, targetCount);
        for (int i = sourceCount - 1; i >= 0; i--) {
            for (int j = targetCount - 1; j >= 0; j--) {
                Node currentNode = new Node(i, j);
                String oldLine = source.get(i);
                String newLine = target.get(j);
                int minEditDistance = LCSUtil.minEditDistance(oldLine, newLine);
                currentNode.setMinEditDistance(minEditDistance);

                int left = 0;
                int top = 0;
                int upperLeft = 0;
                Node upperLeftNode = nodePool.getOldNode(i + 1, j + 1);
                if (upperLeftNode != null) {
                    upperLeft = upperLeftNode.getTotalMatchLength();
                }
                Node leftNode = nodePool.getOldNode(i, j + 1);
                if (leftNode != null) {
                    left = leftNode.getTotalMatchLength();
                }
                Node topNode = nodePool.getOldNode(i + 1, j);
                if (topNode != null) {
                    top = topNode.getTotalMatchLength();
                }
                if (i + 1 == sourceCount && j + 1 == targetCount) {
                    currentNode.setTotalMatchLength(minEditDistance);
                } else if (i + 1 == sourceCount) {
                    currentNode.setTotalMatchLength(minEditDistance + left);
                    currentNode.setNext(leftNode);
                } else if (j + 1 == targetCount) {
                    currentNode.setTotalMatchLength(minEditDistance + top);
                    currentNode.setNext(topNode);
                } else {
                    if (upperLeft <= left) {
                        if (upperLeft <= top) {
                            currentNode.setTotalMatchLength(minEditDistance + upperLeft);
                            currentNode.setNext(upperLeftNode);
                        } else {
                            currentNode.setTotalMatchLength(minEditDistance + top);
                            currentNode.setNext(topNode);
                        }
                    } else if (top <= left) {
                        currentNode.setTotalMatchLength(minEditDistance + top);
                        currentNode.setNext(topNode);
                    } else {
                        currentNode.setTotalMatchLength(minEditDistance + left);
                        currentNode.setNext(leftNode);
                    }
                }

                nodePool.addNode(currentNode);
                nodes[currentNode.getOldIndex()][currentNode.getNewIndex()] = currentNode;
            }
        }
        for (int i = 0; i < sourceCount; i++) {
            for (int j = 0; j < targetCount; j++) {
                System.out.print(nodes[i][j]);
                System.out.print("\t");
            }
            System.out.println();
        }
        System.out.println("------------------------------------------------------------------------------------------");
        List<Node> nodeList = new ArrayList<>();
        Node node = nodePool.getOldNode(0, 0);
        Node previous = null;
        while (node != null) {
            if (previous != null) {
                if (previous.getOldIndex() == node.getOldIndex() || previous.getNewIndex() == node.getNewIndex()) {
                    if (previous.getMinEditDistance() > node.getMinEditDistance()) {
                        previous = node;
                    }
                } else {
                    nodeList.add(previous);
                    previous = node;
                }
            } else {
                previous = node;
            }
            System.out.println(node);
            node = node.getNext();
        }
        nodeList.add(previous);
        System.out.println("------------------------------------------------------------------------------------------");
        nodeList.forEach(System.out::println);
        List<SegmentPair> segmentPairList = new ArrayList<>();
        int lastOldEndIndex = 0;
        int lastNewEndIndex = 0;
        for (Node n : nodeList) {
            if (n.getOldIndex() != lastOldEndIndex || n.getNewIndex() != lastNewEndIndex) {
                segmentPairList.add(new SegmentPair(lastOldEndIndex, n.getOldIndex(), lastNewEndIndex, n.getNewIndex(), false));
            }
            lastOldEndIndex = n.getOldIndex() + 1;
            lastNewEndIndex = n.getNewIndex() + 1;
            segmentPairList.add(new SegmentPair(n.getOldIndex(), lastOldEndIndex, n.getNewIndex(), lastNewEndIndex, false));
        }
        if (lastOldEndIndex != sourceCount || lastNewEndIndex != targetCount) {
            segmentPairList.add(new SegmentPair(lastOldEndIndex, sourceCount, lastNewEndIndex, targetCount, false));
        }
        segmentPairList.forEach(System.out::println);
        return segmentPairList;
    }

    /**
     * @Description: 求字符串source转变成target的最小编辑操作次数，许可的编辑操作包括将一个字符替换成另一个字符（算2次操作），插入一个字符算1次操作，删除一个字符算1次操作。
     * @Author: linbq
     * @Date: 2021/3/5 18:15
     * @Params:[source, target]
     * @Returns:int
     **/
    public static int minEditDistance(String source, String target) {
        /** 先判断，至少有一个字符串为空的情况 **/
        if (StringUtils.isEmpty(source) && StringUtils.isEmpty(target)) {
            return 0;
        }
        if (StringUtils.isEmpty(source)) {
            return target.length();
        }
        if (StringUtils.isEmpty(target)) {
            return source.length();
        }
        /** 两个字符串是否相同 **/
        if (Objects.equals(source, target)) {
            return 0;
        }
        /** 再判断，两个字符串是否有公共前缀和后缀 **/
        int prefixLength = getPrefixLength(source, target);
        if (prefixLength == source.length()) {
            /** source是target的子字符串 **/
            return target.length() - prefixLength;
        } else if (prefixLength == target.length()) {
            /** target是source的子字符串 **/
            return source.length() - prefixLength;
        }
        int suffixLength = getSuffixLength(source, target);
        int prefixSuffixLength = prefixLength + suffixLength;
        if (prefixSuffixLength > source.length()) {
            if (prefixLength < suffixLength) {
                prefixLength = source.length() - suffixLength;
            } else {
                suffixLength = source.length() - prefixLength;
                ;
            }
        } else if (prefixSuffixLength > target.length()) {
            if (prefixLength < suffixLength) {
                prefixLength = target.length() - suffixLength;
            } else {
                suffixLength = target.length() - prefixLength;
                ;
            }
        }
        int sourceCount = source.length() - prefixLength - suffixLength;
        int targetCount = target.length() - prefixLength - suffixLength;
        if (sourceCount == 0) {
            return targetCount;
        }
        if (targetCount == 0) {
            return sourceCount;
        }
        /** 再判断，两个字符串去掉公共前缀和后缀后，是否是包含关系 **/
        if (sourceCount > targetCount) {
            int index = indexOf(source, prefixLength, sourceCount, target, prefixLength, targetCount);
            if (index != -1) {
                return source.length() - target.length();
            }
        } else if (sourceCount < targetCount) {
            int index = indexOf(target, prefixLength, targetCount, source, prefixLength, sourceCount);
            if (index != -1) {
                return target.length() - source.length();
            }
        }
        /** 没有包含关系的情况下，通过LCS算法对两个字符串去掉公共前缀和后缀后进行匹配 **/
        return minEditDistance(source, prefixLength, sourceCount, target, prefixLength, targetCount);
    }

    /**
     * @Description: 求字符串source转变成target的最小编辑操作次数
     * @Author: linbq
     * @Date: 2021/3/5 18:18
     * @Params:[source, sourceOffset, sourceCount, target, targetOffset, targetCount]
     * @Returns:int
     **/
    private static int minEditDistance(String source, int sourceOffset, int sourceCount, String target, int targetOffset, int targetCount) {
        /* 存储偶数行的对比结果 */
        int[] evenRowsArray = new int[targetCount];
        /* 存储奇数行的对比结果 */
        int[] oddRowsArray = new int[targetCount];
        for (int i = 0; i < targetCount; i++) {
            oddRowsArray[i] = i + 1;
        }
        int cost = 0;
//        System.out.println("-------------------------------------------");
        for (int i = 0; i < sourceCount; i++) {
            for (int j = 0; j < targetCount; j++) {
                if (source.charAt(sourceOffset + i) == target.charAt(targetOffset + j)) {
                    cost = 0;
                } else {
                    cost = 2;
                }
                if (i % 2 == 0) {
                    int left = j == 0 ? i + 1 : evenRowsArray[j - 1];
                    int top = oddRowsArray[j];
                    int leftTop = j == 0 ? i : oddRowsArray[j - 1];
                    evenRowsArray[j] = Math.min(leftTop + cost, Math.min(left + 1, top + 1));
//                    System.out.print("[" + i + "][" + j + "]" + evenRowsArray[j]);
                } else {
                    int left = j == 0 ? i + 1 : oddRowsArray[j - 1];
                    int top = evenRowsArray[j];
                    int leftTop = j == 0 ? i : evenRowsArray[j - 1];
                    oddRowsArray[j] = Math.min(leftTop + cost, Math.min(left + 1, top + 1));
//                    System.out.print("[" + i + "][" + j + "]" + oddRowsArray[j]);
                }
//                System.out.print("\t");
            }
//            System.out.println();
        }
        if (sourceCount % 2 == 0) {
            return oddRowsArray[targetCount - 1];
        } else {
            return evenRowsArray[targetCount - 1];
        }
    }


    /**
     * @param oldDataList   旧数据列表
     * @param newDataList   新数据列表
     * @param oldResultList 重组后旧数据列表
     * @param newResultList 重组后新数据列表
     * @param segmentPair
     * @return void
     * @Time:2020年10月22日
     * @Description: 根据LCS算法比较结果，进行新旧数据的重组，体现两份数据的差异处
     */
    public static void regroupLineList(List<BaseLineVo> oldDataList, List<BaseLineVo> newDataList, List<BaseLineVo> oldResultList, List<BaseLineVo> newResultList, SegmentPair segmentPair) {
        List<BaseLineVo> oldSubList = oldDataList.subList(segmentPair.getOldBeginIndex(), segmentPair.getOldEndIndex());
        List<BaseLineVo> newSubList = newDataList.subList(segmentPair.getNewBeginIndex(), segmentPair.getNewEndIndex());
        if (segmentPair.isMatch()) {
            /** 分段对匹配时，行数据不能做标记，直接添加到重组后的数据列表中 **/
            oldResultList.addAll(oldSubList);
            newResultList.addAll(newSubList);
        } else {
            /** 分段对不匹配时，分成下列四种情况 **/
            if (CollectionUtils.isEmpty(newSubList)) {
                /** 删除行 **/
                for (BaseLineVo lineVo : oldSubList) {
                    lineVo.setChangeType("delete");
                    oldResultList.add(lineVo);
                    newResultList.add(createFillBlankLine(lineVo));
                }
            } else if (CollectionUtils.isEmpty(oldSubList)) {
                /** 插入行 **/
                for (BaseLineVo lineVo : newSubList) {
                    oldResultList.add(createFillBlankLine(lineVo));
                    lineVo.setChangeType("insert");
                    newResultList.add(lineVo);
                }
            } else if (oldSubList.size() == 1 && newSubList.size() == 1) {
                /** 修改一行 **/
                BaseLineVo oldLine = oldSubList.get(0);
                BaseLineVo newLine = newSubList.get(0);
                if (oldLine.getHandler().equals(newLine.getHandler())) {
                    /** 行组件相同，才是修改行数据 **/
                    oldLine.setChangeType("update");
                    newLine.setChangeType("update");
                    String handler = oldLine.getHandler();
                    ILineHandler lineHandler = LineHandlerFactory.getHandler(handler);
                    if (lineHandler == null) {
                        throw new LineHandlerNotFoundException(handler);
                    }
                    String oldMainBody = lineHandler.getMainBody(oldLine);
                    String newMainBody = lineHandler.getMainBody(newLine);
                    if (lineHandler.needCompare()) {
                        if (StringUtils.length(oldMainBody) == 0) {
                            lineHandler.setMainBody(newLine, "<span class='insert'>" + newMainBody + "</span>");
                        } else if (StringUtils.length(newMainBody) == 0) {
                            lineHandler.setMainBody(oldLine, "<span class='delete'>" + oldMainBody + "</span>");
                        } else {
                            List<SegmentRange> oldSegmentRangeList = new ArrayList<>();
                            List<SegmentRange> newSegmentRangeList = new ArrayList<>();
                            List<SegmentPair> segmentPairList = LCSUtil.LCSCompare(oldMainBody, newMainBody);
                            for (SegmentPair segmentpair : segmentPairList) {
                                oldSegmentRangeList.add(new SegmentRange(segmentpair.getOldBeginIndex(), segmentpair.getOldEndIndex(), segmentpair.isMatch()));
                                newSegmentRangeList.add(new SegmentRange(segmentpair.getNewBeginIndex(), segmentpair.getNewEndIndex(), segmentpair.isMatch()));
                            }
                            lineHandler.setMainBody(oldLine, LCSUtil.wrapChangePlace(oldMainBody, oldSegmentRangeList, "<span class='delete'>", "</span>"));
                            lineHandler.setMainBody(newLine, LCSUtil.wrapChangePlace(newMainBody, newSegmentRangeList, "<span class='insert'>", "</span>"));
                        }
                    }
                    oldResultList.add(oldLine);
                    newResultList.add(newLine);
                } else {
                    /** 行组件不相同，说明删除一行，再添加一行，根据行号大小判断加入重组后数据列表顺序 **/
                    if (oldLine.getLineNumber() <= newLine.getLineNumber()) {
                        oldLine.setChangeType("delete");
                        oldResultList.add(oldLine);
                        newResultList.add(createFillBlankLine(oldLine));

                        oldResultList.add(createFillBlankLine(newLine));
                        newLine.setChangeType("insert");
                        newResultList.add(newLine);
                    } else {
                        oldResultList.add(createFillBlankLine(newLine));
                        newLine.setChangeType("insert");
                        newResultList.add(newLine);

                        oldLine.setChangeType("delete");
                        oldResultList.add(oldLine);
                        newResultList.add(createFillBlankLine(oldLine));
                    }
                }

            } else {
                /** 修改多行，多行间需要做最优匹配 **/
                List<SegmentPair> segmentPairList = differenceBestMatchForBaseLineVo(oldSubList, newSubList);
                for (SegmentPair segmentpair : segmentPairList) {
                    /** 递归 **/
                    regroupLineList(oldSubList, newSubList, oldResultList, newResultList, segmentpair);
                }
            }
        }
    }

    private static BaseLineVo createFillBlankLine(BaseLineVo line) {
        BaseLineVo fillBlankLine = new BaseLineVo();
        fillBlankLine.setChangeType("fillblank");
        fillBlankLine.setHandler(line.getHandler());
        fillBlankLine.setConfig(line.getConfigStr());
        fillBlankLine.setContent(line.getContent());
        return fillBlankLine;
    }

    /**
     * @Description: 通过最短编辑距离算法，对不匹配段之间进行最佳匹配
     * @Author: linbq
     * @Date: 2021/3/5 17:32
     * @Params:[source, target]
     * @Returns:java.util.List<neatlogic.module.knowledge.lcs.SegmentPair>
     **/
    private static List<SegmentPair> differenceBestMatchForBaseLineVo(List<BaseLineVo> source, List<BaseLineVo> target) {
        int sourceCount = source.size();
        int targetCount = target.size();
        NodePool nodePool = new NodePool(sourceCount, targetCount);
        for (int i = sourceCount - 1; i >= 0; i--) {
            for (int j = targetCount - 1; j >= 0; j--) {
                Node currentNode = new Node(i, j);
                BaseLineVo oldLine = source.get(i);
                BaseLineVo newLine = target.get(j);
                String oldHandler = oldLine.getHandler();
                ILineHandler oldLineHandler = LineHandlerFactory.getHandler(oldHandler);
                if (oldLineHandler == null) {
                    throw new LineHandlerNotFoundException(oldHandler);
                }
                String newHandler = newLine.getHandler();
                ILineHandler newLineHandler = LineHandlerFactory.getHandler(newHandler);
                if (newLineHandler == null) {
                    throw new LineHandlerNotFoundException(newHandler);
                }
                String oldMainBody = oldLineHandler.getMainBody(oldLine);
                String newMainBody = newLineHandler.getMainBody(newLine);
                int oldLineContentLength = StringUtils.length(oldMainBody);
                int newLineContentLength = StringUtils.length(newMainBody);
                int minEditDistance = 0;
                if (oldLine.getHandler().equals(newLine.getHandler())) {
                    if (oldLineHandler.needCompare() && oldLineContentLength > 0 && newLineContentLength > 0) {
                        minEditDistance = LCSUtil.minEditDistance(oldMainBody, newMainBody);
                    } else {
                        minEditDistance = oldLineContentLength + newLineContentLength;
                    }
                } else {
                    minEditDistance = oldLineContentLength + newLineContentLength;
                }
                currentNode.setMinEditDistance(minEditDistance);
                int left = 0;
                int top = 0;
                int upperLeft = 0;
                Node upperLeftNode = nodePool.getOldNode(i + 1, j + 1);
                if (upperLeftNode != null) {
                    upperLeft = upperLeftNode.getTotalMatchLength();
                }
                Node leftNode = nodePool.getOldNode(i, j + 1);
                if (leftNode != null) {
                    left = leftNode.getTotalMatchLength();
                }
                Node topNode = nodePool.getOldNode(i + 1, j);
                if (topNode != null) {
                    top = topNode.getTotalMatchLength();
                }
                if (i + 1 == sourceCount && j + 1 == targetCount) {
                    currentNode.setTotalMatchLength(minEditDistance);
                } else if (i + 1 == sourceCount) {
                    currentNode.setTotalMatchLength(minEditDistance + left);
                    currentNode.setNext(leftNode);
                } else if (j + 1 == targetCount) {
                    currentNode.setTotalMatchLength(minEditDistance + top);
                    currentNode.setNext(topNode);
                } else {
                    if (upperLeft <= left) {
                        if (upperLeft <= top) {
                            currentNode.setTotalMatchLength(minEditDistance + upperLeft);
                            currentNode.setNext(upperLeftNode);
                        } else {
                            currentNode.setTotalMatchLength(minEditDistance + top);
                            currentNode.setNext(topNode);
                        }
                    } else if (top <= left) {
                        currentNode.setTotalMatchLength(minEditDistance + top);
                        currentNode.setNext(topNode);
                    } else {
                        currentNode.setTotalMatchLength(minEditDistance + left);
                        currentNode.setNext(leftNode);
                    }
                }

                nodePool.addNode(currentNode);
            }
        }
        List<Node> nodeList = new ArrayList<>();
        Node previous = null;
        Node node = nodePool.getOldNode(0, 0);
        while (node != null) {
            if (previous != null) {
                if (previous.getOldIndex() == node.getOldIndex() || previous.getNewIndex() == node.getNewIndex()) {
                    if (previous.getMinEditDistance() > node.getMinEditDistance()) {
                        previous = node;
                    }
                } else {
                    nodeList.add(previous);
                    previous = node;
                }
            } else {
                previous = node;
            }
            node = node.getNext();
        }
        if (previous != null) {
            nodeList.add(previous);
        }
        List<SegmentPair> segmentPairList = new ArrayList<>();
        int lastOldEndIndex = 0;
        int lastNewEndIndex = 0;
        for (Node n : nodeList) {
            if (n.getOldIndex() != lastOldEndIndex || n.getNewIndex() != lastNewEndIndex) {
                segmentPairList.add(new SegmentPair(lastOldEndIndex, n.getOldIndex(), lastNewEndIndex, n.getNewIndex(), false));
            }
            lastOldEndIndex = n.getOldIndex() + 1;
            lastNewEndIndex = n.getNewIndex() + 1;
            segmentPairList.add(new SegmentPair(n.getOldIndex(), lastOldEndIndex, n.getNewIndex(), lastNewEndIndex, false));
        }
        if (lastOldEndIndex != sourceCount || lastNewEndIndex != targetCount) {
            segmentPairList.add(new SegmentPair(lastOldEndIndex, sourceCount, lastNewEndIndex, targetCount, false));
        }
        return segmentPairList;
    }

    public static void main(String[] args) {
//        String source = "sasdfweghjklr";
//        String target = "sasdfghwejklr";
//        System.out.println(getPrefixLength(source, target));
//        System.out.println(getSuffixLength(source, target));

//        String source = "abcgdefghijklmnopq";
//        String target = "aghijkld";
//        System.out.println(indexOf(source, 0, source.length(), target, 1, target.length() - 2));
//
//        String source = "china";
//        String target = "chinow";
        String source = "abcdefjhijklmnopqrstuvwxyz";
        String target = "bbcdef4j4h4i4j4k4l4mnopqrstuvwxyz";
        int min = minEditDistance(source, 0, source.length(), target, 0, target.length());
        System.out.println("最小编辑距离是：" + min);
    }
}
