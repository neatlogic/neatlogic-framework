/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.lrcode;

import codedriver.framework.lock.core.LockManager;
import codedriver.framework.lrcode.constvalue.MoveType;
import codedriver.framework.lrcode.dao.mapper.TreeMapper;
import codedriver.framework.lrcode.dto.TreeNodeVo;
import codedriver.framework.lrcode.exception.MoveTargetNodeIllegalException;
import codedriver.framework.lrcode.exception.TreeNodeNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class LRCodeManager {

    private static TreeMapper treeMapper;

    @Autowired
    public LRCodeManager(TreeMapper _treeMapper) {
        treeMapper = _treeMapper;
    }

    /**
     * @Description: 添加节点，调用方插入A新节点前，先调用该方法获取A节点左右编码并更新其他节点左右编码，再插入A节点
     * @Author: linbq
     * @Date: 2021/3/17 17:41
     * @Params: [tableName, idKey, idValue, lft, rht]
     * @Returns: int
     **/
    public static int beforeAddTreeNode(String tableName, String idKey, String parentIdKey, Object parentIdValue) {
        initializeLRCode(tableName, idKey, parentIdKey);
        int lft;
        if (parentIdValue == null || TreeNodeVo.ROOT_UUID.equals(parentIdValue.toString())) {
            lft = treeMapper.getRootRht(tableName);
        } else {
            TreeNodeVo parentTreeNodeVo = treeMapper.getTreeNodeById(tableName, idKey, parentIdKey, parentIdValue);
            if (parentTreeNodeVo == null) {
                throw new TreeNodeNotFoundException(tableName, parentIdValue);
            }
            lft = parentTreeNodeVo.getRht();
        }

        //更新插入位置右边的左右编码值
        treeMapper.batchUpdateTreeNodeLeftCode(tableName, lft, 2);
        treeMapper.batchUpdateTreeNodeRightCode(tableName, lft, 2);
        return lft;
    }

    /**
     * @Description: 移动节点
     * @Author: linbq
     * @Date: 2021/3/17 17:41
     * @Params: [tableName, idKey, parentIdKey, idValue, moveType, targetIdValue]
     * @Returns: void
     **/
    public static int[] moveTreeNode(String tableName, String idKey, String parentIdKey, Object idValue, MoveType moveType, Object targetIdValue) {
        initializeLRCode(tableName, idKey, parentIdKey);
        if (Objects.equals(idValue, targetIdValue)) {
            throw new MoveTargetNodeIllegalException();
        }
        TreeNodeVo moveCatalog = treeMapper.getTreeNodeById(tableName, idKey, parentIdKey, idValue);
        //判断被移动的服务目录是否存在
        if (moveCatalog == null) {
            throw new TreeNodeNotFoundException(tableName, idValue);
        }
        //判断移动后的父节点是否在当前节点的后代节点中
        if (treeMapper.checkTreeNodeIsExistsByLeftRightCode(tableName, idKey, targetIdValue, moveCatalog.getLft(), moveCatalog.getRht()) > 0) {
            throw new MoveTargetNodeIllegalException();
        }
        TreeNodeVo targetCatalog;
        if (targetIdValue == null) {
            targetCatalog = new TreeNodeVo();
            if (idValue instanceof Long) {
                targetIdValue = 0;
                targetCatalog.setIdValue(0);
                targetCatalog.setParentIdValue(-1);
            } else if (idValue instanceof Integer) {
                targetIdValue = 0;
                targetCatalog.setIdValue(0);
                targetCatalog.setParentIdValue(-1);
            } else if (idValue instanceof String) {
                targetIdValue = "0";
                targetCatalog.setIdValue("0");
                targetCatalog.setParentIdValue("-1");
            }
            targetCatalog.setLft(1);
            targetCatalog.setRht(treeMapper.getRootRht(tableName));
        } else {
            targetCatalog = treeMapper.getTreeNodeById(tableName, idKey, parentIdKey, targetIdValue);
            //判断目标节点服务目录是否存在
            if (targetCatalog == null) {
                throw new TreeNodeNotFoundException(tableName, targetIdValue);
            }
        }
        //移动到目标节点前面或后面
        Object parentIdValue;
        if (MoveType.INNER == moveType) {
            parentIdValue = targetIdValue;
            if (Objects.equals(moveCatalog.getParentIdValue(), targetIdValue)) {
                if (moveCatalog.getRht() + 1 == targetCatalog.getRht()) {
                    return null;
                }
            }
        } else {
            parentIdValue = targetCatalog.getParentIdValue();
            if (MoveType.PREV == moveType) {
                if (Objects.equals(moveCatalog.getParentIdValue(), parentIdValue)) {
                    if (moveCatalog.getRht() + 1 == targetCatalog.getLft()) {
                        return null;
                    }
                }
            } else {
                if (Objects.equals(moveCatalog.getParentIdValue(), parentIdValue)) {
                    if (targetCatalog.getRht() + 1 == moveCatalog.getLft()) {
                        return null;
                    }
                }
            }
        }
        //目录只能移动到目录前面，不能移动到通道前面
        //目录只能移动到目录后面，不能移动到通道后面
        //目录只能移进目录里面，不能移进通道里面
        //所以目标节点只能是目录

        //将被移动块中的所有节点的左右编码值设置为<=0
        treeMapper.batchUpdateTreeNodeLeftRightCodeByLeftRightCode(tableName, moveCatalog.getLft(), moveCatalog.getRht(), -moveCatalog.getRht());
        //计算被移动块右边的节点移动步长
        int step = moveCatalog.getRht() - moveCatalog.getLft() + 1;
        //更新旧位置右边的左右编码值
        treeMapper.batchUpdateTreeNodeLeftCode(tableName, moveCatalog.getLft(), -step);
        treeMapper.batchUpdateTreeNodeRightCode(tableName, moveCatalog.getLft(), -step);
        if (targetCatalog.getLft() >= moveCatalog.getLft()) {
            targetCatalog.setLft(targetCatalog.getLft() - step);
        }
        if (targetCatalog.getRht() >= moveCatalog.getLft()) {
            targetCatalog.setRht(targetCatalog.getRht() - step);
        }
        //找出被移动块移动后左编码值
        int lft;
        //目标节点uuid
        if (MoveType.INNER == moveType) {//移动到末尾
            lft = targetCatalog.getRht();
        } else {
            if (MoveType.PREV == moveType) {
                lft = targetCatalog.getLft();
            } else {
                lft = targetCatalog.getRht() + 1;
            }
        }
        treeMapper.updateTreeNodeParentIdById(tableName, idKey, parentIdKey, idValue, parentIdValue);

        //更新新位置右边的左右编码值
        treeMapper.batchUpdateTreeNodeLeftCode(tableName, lft, step);
        treeMapper.batchUpdateTreeNodeRightCode(tableName, lft, step);

        //更新被移动块中节点的左右编码值
        treeMapper.batchUpdateTreeNodeLeftRightCodeByLeftRightCode(tableName, moveCatalog.getLft() - moveCatalog.getRht(), 0, lft - moveCatalog.getLft() + moveCatalog.getRht());
        int[] lftRht = new int[2];
        lftRht[0] = lft;
        lftRht[1] = lft + step - 1;
        return lftRht;
    }

    /**
     * @Description: 删除节点，调用方删除A节点前，先调用该方法将A节点左右编码置空并更新其他节点左右编码，再删除A节点
     * @Author: linbq
     * @Date: 2021/3/17 17:42
     * @Params: [tableName, lft, rht]
     * @Returns: void
     **/
    public static void beforeDeleteTreeNode(String tableName, String idKey, String parentIdKey, Object idValue) {
        initializeLRCode(tableName, idKey, parentIdKey);
        TreeNodeVo treeNodeVo = treeMapper.getTreeNodeById(tableName, idKey, parentIdKey, idValue);
        if (treeNodeVo == null) {
            throw new TreeNodeNotFoundException(tableName, idValue);
        }
        treeMapper.batchUpdateTreeNodeLeftRightCodeToNullByLeftRightCode(tableName, treeNodeVo.getLft(), treeNodeVo.getRht());
        //计算被移动块右边的节点移动步长
        int step = treeNodeVo.getRht() - treeNodeVo.getLft() + 1;
        //更新删除位置右边的左右编码值
        treeMapper.batchUpdateTreeNodeLeftCode(tableName, treeNodeVo.getLft(), -step);
        treeMapper.batchUpdateTreeNodeRightCode(tableName, treeNodeVo.getLft(), -step);
    }

    /**
     * @Description: 重建左右编码
     * @Author: linbq
     * @Date: 2021/3/17 17:42
     * @Params: [tableName, idKey, parentIdKey]
     * @Returns:void
     **/
    public static void rebuildLeftRightCode(String tableName, String idKey, String parentIdKey) {
        rebuildLeftRightCode(tableName, idKey, parentIdKey, TreeNodeVo.ROOT_UUID, 1);
    }

    private static Integer rebuildLeftRightCode(String tableName, String idKey, String parentIdKey, Object parentIdValue, int parentLft) {
        List<TreeNodeVo> catalogList = treeMapper.getTreeNodeListByParentId(tableName, idKey, parentIdKey, parentIdValue);
        for (TreeNodeVo catalog : catalogList) {
            if (catalog.getChildrenCount() == 0) {
                treeMapper.updateTreeNodeLeftRightCodeById(tableName, idKey, catalog.getIdValue(), parentLft + 1, parentLft + 2);
                parentLft += 2;
            } else {
                int lft = parentLft + 1;
                parentLft = rebuildLeftRightCode(tableName, idKey, parentIdKey, catalog.getIdValue(), lft);
                treeMapper.updateTreeNodeLeftRightCodeById(tableName, idKey, catalog.getIdValue(), lft, parentLft + 1);
                parentLft += 1;
            }
        }
        return parentLft;
    }

    /**
     * @Description: 初始化左右编码
     * @Author: linbq
     * @Date: 2021/3/17 17:42
     * @Params: [tableName, idKey, parentIdKey]
     * @Returns:void
     **/
    private static void initializeLRCode(String tableName, String idKey, String parentIdKey) {
        LockManager.getLockById(tableName);
        if (treeMapper.checkLeftRightCodeIsWrong(tableName, idKey) != null) {
            rebuildLeftRightCode(tableName, idKey, parentIdKey);
        }
//        if(treeMapper.checkLeftRightCodeIsNull(tableName, idKey, idValue) != null){
//            rebuildLeftRightCode(tableName, idKey, parentIdKey);
//        }else if(treeMapper.checkLftLt2OrRhtLt3IsExists(tableName, idKey)  != null){
//            rebuildLeftRightCode(tableName, idKey, parentIdKey);
//        }else if(treeMapper.checkChildLftLeParentLftOrChildRhtGeParentRhtIsExists(tableName, idKey, parentIdKey)  != null){
//            rebuildLeftRightCode(tableName, idKey, parentIdKey);
//        }else if(treeMapper.checkLeafNodeLeftRightCodeAreNotContinuous(tableName, idKey, parentIdKey)  != null){
//            rebuildLeftRightCode(tableName, idKey, parentIdKey);
//        }
    }
}
