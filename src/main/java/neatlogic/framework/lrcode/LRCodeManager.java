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

package neatlogic.framework.lrcode;

import neatlogic.framework.lock.core.LockManager;
import neatlogic.framework.lrcode.constvalue.MoveType;
import neatlogic.framework.lrcode.dao.mapper.TreeMapper;
import neatlogic.framework.lrcode.dto.TreeNodeVo;
import neatlogic.framework.lrcode.exception.MoveTargetNodeIllegalException;
import neatlogic.framework.lrcode.exception.TreeNodeNotFoundException;
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
    public static int beforeAddTreeNode(String tableName, String idKey, String parentIdKey, Object parentIdValue, String condition) {
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
        treeMapper.batchUpdateTreeNodeLeftCode(tableName, lft, 2, condition);
        treeMapper.batchUpdateTreeNodeRightCode(tableName, lft, 2, condition);
        return lft;
    }

    public static int beforeAddTreeNode(String tableName, String idKey, String parentIdKey, Object parentIdValue) {
        return beforeAddTreeNode(tableName, idKey, parentIdKey, parentIdValue, null);
    }

    public static int[] moveTreeNode(String tableName, String idKey, String parentIdKey, Object idValue, MoveType moveType, Object targetIdValue) {
        return moveTreeNode(tableName, idKey, parentIdKey, idValue, moveType, targetIdValue, null);
    }

    /**
     * @Description: 移动节点
     * @Author: linbq
     * @Date: 2021/3/17 17:41
     * @Params: [tableName, idKey, parentIdKey, idValue, moveType, targetIdValue]
     * @Returns: void
     **/
    public static int[] moveTreeNode(String tableName, String idKey, String parentIdKey, Object idValue, MoveType moveType, Object targetIdValue, String condition) {
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
        if (targetIdValue == null || Objects.equals(targetIdValue.toString(), "0")) {
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
        treeMapper.batchUpdateTreeNodeLeftRightCodeByLeftRightCode(tableName, moveCatalog.getLft(), moveCatalog.getRht(), -moveCatalog.getRht(), condition);
        //计算被移动块右边的节点移动步长
        int step = moveCatalog.getRht() - moveCatalog.getLft() + 1;
        //更新旧位置右边的左右编码值
        treeMapper.batchUpdateTreeNodeLeftCode(tableName, moveCatalog.getLft(), -step, condition);
        treeMapper.batchUpdateTreeNodeRightCode(tableName, moveCatalog.getLft(), -step, condition);
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
        treeMapper.batchUpdateTreeNodeLeftCode(tableName, lft, step, condition);
        treeMapper.batchUpdateTreeNodeRightCode(tableName, lft, step, condition);

        //更新被移动块中节点的左右编码值
        treeMapper.batchUpdateTreeNodeLeftRightCodeByLeftRightCode(tableName, moveCatalog.getLft() - moveCatalog.getRht(), 0, lft - moveCatalog.getLft() + moveCatalog.getRht(), condition);
        int[] lftRht = new int[2];
        lftRht[0] = lft;
        lftRht[1] = lft + step - 1;
        return lftRht;
    }

    public static void beforeDeleteTreeNode(String tableName, String idKey, String parentIdKey, Object idValue) {
        beforeDeleteTreeNode(tableName, idKey, parentIdKey, idValue, null);
    }

    /**
     * @Description: 删除节点，调用方删除A节点前，先调用该方法将A节点左右编码置空并更新其他节点左右编码，再删除A节点
     * @Author: linbq
     * @Date: 2021/3/17 17:42
     * @Params: [tableName, lft, rht]
     * @Returns: void
     **/
    public static void beforeDeleteTreeNode(String tableName, String idKey, String parentIdKey, Object idValue, String condition) {
        initializeLRCode(tableName, idKey, parentIdKey);
        TreeNodeVo treeNodeVo = treeMapper.getTreeNodeById(tableName, idKey, parentIdKey, idValue);
        if (treeNodeVo == null) {
            throw new TreeNodeNotFoundException(tableName, idValue);
        }
        treeMapper.batchUpdateTreeNodeLeftRightCodeToNullByLeftRightCode(tableName, treeNodeVo.getLft(), treeNodeVo.getRht());
        //计算被移动块右边的节点移动步长
        int step = treeNodeVo.getRht() - treeNodeVo.getLft() + 1;
        //更新删除位置右边的左右编码值
        treeMapper.batchUpdateTreeNodeLeftCode(tableName, treeNodeVo.getLft(), -step, condition);
        treeMapper.batchUpdateTreeNodeRightCode(tableName, treeNodeVo.getLft(), -step, condition);
    }

    public static void rebuildLeftRightCodeOrderBySortKey(String tableName, String idKey, String parentIdKey, String sortKey) {
        rebuildLeftRightCode(tableName, idKey, parentIdKey, null, 1, null, sortKey);
    }

    public static void rebuildLeftRightCodeOrderBySortKey(String tableName, String idKey, String parentIdKey, String condition, String sortKey) {
        rebuildLeftRightCode(tableName, idKey, parentIdKey, null, 1, condition, sortKey);
    }

    public static void rebuildLeftRightCode(String tableName, String idKey, String parentIdKey) {
        rebuildLeftRightCode(tableName, idKey, parentIdKey, null, 1, null, null);
    }

    public static void rebuildLeftRightCode(String tableName, String idKey, String parentIdKey, String condition) {
        rebuildLeftRightCode(tableName, idKey, parentIdKey, null, 1, condition, null);
    }


    private static Integer rebuildLeftRightCode(String tableName, String idKey, String parentIdKey, Object parentIdValue, int parentLft, String condition, String sortKey) {
        List<TreeNodeVo> catalogList = treeMapper.getTreeNodeListByParentId(tableName, idKey, parentIdKey, parentIdValue, condition, sortKey);
        for (TreeNodeVo catalog : catalogList) {
            if (catalog.getChildrenCount() == 0) {
                treeMapper.updateTreeNodeLeftRightCodeById(tableName, idKey, catalog.getIdValue(), parentLft + 1, parentLft + 2);
                parentLft += 2;
            } else {
                int lft = parentLft + 1;
                parentLft = rebuildLeftRightCode(tableName, idKey, parentIdKey, catalog.getIdValue(), lft, condition, sortKey);
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
