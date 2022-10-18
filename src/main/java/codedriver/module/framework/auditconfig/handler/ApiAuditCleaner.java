/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.module.framework.auditconfig.handler;

import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.auditconfig.core.AuditCleanerBase;
import codedriver.framework.common.util.FileUtil;
import codedriver.framework.healthcheck.dao.mapper.DatabaseFragmentMapper;
import codedriver.framework.restful.dao.mapper.ApiAuditMapper;
import codedriver.framework.restful.dto.ApiAuditPathVo;
import codedriver.framework.restful.dto.ApiAuditVo;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

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
        //查询出指定时间点的最后一条记录
        ApiAuditVo lastApiAuditVo = apiAuditMapper.getLastApiAuditByDayBefore(dayBefore);
        if (lastApiAuditVo != null) {
            //找出最小的记录路径id
            Long minPathId = null;
            Long paramPathId = lastApiAuditVo.getParamPathId();
            Long resultPathId = lastApiAuditVo.getResultPathId();
            Long errorPathId = lastApiAuditVo.getErrorPathId();
            if (paramPathId != null) {
                minPathId = paramPathId;
            } else if (resultPathId != null) {
                minPathId = resultPathId;
            } else if (errorPathId != null) {
                minPathId = errorPathId;
            }
            if (minPathId != null) {
                // 这个最小记录路径id的对应的记录文件不能删除，但它之前的记录路径文件都可以删除
                String cannotDeletePath = "";
                ApiAuditPathVo apiAuditPathVo = apiAuditMapper.getApiAuditPathById(minPathId);
                if (apiAuditPathVo != null) {
                    cannotDeletePath = apiAuditPathVo.getPath();
                    Integer archiveIndex = apiAuditPathVo.getArchiveIndex();
                    if (archiveIndex != null) {
                        cannotDeletePath = cannotDeletePath + "." + archiveIndex;
                    }
                }
                List<ApiAuditPathVo> apiAuditPathList = apiAuditMapper.getDistinctPathAndArchiveIndexByIdBefore(minPathId);
                if (CollectionUtils.isNotEmpty(apiAuditPathList)) {
                    // 找出可以删除的文件路径集合
                    List<String> pathList = new ArrayList<>();
                    for (ApiAuditPathVo auditPathVo : apiAuditPathList) {
                        String path = auditPathVo.getPath();
                        Integer archiveIndex = apiAuditPathVo.getArchiveIndex();
                        if (archiveIndex != null) {
                            path = path + "." + archiveIndex;
                        }
                        if (Objects.equals(path, cannotDeletePath)) {
                            continue;
                        }
                        if (pathList.contains(path)) {
                            continue;
                        }
                        pathList.add(path);
                    }
                    // 遍历删除文件
                    if (CollectionUtils.isNotEmpty(pathList)) {
                        for (String path : pathList) {
                            try {
                                if (!path.startsWith("file:")) {
                                    path = "file:" + path;
                                }
                                FileUtil.deleteData(path);
                            } catch (Exception e) {
                                logger.error(e.getMessage(), e);
                            }
                        }
                    }
                    // 删除api_audit_path表数据
                    apiAuditMapper.deleteApiAuditPathByIdBefore(minPathId);
                }
            }
        }
        apiAuditMapper.deleteAuditByDayBefore(dayBefore);
        databaseFragmentMapper.rebuildTable(TenantContext.get().getDbName(), "api_audit");
    }
}
