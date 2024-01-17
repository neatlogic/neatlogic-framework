package neatlogic.module.framework.importexport.handler;

import neatlogic.framework.asynchronization.threadlocal.UserContext;
import neatlogic.framework.exception.integration.IntegrationNotFoundException;
import neatlogic.framework.importexport.constvalue.FrameworkImportExportHandlerType;
import neatlogic.framework.importexport.core.ImportExportHandlerBase;
import neatlogic.framework.importexport.core.ImportExportHandlerType;
import neatlogic.framework.importexport.dto.ImportExportBaseInfoVo;
import neatlogic.framework.importexport.dto.ImportExportPrimaryChangeVo;
import neatlogic.framework.importexport.dto.ImportExportVo;
import neatlogic.framework.integration.dao.mapper.IntegrationMapper;
import neatlogic.framework.integration.dto.IntegrationVo;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.zip.ZipOutputStream;

@Component
public class IntegrationImportExportHandler extends ImportExportHandlerBase {

    @Resource
    private IntegrationMapper integrationMapper;

    @Override
    public ImportExportHandlerType getType() {
        return FrameworkImportExportHandlerType.INTEGRATION;
    }

    @Override
    public boolean checkImportAuth(ImportExportVo importExportVo) {
        return true;
    }

    @Override
    public boolean checkExportAuth(Object primaryKey) {
        return true;
    }

    @Override
    public boolean checkIsExists(ImportExportBaseInfoVo importExportBaseInfoVo) {
        return integrationMapper.getIntegrationByName(importExportBaseInfoVo.getName()) != null;
    }

    @Override
    public Object getPrimaryByName(ImportExportVo importExportVo) {
        IntegrationVo integration = integrationMapper.getIntegrationByName(importExportVo.getName());
        if (integration == null) {
            throw new IntegrationNotFoundException(importExportVo.getName());
        }
        return integration.getUuid();
    }

    @Override
    public Object importData(ImportExportVo importExportVo, List<ImportExportPrimaryChangeVo> primaryChangeList) {
        IntegrationVo integration = importExportVo.getData().toJavaObject(IntegrationVo.class);
        IntegrationVo oldIntegration = integrationMapper.getIntegrationByName(importExportVo.getName());
        if (oldIntegration != null) {
            integration.setUuid(oldIntegration.getUuid());
            integration.setLcu(UserContext.get().getUserUuid());
            integrationMapper.updateIntegration(integration);
        } else {
            if (integrationMapper.getIntegrationByUuid(integration.getUuid()) != null) {
                integration.setUuid(null);
            }
            integration.setFcu(UserContext.get().getUserUuid());
            integrationMapper.insertIntegration(integration);
        }
        return integration.getUuid();
    }

    @Override
    protected ImportExportVo myExportData(Object primaryKey, List<ImportExportBaseInfoVo> dependencyList, ZipOutputStream zipOutputStream) {
        String uuid = (String) primaryKey;
        IntegrationVo integration = integrationMapper.getIntegrationByUuid(uuid);
        if (integration == null) {
            throw new IntegrationNotFoundException(uuid);
        }
        ImportExportVo importExportVo = new ImportExportVo(this.getType().getValue(), primaryKey, integration.getName());
        importExportVo.setDataWithObject(integration);
        return importExportVo;
    }
}
