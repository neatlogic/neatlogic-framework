/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.module.framework.auditconfig.handler;

import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.auditconfig.core.AuditCleanerBase;
import codedriver.framework.common.config.Config;
import codedriver.framework.file.core.AuditType;
import codedriver.framework.healthcheck.dao.mapper.DatabaseFragmentMapper;
import codedriver.framework.restful.dao.mapper.ApiAuditMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Comparator;

@Component
public class ApiAuditCleaner extends AuditCleanerBase {

    private Logger logger = LoggerFactory.getLogger(ApiAuditCleaner.class);

    @Resource
    private ApiAuditMapper apiAuditMapper;
    @Resource
    private DatabaseFragmentMapper databaseFragmentMapper;

    @Override
    public String getName() {
        return "API-AUDIT";
    }

    @Override
    protected void myClean(int dayBefore) {
        String directoryPath = Config.DATA_HOME() + TenantContext.get().getTenantUuid() + File.separator + AuditType.API_AUDIT.getType();
        File dir = new File(directoryPath);
        if (dir.exists()) {
            File[] listFiles = dir.listFiles();
            Arrays.sort(listFiles, Comparator.comparing(File::lastModified));
            for (File file : listFiles) {
//                try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r")) {
//                    long fileLastPointer = randomAccessFile.length() - 1;
//                    for (long filePointer = fileLastPointer; filePointer != -1; filePointer--) {
//
//                    }
//                } catch (Exception e) {
//
//                }
            }
        }
        apiAuditMapper.deleteAuditByDayBefore(dayBefore);
        databaseFragmentMapper.rebuildTable(TenantContext.get().getDbName(), "api_audit");
    }
}
