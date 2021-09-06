/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.module.framework.form.attribute.handler;

import codedriver.framework.common.constvalue.ParamType;
import codedriver.framework.form.attribute.core.FormHandlerBase;
import codedriver.framework.form.constvalue.FormConditionModel;
import codedriver.framework.form.dto.AttributeDataVo;
import codedriver.framework.form.exception.AttributeValidException;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import java.util.List;

/**
 * @author linbq
 * @since 2021/8/24 14:13
 **/
@Component
public class AccountsHandler extends FormHandlerBase {

    private final static JSONArray theadList = new JSONArray();
    static {
        JSONObject name = new JSONObject();
        name.put("title", "资产名");
        name.put("key", "name");
        theadList.add(name);
        JSONObject ip = new JSONObject();
        ip.put("title", "资产IP");
        ip.put("key", "ip");
        theadList.add(ip);
        JSONObject accountName = new JSONObject();
        accountName.put("title", "账号名称");
        accountName.put("key", "accountName");
        theadList.add(accountName);
        JSONObject account = new JSONObject();
        account.put("title", "用户名");
        account.put("key", "account");
        theadList.add(account);
        JSONObject protocol = new JSONObject();
        protocol.put("title", "连接协议");
        protocol.put("key", "protocol");
        theadList.add(protocol);
        JSONObject port = new JSONObject();
        port.put("title", "端口");
        port.put("key", "port");
        theadList.add(port);
    }

    @Override
    public String getHandler() {
        return "formaccounts";
    }

    @Override
    public String getHandlerName() {
        return "账号组件";
    }

    @Override
    public String getHandlerType(FormConditionModel model) {
        return null;
    }

    @Override
    public String getIcon() {
        return "tsfont-group";
    }

    @Override
    public ParamType getParamType() {
        return ParamType.ARRAY;
    }

    @Override
    public String getDataType() {
        return "list";
    }

    @Override
    public boolean isAudit() {
        return true;
    }

    @Override
    public boolean isConditionable() {
        return true;
    }

    @Override
    public boolean isShowable() {
        return true;
    }

    @Override
    public boolean isValueable() {
        return true;
    }

    @Override
    public boolean isFilterable() {
        return true;
    }

    @Override
    public boolean isExtendable() {
        return false;
    }

    @Override
    public boolean isForTemplate() {
        return true;
    }

    @Override
    public String getModule() {
        return "framework";
    }

    @Override
    public boolean valid(AttributeDataVo attributeDataVo, JSONObject configObj) throws AttributeValidException {
        return false;
    }

    @Override
    public Object valueConversionText(AttributeDataVo attributeDataVo, JSONObject configObj) {
        return "已更新";
    }

    @Override
    public Object dataTransformationForEmail(AttributeDataVo attributeDataVo, JSONObject configObj) {
        JSONObject tableObj = new JSONObject();
        JSONObject dataObj = (JSONObject) attributeDataVo.getDataObj();
        if (MapUtils.isNotEmpty(dataObj)) {
            JSONArray tbodyList = dataObj.getJSONArray("selectedDataList");
            if (CollectionUtils.isNotEmpty(tbodyList)) {
                tableObj.put("theadList", theadList);
                tableObj.put("tbodyList", tbodyList);
            }
        }
        return tableObj;
    }

    @Override
    public Object textConversionValue(List<String> values, JSONObject config) {
        return null;
    }
}
