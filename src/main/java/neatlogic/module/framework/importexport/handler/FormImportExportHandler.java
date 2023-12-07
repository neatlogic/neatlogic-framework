package neatlogic.module.framework.importexport.handler;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.crossover.CrossoverServiceFactory;
import neatlogic.framework.form.constvalue.FormHandler;
import neatlogic.framework.form.dao.mapper.FormMapper;
import neatlogic.framework.form.dto.FormVersionVo;
import neatlogic.framework.form.dto.FormVo;
import neatlogic.framework.form.exception.FormActiveVersionNotFoundExcepiton;
import neatlogic.framework.form.exception.FormNotFoundException;
import neatlogic.framework.form.service.IFormCrossoverService;
import neatlogic.framework.importexport.constvalue.FrameworkImportExportHandlerType;
import neatlogic.framework.importexport.core.ImportExportHandlerBase;
import neatlogic.framework.importexport.core.ImportExportHandlerType;
import neatlogic.framework.importexport.dto.ImportExportBaseInfoVo;
import neatlogic.framework.importexport.dto.ImportExportPrimaryChangeVo;
import neatlogic.framework.importexport.dto.ImportExportVo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipOutputStream;

@Component
public class FormImportExportHandler extends ImportExportHandlerBase {

    private List<String> handlerList = new ArrayList<>();

    {
        handlerList.add(FormHandler.FORMSELECT.getHandler());
        handlerList.add(FormHandler.FORMRADIO.getHandler());
        handlerList.add(FormHandler.FORMCHECKBOX.getHandler());
        handlerList.add(FormHandler.FORMTABLESELECTOR.getHandler());
    }

    @Resource
    private FormMapper formMapper;

    @Override
    public ImportExportHandlerType getType() {
        return FrameworkImportExportHandlerType.FORM;
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
        return formMapper.getFormByName(importExportBaseInfoVo.getName()) != null
                || formMapper.getFormByUuid((String) importExportBaseInfoVo.getPrimaryKey()) != null;
    }

    @Override
    public Object getPrimaryByName(ImportExportVo importExportVo) {
        FormVo form = formMapper.getFormByName(importExportVo.getName());
        if (form == null) {
            throw new FormNotFoundException(importExportVo.getName());
        }
        return form.getUuid();
    }

    @Override
    public Object importData(ImportExportVo importExportVo, List<ImportExportPrimaryChangeVo> primaryChangeList) {
        FormVersionVo formVersion = importExportVo.getData().toJavaObject(FormVersionVo.class);
        FormVo form = new FormVo();
        if (formMapper.getFormByUuid(formVersion.getFormUuid()) != null) {
            form.setUuid(formVersion.getFormUuid());
        } else {
            FormVo oldForm = formMapper.getFormByName(formVersion.getFormName());
            if (oldForm != null) {
                form.setUuid(oldForm.getUuid());
            }
        }
        form.setName(formVersion.getFormName());
        if (formMapper.getFormVersionByUuid(formVersion.getUuid()) != null) {
            form.setCurrentVersionUuid(formVersion.getUuid());
        }
        importHandle(formVersion, primaryChangeList);
        form.setFormConfig(formVersion.getFormConfig());
        IFormCrossoverService formCrossoverService = CrossoverServiceFactory.getApi(IFormCrossoverService.class);
        formCrossoverService.saveForm(form);
        return form.getUuid();
    }

    /**
     * 导入处理，更新依赖组件的唯一标识
     * @param formVersion
     * @param primaryChangeList
     */
    private void importHandle(FormVersionVo formVersion, List<ImportExportPrimaryChangeVo> primaryChangeList) {
        JSONObject formConfig = formVersion.getFormConfig();
        dependencyHandle(IMPORT, formConfig, null, null, primaryChangeList);
    }

    @Override
    protected ImportExportVo myExportData(Object primaryKey, List<ImportExportBaseInfoVo> dependencyList, ZipOutputStream zipOutputStream) {
        String uuid = (String) primaryKey;
        FormVo form = formMapper.getFormByUuid(uuid);
        if (form != null) {
            throw new FormNotFoundException(uuid);
        }
        FormVersionVo formVersion = formMapper.getActionFormVersionByFormUuid(uuid);
        if (formVersion == null) {
            throw new FormActiveVersionNotFoundExcepiton(form.getName());
        }
        exportHandle(formVersion, dependencyList, zipOutputStream);
        ImportExportVo importExportVo = new ImportExportVo(this.getType().getValue(), primaryKey, formVersion.getFormName());
        importExportVo.setDataWithObject(formVersion);
        return importExportVo;
    }

