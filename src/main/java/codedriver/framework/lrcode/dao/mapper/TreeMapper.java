package codedriver.framework.lrcode.dao.mapper;

import codedriver.framework.lrcode.dto.TreeNodeVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Title: TreeMapper
 * @Package codedriver.framework.tree
 * @Description: TODO
 * @Author: linbq
 * @Date: 2021/3/16 20:38
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 **/
public interface TreeMapper {
    /**
     *
     * @Time:2020年7月20日
     * @Description: 判断左右编码是否全部正确，符合下列条件的才正确
     * 1.左右编码不能为null
     * 2.左编码不能小于2，右编码不能小于3
     * 3.子节点的左编码大于父节点的左编码，子节点的右编码小于父节点的右编码
     * 4.没有子节点的节点左编码比右编码小1
     * @return int 返回左右编码不正确的个数
     */
    public Object checkLeftRightCodeIsWrong(@Param("tableName")String tableName, @Param("idKey") String idKey);
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

    public List<TreeNodeVo> getTreeNodeListByParentId(@Param("tableName")String tableName, @Param("parentIdKey") String parentIdKey, @Param("parentIdValue")Object parentIdValue);

    public int updateTreeNodeLeftRightCodeById(@Param("tableName")String tableName, @Param("idKey") String idKey, @Param("idValue") Object idValue, @Param("lft") int lft, @Param("rht") int rht);

    public TreeNodeVo getTreeNodeById(@Param("tableName")String tableName, @Param("idKey") String idKey, @Param("parentIdKey") String parentIdKey, @Param("idValue") Object idValue);

    public int batchUpdateTreeNodeLeftCode(@Param("tableName")String tableName, @Param("minCode") Integer minCode, @Param("step") int step);

    public int batchUpdateTreeNodeRightCode(@Param("tableName")String tableName, @Param("minCode") Integer minCode, @Param("step") int step);

    public int batchUpdateTreeNodeLeftRightCodeByLeftRightCode(@Param("tableName")String tableName, @Param("lft") Integer lft, @Param("rht") Integer rht, @Param("step") int step);

    public int checkTreeNodeIsExistsByLeftRightCode(@Param("tableName")String tableName, @Param("idKey") String idKey, @Param("idValue")Object idValue, @Param("lft") Integer lft, @Param("rht") Integer rht);

    public int updateTreeNodeParentIdById(@Param("tableName")String tableName, @Param("idKey") String idKey, @Param("parentIdKey") String parentIdKey, @Param("idValue")Object idValue, @Param("parentIdValue") Object parentIdValue);

    public int batchUpdateTreeNodeLeftRightCodeToNullByLeftRightCode(@Param("tableName")String tableName, @Param("lft") Integer lft, @Param("rht") Integer rht);

    public int getMaxRht(String tableName);
}
