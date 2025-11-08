/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */

package neatlogic.module.framework.auditconfig.handler;

import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.auditconfig.core.AuditCleanerBase;
import neatlogic.framework.common.config.Config;
import neatlogic.framework.file.core.AuditType;
import neatlogic.framework.healthcheck.dao.mapper.DatabaseFragmentMapper;
import neatlogic.framework.restful.dao.mapper.ApiAuditMapper;
import neatlogic.framework.util.TimeUtil;
import org.apache.commons.io.input.ReversedLinesFileReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;

@Component
public class ApiAuditCleaner extends AuditCleanerBase {

    private static final Logger logger = LoggerFactory.getLogger(ApiAuditCleaner.class);

    @Resource
    private ApiAuditMapper apiAuditMapper;
    @Resource
    private DatabaseFragmentMapper databaseFragmentMapper;

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(TimeUtil.YYYY_MM_DD_HH_MM_SS_SSS);

    private final String PREFIX = "fileFooter##########";

    @Override
    public String getName() {
        return "API-AUDIT";
    }

    @Override
    protected void myClean(int dayBefore) {
        String directoryPath = Config.AUDIT_HOME() + TenantContext.get().getTenantUuid() + File.separator + AuditType.API_AUDIT.getType();
        File dir = new File(directoryPath);
        if (dir.exists()) {
            File[] listFiles = dir.listFiles();
            if (listFiles != null) {
                Arrays.sort(listFiles, Comparator.comparing(File::lastModified));
                for (File file : listFiles) {
                    try (ReversedLinesFileReader rlfr = new ReversedLinesFileReader(file, StandardCharsets.UTF_8)) {
                        String lastLine = rlfr.readLine();
                        if (lastLine.startsWith(PREFIX)) {
                            String formatStr = lastLine.substring(PREFIX.length(), PREFIX.length() + TimeUtil.YYYY_MM_DD_HH_MM_SS_SSS.length());
                            LocalDate endDate = LocalDate.parse(formatStr, dateTimeFormatter);
                            if (LocalDate.now().toEpochDay() - endDate.toEpochDay() > dayBefore) {
                                file.delete();
                            }
                        }
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }
        }
        apiAuditMapper.deleteAuditByDayBefore(dayBefore);
        databaseFragmentMapper.rebuildTable(TenantContext.get().getDbName(), "api_audit");
    }
}
