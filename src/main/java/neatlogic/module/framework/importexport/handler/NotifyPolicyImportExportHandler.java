package neatlogic.module.framework.importexport.handler;

import neatlogic.framework.asynchronization.threadlocal.UserContext;
import neatlogic.framework.importexport.constvalue.FrameworkImportExportHandlerType;
import neatlogic.framework.importexport.core.ImportExportHandlerBase;
import neatlogic.framework.importexport.core.ImportExportHandlerType;
import neatlogic.framework.importexport.dto.ImportExportBaseInfoVo;
import neatlogic.framework.importexport.dto.ImportExportPrimaryChangeVo;
import neatlogic.framework.importexport.dto.ImportExportVo;
import neatlogic.framework.notify.core.INotifyPolicyHandler;
import neatlogic.framework.notify.core.NotifyPolicyHandlerFactory;
import neatlogic.framework.notify.dao.mapper.NotifyMapper;
import neatlogic.framework.notify.dto.NotifyPolicyVo;
import neatlogic.framework.notify.exception.NotifyPolicyHandlerNotFoundException;
import neatlogic.framework.notify.exception.NotifyPolicyNotFoundException;
import neatlogic.framework.util.$;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipOutputStream;

@Component
public class NotifyPolicyImportExportHandler extends ImportExportHandlerBase {

    @Resource
    private NotifyMapper notifyMapper;

    @Override
    public ImportExportHandlerType getType() {
        return FrameworkImportExportHandlerType.NOTIFY_POLICY;
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
        return getNotifyPolicyByName(importExportBaseInfoVo.getName()) != null;
    }

    @Override
    public Object getPrimaryByName(ImportExportVo importExportVo) {
        NotifyPolicyVo notifyPolicy = getNotifyPolicyByName(importExportVo.getName());
        if (notifyPolicy == null) {
            throw new NotifyPolicyNotFoundException(importExportVo.getName());
        }
        return notifyPolicy.getId();
    }

    @Override
    public Object importData(ImportExportVo importExportVo, List<ImportExportPrimaryChangeVo> primaryChangeList) {
        NotifyPolicyVo notifyPolicy = importExportVo.getData().toJavaObject(NotifyPolicyVo.class);
        NotifyPolicyVo oldNotifyPolicy = getNotifyPolicyByName(importExportVo.getName());
        if (oldNotifyPolicy != null) {
            notifyPolicy.setId(oldNotifyPolicy.getId());
            notifyPolicy.setLcu(UserContext.get().getUserUuid(true));
            notifyMapper.updateNotifyPolicyById(notifyPolicy);
        } else {
            if (notifyMapper.getNotifyPolicyById(notifyPolicy.getId()) != null) {
                notifyPolicy.setId(null);
            }
            notifyPolicy.setFcu(UserContext.get().getUserUuid(true));
            notifyMapper.insertNotifyPolicy(notifyPolicy);
        }
        return notifyPolicy.getId();
    }

    @Override
    protected ImportExportVo myExportData(Object primaryKey, List<ImportExportBaseInfoVo> dependencyList, ZipOutputStream zipOutputStream) {
        Long id = (Long) primaryKey;
        NotifyPolicyVo notifyPolicy = notifyMapper.getNotifyPolicyById(id);
        if (notifyPolicy == null) {
            throw new NotifyPolicyNotFoundException(id);
        }
        INotifyPolicyHandler notifyPolicyHandler = NotifyPolicyHandlerFactory.getHandler(notifyPolicy.getHandler());
        if (notifyPolicyHandler == null) {
            throw new NotifyPolicyHandlerNotFoundException(notifyPolicy.getHandler());
        }
        String name = $.t(notifyPolicyHandler.getName()) + "/" + notifyPolicy.getName();
        ImportExportVo importExportVo = new ImportExportVo(this.getType().getValue(), primaryKey, name);
        importExportVo.setDataWithObject(notifyPolicy);
        return importExportVo;
    }

    private NotifyPolicyVo getNotifyPolicyByName(String name) {
        String[] spilt = name.split("/");
        String handlerName = spilt[0];
        String notifyPolicyName = spilt[1];
        List<NotifyPolicyVo> list = notifyMapper.getNotifyPolicyListByName(notifyPolicyName);
        for (NotifyPolicyVo notifyPolicy : list) {
            INotifyPolicyHandler notifyPolicyHandler = NotifyPolicyHandlerFactory.getHandler(notifyPolicy.getHandler());
            if (notifyPolicyHandler == null) {
                continue;
            }
            if (Objects.equals($.t(notifyPolicyHandler.getName()), handlerName)) {
                return notifyPolicy;
            }
        }
        return null;
    }
}
