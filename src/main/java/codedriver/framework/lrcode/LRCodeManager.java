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

/**
 * @Title: TreeServiceImpl
 * @Package codedriver.framework.tree.service
 * @Description: 左右编码工具类
 * @Author: linbq
 * @Date: 2021/3/17 6:03
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 **/
@Service
public class LRCodeManager {

    private static TreeMapper treeMapper;

    @Autowired
    public LRCodeManager(TreeMapper _treeMapper){
        treeMapper = _treeMapper;
    }

    public static int addTreeNode(String tableName, String idKey, Object idValue, int lft, int rht) {
        //计算被移动块右边的节点移动步长
        int step = rht - lft + 1;
        //更新插入位置右边的左右编码值
        treeMapper.batchUpdateTreeNodeLeftCode(tableName, lft, step);
        treeMapper.batchUpdateTreeNodeRightCode(tableName, lft, step);
        treeMapper.updateTreeNodeLeftRightCodeById(tableName, idKey, idValue, lft, rht);
        return 1;
    }

    public static int moveTreeNode(String tableName, String idKey, String parentIdKey, Object idValue, String moveType, Object targetIdValue) {
//        initializeLRCode(tableName);
        if(Objects.equals(idValue, targetIdValue)) {
            throw new MoveTargetNodeIllegalException();
        }
        TreeNodeVo moveCatalog = treeMapper.getTreeNodeById(tableName, idKey, parentIdKey, idValue);
        //判断被移动的服务目录是否存在
        if(moveCatalog == null) {
            throw new TreeNodeNotFoundException(tableName, idValue);
        }
        //判断移动后的父节点是否在当前节点的后代节点中
        if(treeMapper.checkTreeNodeIsExistsByLeftRightCode(tableName, idKey, targetIdValue, moveCatalog.getLft(), moveCatalog.getRht()) > 0) {
            throw new MoveTargetNodeIllegalException();
        }
        //移动到目标节点前面或后面
        TreeNodeVo targetCatalog = treeMapper.getTreeNodeById(tableName, idKey, parentIdKey, targetIdValue);
        //判断目标节点服务目录是否存在
        if(targetCatalog == null) {
            throw new TreeNodeNotFoundException(tableName, targetIdValue);
        }
        Object parentIdValue;
        if(MoveType.INNER.getValue().equals(moveType)) {
            parentIdValue = targetIdValue;
            if(Objects.equals(moveCatalog.getParentIdValue(), targetIdValue)){
                if(moveCatalog.getRht() + 1 == targetCatalog.getRht()){
                    return 0;
                }
            }
        }else {
            parentIdValue = targetCatalog.getParentIdValue();
            if(MoveType.PREV.getValue().equals(moveType)) {
                if(Objects.equals(moveCatalog.getParentIdValue(), parentIdValue)){
                    if(moveCatalog.getRht() + 1 == targetCatalog.getLft()){
                        return 0;
                    }
                }
            }else {
                if(Objects.equals(moveCatalog.getParentIdValue(), parentIdValue)){
                    if(targetCatalog.getRht() + 1 == moveCatalog.getLft()){
                        return 0;
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
        if(targetCatalog.getLft() >= moveCatalog.getLft()){
            targetCatalog.setLft(targetCatalog.getLft() - step);
        }
        if(targetCatalog.getRht() >= moveCatalog.getLft()){
            targetCatalog.setRht(targetCatalog.getRht() - step);
        }
        //找出被移动块移动后左编码值
        int lft = 0;
        //目标节点uuid
        if(MoveType.INNER.getValue().equals(moveType)) {//移动到末尾
            lft = targetCatalog.getRht();
        }else {
            if(MoveType.PREV.getValue().equals(moveType)) {
                lft = targetCatalog.getLft();
            }else {
                lft = targetCatalog.getRht() + 1;
            }
        }
        treeMapper.updateTreeNodeParentIdById(tableName, idKey, parentIdKey, idValue, parentIdValue);

        //更新新位置右边的左右编码值
        treeMapper.batchUpdateTreeNodeLeftCode(tableName, lft, step);
        treeMapper.batchUpdateTreeNodeRightCode(tableName, lft, step);

        //更新被移动块中节点的左右编码值
        treeMapper.batchUpdateTreeNodeLeftRightCodeByLeftRightCode(tableName, moveCatalog.getLft() - moveCatalog.getRht(), moveCatalog.getRht() - moveCatalog.getRht(), lft - moveCatalog.getLft() + moveCatalog.getRht());
        return 1;
    }

    public static int deleteTreeNode(String tableName, int lft, int rht) {
        //计算被移动块右边的节点移动步长
        int step = rht - lft + 1;
        //更新删除位置右边的左右编码值
        treeMapper.batchUpdateTreeNodeLeftCode(tableName, lft, -step);
        treeMapper.batchUpdateTreeNodeRightCode(tableName, lft, -step);
        return 1;
    }

    public static void rebuildLeftRightCode(String tableName, String idKey, String parentIdKey) {
        rebuildLeftRightCode(tableName, idKey, parentIdKey, TreeNodeVo.ROOT_UUID, 1);

    }

    private static Integer rebuildLeftRightCode(String tableName, String idKey, String parentIdKey, Object parentIdValue, int parentLft) {
        List<TreeNodeVo> catalogList= treeMapper.getTreeNodeListByParentId(tableName, parentIdKey, parentIdValue);
        for(TreeNodeVo catalog : catalogList) {
            if(catalog.getChildrenCount() == 0) {
                treeMapper.updateTreeNodeLeftRightCodeById(tableName, idKey, catalog.getIdValue(), parentLft + 1, parentLft + 2);
                parentLft += 2;
            }else {
                int lft = parentLft + 1;
                parentLft = rebuildLeftRightCode(tableName, idKey, parentIdKey, catalog.getIdValue(), lft);
                treeMapper.updateTreeNodeLeftRightCodeById(tableName, idKey, catalog.getIdValue(), lft, parentLft + 1);
                parentLft += 1;
            }
        }
        return parentLft;
    }

    public static void initializeLRCode(String tableName, String idKey, String parentIdKey){
        LockManager.getLockById(tableName);
        if(treeMapper.checkLeftRightCodeIsWrong(tableName, idKey, parentIdKey) > 0) {
            rebuildLeftRightCode(tableName, idKey, parentIdKey);
        }
    }
}