    /**
     * 导出处理，先导出依赖组件
     * @param formVersion
     * @param dependencyList
     * @param zipOutputStream
     */
    private void exportHandle(FormVersionVo formVersion, List<ImportExportBaseInfoVo> dependencyList, ZipOutputStream zipOutputStream) {
        JSONObject formConfig = formVersion.getFormConfig();
        dependencyHandle(EXPORT, formConfig, dependencyList, zipOutputStream, null);
    }

    /**
     * 导出处理，先导出依赖组件
     * 导入处理，更新依赖组件的唯一标识
     * @param action
     * @param formConfig
     * @param dependencyList
     * @param zipOutputStream
     * @param primaryChangeList
     */
    private void dependencyHandle(String action, JSONObject formConfig, List<ImportExportBaseInfoVo> dependencyList, ZipOutputStream zipOutputStream, List<ImportExportPrimaryChangeVo> primaryChangeList) {
        if (MapUtils.isEmpty(formConfig)) {
            return;
        }
        JSONArray tableList = formConfig.getJSONArray("tableList");
        if (CollectionUtils.isNotEmpty(tableList)) {
            for (int i = 0; i < tableList.size(); i++) {
                JSONObject tableObj = tableList.getJSONObject(i);
                if (MapUtils.isEmpty(tableObj)) {
                    continue;
                }
                JSONObject component = tableObj.getJSONObject("component");
                componentHandle(action, component, dependencyList, zipOutputStream, primaryChangeList);

            }
        }
        JSONArray sceneList = formConfig.getJSONArray("sceneList");
        if (CollectionUtils.isNotEmpty(sceneList)) {
            for (int i = 0; i < sceneList.size(); i++) {
                JSONObject sceneObj = sceneList.getJSONObject(i);
                dependencyHandle(action, sceneObj, dependencyList, zipOutputStream, primaryChangeList);
            }
        }
    }

    /**
     * 处理表单单个组件配置信息
     * @param action
     * @param component
     * @param dependencyList
     * @param zipOutputStream
     * @param primaryChangeList
     */
    private void componentHandle(String action, JSONObject component, List<ImportExportBaseInfoVo> dependencyList, ZipOutputStream zipOutputStream, List<ImportExportPrimaryChangeVo> primaryChangeList) {
        if (MapUtils.isEmpty(component)) {
            return;
        }
        Boolean inherit = component.getBoolean("inherit");
        if (Objects.equals(inherit, true)) {
            return;
        }
        String handler = component.getString("handler");
        if (handlerList.contains(handler)) {
            // 单选框、复选框、下拉框、表格选择组件
            JSONObject config = component.getJSONObject("config");
            if (MapUtils.isEmpty(config)) {
                return;
            }
            String dataSource = config.getString("dataSource");
            if (!Objects.equals(dataSource, "matrix")) {
                return;
            }
            String matrixUuid = config.getString("matrixUuid");
            if (StringUtils.isBlank(matrixUuid)) {
                return;
            }
            if (action == IMPORT) {
                Object newPrimaryKey = getNewPrimaryKey(FrameworkImportExportHandlerType.MATRIX, matrixUuid, primaryChangeList);
                if (newPrimaryKey != null) {
                    config.put("matrixUuid", newPrimaryKey);
                }
            } else if (action == EXPORT) {
                doExportData(FrameworkImportExportHandlerType.MATRIX, matrixUuid, dependencyList, zipOutputStream);
            }
        } else if (Objects.equals(handler, FormHandler.FORMTABLEINPUTER.getHandler())) {
            // 表格输入组件
            JSONObject config = component.getJSONObject("config");
            if (MapUtils.isEmpty(config)) {
                return;
            }
            JSONArray dataConfig = config.getJSONArray("dataConfig");
            for (int i = 0; i < dataConfig.size(); i++) {
                JSONObject dataObj = dataConfig.getJSONObject(i);
                componentHandle(action, dataObj, dependencyList, zipOutputStream, primaryChangeList);
            }
        } else if (Objects.equals(handler, FormHandler.FORMTAB.getHandler()) || Objects.equals(handler, FormHandler.FORMCOLLAPSE.getHandler())) {
            // 选项卡、折叠面板
            JSONArray componentArray = component.getJSONArray("component");
            for (int i = 0; i < componentArray.size(); i++) {
                JSONObject componentObj = componentArray.getJSONObject(i);
                componentHandle(action, componentObj, dependencyList, zipOutputStream, primaryChangeList);
            }
        } else if (Objects.equals(handler, FormHandler.FORMSUBASSEMBLY.getHandler())) {
            // 子表单
            JSONObject formData = component.getJSONObject("formData");
            if (MapUtils.isEmpty(formData)) {
                return;
            }
            JSONObject formConfig = formData.getJSONObject("formConfig");
            dependencyHandle(action, formConfig, dependencyList, zipOutputStream, primaryChangeList);
        }
    }
}
