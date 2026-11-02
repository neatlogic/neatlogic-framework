package neatlogic.module.framework.dashboard.integration.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.exception.integration.IntegrationRequestResultFormatInvalidException;
import neatlogic.framework.exception.integration.IntegrationRequestResultIsEmptyException;
import neatlogic.framework.integration.core.IntegrationHandlerBase;
import neatlogic.framework.integration.dto.IntegrationResultVo;
import neatlogic.framework.integration.dto.IntegrationVo;
import neatlogic.framework.integration.dto.PatternVo;
import neatlogic.framework.util.$;
import neatlogic.framework.dashboard.datasource.exception.DashboardIntegrationRequestFailedException;
import neatlogic.framework.dashboard.datasource.exception.DashboardIntegrationResultInvalidException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/** 大屏集成规范，固定查询输入、表头与分页输出，实际请求及审计仍由集成基类执行。 */
@Component
public class DashboardIntegrationHandler extends IntegrationHandlerBase {

    /** 按当前请求语言展示规范名称。 */
    @Override
    public String getName() {
        return $.t("nmfdih.dashboardintegrationhandler.getname");
    }

    /** 大屏输入和输出均有固定规范。 */
    @Override
    public Integer hasPattern() {
        return 1;
    }

    /** 允许在固定规范之外追加集成自身需要的输入及默认值。 */
    @Override
    public Integer allowEditInputParam() {
        return 1;
    }

    /** 允许追加输出参数，组件取数只消费固定输出契约。 */
    @Override
    public Integer allowEditOutputParam() {
        return 1;
    }

    /** 输入保留筛选、排序及分页参数，表头在输出规范中配置。 */
    @Override
    public List<PatternVo> getInputPattern() {
        List<PatternVo> patterns = new ArrayList<>();
        patterns.add(new PatternVo("conditionList", "input", ApiParamType.JSONARRAY, 0, $.t("nmfdih.dashboardintegrationhandler.input.conditionlist")));
        patterns.add(new PatternVo("sortList", "input", ApiParamType.JSONARRAY, 0, $.t("nmfdih.dashboardintegrationhandler.input.sortlist")));
        patterns.add(new PatternVo("limit", "input", ApiParamType.INTEGER, 0, $.t("nmfdih.dashboardintegrationhandler.input.limit")));
        patterns.add(new PatternVo("currentPage", "input", ApiParamType.INTEGER, 0, $.t("common.currentpage")));
        patterns.add(new PatternVo("pageSize", "input", ApiParamType.INTEGER, 0, $.t("common.pagesize")));
//        patterns.add(new PatternVo("isExpired", "input", ApiParamType.INTEGER, 0, $.t("nmfdih.dashboardintegrationhandler.input.isexpired")));
//        patterns.add(new PatternVo("needPage", "input", ApiParamType.BOOLEAN, 0, $.t("nmfdih.dashboardintegrationhandler.input.needpage")));
        return patterns;
    }

    /** 表头必须在输出规范配置默认值，空 config 省略；外部输出仍只要求对象行数组及四个整数分页字段。 */
    @Override
    public List<PatternVo> getOutputPattern() {
        List<PatternVo> patterns = new ArrayList<>();
        PatternVo thead = new PatternVo("theadList", "output", ApiParamType.JSONARRAY, 1,
                $.t("nmfdih.dashboardintegrationhandler.output.theadlist"));
        thead.addChild(new PatternVo("key", "output", ApiParamType.STRING, 1, $.t("nmfdih.dashboardintegrationhandler.output.key")));
        thead.addChild(new PatternVo("title", "output", ApiParamType.STRING, 1, $.t("nmfdih.dashboardintegrationhandler.output.title")));
        thead.addChild(new PatternVo("type", "output", ApiParamType.STRING, 0, $.t("nmfdih.dashboardintegrationhandler.output.type")));
        thead.addChild(new PatternVo("isCondition", "output", ApiParamType.INTEGER, 0, $.t("nmfdih.dashboardintegrationhandler.output.iscondition")));
        thead.addChild(new PatternVo("inputType", "output", ApiParamType.STRING, 0, $.t("nmfdih.dashboardintegrationhandler.output.inputtype")));
        thead.addChild(new PatternVo("config", "output", ApiParamType.JSONOBJECT, 0, $.t("nmfdih.dashboardintegrationhandler.output.config")));
        patterns.add(thead);
        patterns.add(new PatternVo("tbodyList", "output", ApiParamType.JSONARRAY, 1, $.t("nmfdih.dashboardintegrationhandler.output.tbodylist")));
        patterns.add(new PatternVo("currentPage", "output", ApiParamType.INTEGER, 1, $.t("common.currentpage")));
        patterns.add(new PatternVo("pageSize", "output", ApiParamType.INTEGER, 1, $.t("common.pagesize")));
        patterns.add(new PatternVo("pageCount", "output", ApiParamType.INTEGER, 1, $.t("common.pagecount")));
        patterns.add(new PatternVo("rowNum", "output", ApiParamType.INTEGER, 1, $.t("nmfdih.dashboardintegrationhandler.output.rownum")));
        return patterns;
    }

    /** 执行或转换错误不能伪装成空数据；成功输出逐项校验，不自动转换字符串和小数。 */
    @Override
    public void validate(IntegrationResultVo resultVo) {
        if (resultVo == null) {
            throw new IntegrationRequestResultIsEmptyException();
        }
        if (StringUtils.isNotBlank(resultVo.getError())) {
            throw new DashboardIntegrationRequestFailedException(resultVo.getError());
        }
        if (resultVo.getStatusCode() > 0 && (resultVo.getStatusCode() < 200 || resultVo.getStatusCode() >= 300)) {
            throw new DashboardIntegrationRequestFailedException("HTTP " + resultVo.getStatusCode());
        }
        if (StringUtils.isBlank(resultVo.getTransformedResult())) {
            throw new IntegrationRequestResultIsEmptyException();
        }
        Object parsed;
        try {
            parsed = JSON.parse(resultVo.getTransformedResult());
        } catch (Exception exception) {
            throw new IntegrationRequestResultFormatInvalidException();
        }
        if (!(parsed instanceof JSONObject)) {
            throw new IntegrationRequestResultFormatInvalidException();
        }
        JSONObject result = (JSONObject) parsed;
        if (!(result.get("tbodyList") instanceof JSONArray)) {
            throw new DashboardIntegrationResultInvalidException("tbodyList");
        }
        JSONArray rows = result.getJSONArray("tbodyList");
        for (int i = 0; i < rows.size(); i++) {
            if (!(rows.get(i) instanceof JSONObject)) {
                throw new DashboardIntegrationResultInvalidException("tbodyList[" + i + "]");
            }
        }
        for (String name : new String[]{"currentPage", "pageSize", "pageCount", "rowNum"}) {
            Object value = result.get(name);
            if (!(value instanceof Byte || value instanceof Short || value instanceof Integer || value instanceof Long || value instanceof BigInteger)) {
                throw new DashboardIntegrationResultInvalidException(name);
            }
        }
    }

    /** 无额外发送动作，保持集成基类的认证、转换与审计顺序。 */
    @Override
    protected void beforeSend(IntegrationVo integrationVo) {

    }

    /** 无额外返回动作，取数适配器在 sendRequest 后显式调用规范校验。 */
    @Override
    protected void afterReturn(IntegrationVo integrationVo) {

    }
}
