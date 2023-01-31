/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.module.framework.file.plugin;

import neatlogic.framework.auth.core.AuthActionChecker;
import neatlogic.framework.auth.label.DATA_WAREHOUSE_MODIFY;
import neatlogic.framework.exception.type.PermissionDeniedException;
import neatlogic.framework.file.core.FileTypeHandlerBase;
import neatlogic.framework.file.dto.FileVo;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

@Component
public class DataWarehouseDataSourceFileHandler extends FileTypeHandlerBase {

    @Override
    public boolean valid(String userUuid, FileVo fileVo, JSONObject jsonObj) throws PermissionDeniedException {
        if (!AuthActionChecker.check(DATA_WAREHOUSE_MODIFY.class)) {
            throw new PermissionDeniedException(DATA_WAREHOUSE_MODIFY.class);
        }
        return true;
    }

    @Override
    public String getName() {
        return "DATASOURCE";
    }

    @Override
    public String getDisplayName() {
        return "数据仓库数据源配置文件";
    }

    @Override
    public void afterUpload(FileVo fileVo, JSONObject jsonObj) {

    }

    @Override
    protected boolean myDeleteFile(FileVo fileVo, JSONObject paramObj) {
        return true;
    }
}
