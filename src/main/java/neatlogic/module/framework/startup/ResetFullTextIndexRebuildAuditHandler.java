/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.module.framework.startup;

import neatlogic.framework.fulltextindex.dao.mapper.FullTextIndexRebuildAuditMapper;
import neatlogic.framework.startup.IStartup;
import neatlogic.framework.startup.StartupBase;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class ResetFullTextIndexRebuildAuditHandler  extends StartupBase {
    @Resource
    private FullTextIndexRebuildAuditMapper auditMapper;

    @Override
    public String getName() {
        return "重置重建全局搜索索引状态";
    }

    @Override
    public int sort() {
        return 1;
    }

    @Override
    public void executeForCurrentTenant() {
        auditMapper.resetFullTextIndexRebuildAuditStatus();
    }

    @Override
    public void executeForAllTenant() {

    }
}
