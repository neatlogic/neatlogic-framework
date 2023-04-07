/*
 * Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package neatlogic.framework.lrcode.dao.mapper;

import neatlogic.framework.lrcode.dto.TreeNodeVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TreeMapper {
    /**
     * @return int 返回左右编码不正确的个数
     * @Time:2020年7月20日
     * @Description: 判断左右编码是否全部正确，符合下列条件的才正确
     * 1.左右编码不能为null
     * 2.左编码不能小于2，右编码不能小于3
     * 3.子节点的左编码大于父节点的左编码，子节点的右编码小于父节点的右编码
     * 4.没有子节点的节点左编码比右编码小1
     */
    public Object checkLeftRightCodeIsWrong(@Param("tableName") String tableName, @Param("idKey") String idKey);
    /**
     * @Description: 左右编码不能为null
     * @Author: linbq
     * @Date: 2021/3/17 19:10
     * @Params:[tableName, idKey, idValue]
     * @Returns:java.lang.Object
     **/
//    public Object checkLeftRightCodeIsNull(@Param("tableName")String tableName, @Param("idKey") String idKey, @Param("idValue") Object idValue);
    /**
     * @Description: 左编码不能小于2，右编码不能小于3
     * @Author: linbq
     * @Date: 2021/3/17 19:11
     * @Params:[tableName]
     * @Returns:java.lang.Object
     **/
//    public Object checkLftLt2OrRhtLt3IsExists(@Param("tableName")String tableName, @Param("idKey") String idKey);
    /**
     * @Description: 子节点的左编码大于父节点的左编码，子节点的右编码小于父节点的右编码
     * @Author: linbq
     * @Date: 2021/3/17 19:11
     * @Params:[tableName]
     * @Returns:java.lang.Object
     **/
//    public Object checkChildLftLeParentLftOrChildRhtGeParentRhtIsExists(@Param("tableName")String tableName, @Param("idKey") String idKey, @Param("parentIdKey") String parentIdKey);

    /**
     * @Description: 叶子节点的左右编码是否不连续
     * @Author: linbq
     * @Date: 2021/3/17 19:11
     * @Params:[tableName]
     * @Returns:java.lang.Object
     **/
//    public Object checkLeafNodeLeftRightCodeAreNotContinuous(@Param("tableName")String tableName, @Param("idKey") String idKey, @Param("parentIdKey") String parentIdKey);
    public List<TreeNodeVo> getTreeNodeListByParentId(@Param("tableName") String tableName, @Param("idKey") String idKey, @Param("parentIdKey") String parentIdKey, @Param("parentIdValue") Object parentIdValue, @Param("condition") String condition);

    public int updateTreeNodeLeftRightCodeById(@Param("tableName") String tableName, @Param("idKey") String idKey, @Param("idValue") Object idValue, @Param("lft") int lft, @Param("rht") int rht);

    public TreeNodeVo getTreeNodeById(@Param("tableName") String tableName, @Param("idKey") String idKey, @Param("parentIdKey") String parentIdKey, @Param("idValue") Object idValue);

    public int batchUpdateTreeNodeLeftCode(@Param("tableName") String tableName, @Param("minCode") Integer minCode, @Param("step") int step);

    public int batchUpdateTreeNodeRightCode(@Param("tableName") String tableName, @Param("minCode") Integer minCode, @Param("step") int step);

    public int batchUpdateTreeNodeLeftRightCodeByLeftRightCode(@Param("tableName") String tableName, @Param("lft") Integer lft, @Param("rht") Integer rht, @Param("step") int step);

    public int checkTreeNodeIsExistsByLeftRightCode(@Param("tableName") String tableName, @Param("idKey") String idKey, @Param("idValue") Object idValue, @Param("lft") Integer lft, @Param("rht") Integer rht);

    public int updateTreeNodeParentIdById(@Param("tableName") String tableName, @Param("idKey") String idKey, @Param("parentIdKey") String parentIdKey, @Param("idValue") Object idValue, @Param("parentIdValue") Object parentIdValue);

    public int batchUpdateTreeNodeLeftRightCodeToNullByLeftRightCode(@Param("tableName") String tableName, @Param("lft") Integer lft, @Param("rht") Integer rht);

    public int getRootRht(String tableName);
}
