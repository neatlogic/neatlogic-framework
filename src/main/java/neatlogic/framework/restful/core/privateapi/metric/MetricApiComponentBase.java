/*
 * Copyright (C) 2026  深圳极向量科技有限公司 All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package neatlogic.framework.restful.core.privateapi.metric;

import neatlogic.framework.restful.core.privateapi.binarystream.BinaryStreamApiComponentBase;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 指标接口基类。
 * 当前复用二进制流接口模板，后续若有专属协议能力可在此集中扩展。
 */
public abstract class MetricApiComponentBase extends BinaryStreamApiComponentBase implements MyMetricApiComponent {
    protected static final String OPEN_METRICS_CONTENT_TYPE = "application/openmetrics-text; version=1.0.0; charset=utf-8";

    /**
     * 初始化 OpenMetrics 响应头并返回文本输出流。
     */
    protected PrintWriter createOpenMetricsWriter(HttpServletResponse response) throws IOException {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(OPEN_METRICS_CONTENT_TYPE);
        return response.getWriter();
    }

    /**
     * 输出 MetricFamily 的 HELP 元数据行。
     */
    protected void writeHelp(PrintWriter writer, String metricName, String help) {
        writer.append("# HELP ").append(metricName).append(' ').append(escapeHelp(help)).append('\n');
    }

    /**
     * 输出 MetricFamily 的 TYPE 元数据行。
     */
    protected void writeType(PrintWriter writer, String metricName, String type) {
        writer.append("# TYPE ").append(metricName).append(' ').append(type).append('\n');
    }

    /**
     * 一次性输出同一个 MetricFamily 的 HELP 和 TYPE 元数据。
     */
    protected void writeMetricFamily(PrintWriter writer, String metricName, String help, String type) {
        writeHelp(writer, metricName, help);
        writeType(writer, metricName, type);
    }

    /**
     * 以 gauge 类型输出 MetricFamily 元数据。
     * 适用于当前值语义的指标，值可以随时间上升或下降，例如库存、队列长度、最近一分钟告警数。
     */
    protected void writeGaugeFamily(PrintWriter writer, String metricName, String help) {
        writeMetricFamily(writer, metricName, help, "gauge");
    }

    /**
     * 以 counter 类型输出 MetricFamily 元数据。
     * 适用于累计计数语义的指标，值只增不减，通常只会在进程重启后归零，例如累计请求数、累计异常数。
     */
    protected void writeCounterFamily(PrintWriter writer, String metricName, String help) {
        writeMetricFamily(writer, metricName, help, "counter");
    }

    /**
     * 以 histogram 类型输出 MetricFamily 元数据。
     * 适用于需要统计分布的指标，通常配合多个 bucket、sum、count 一起输出，例如耗时分布、大小分布。
     */
    protected void writeHistogramFamily(PrintWriter writer, String metricName, String help) {
        writeMetricFamily(writer, metricName, help, "histogram");
    }

    /**
     * 以 summary 类型输出 MetricFamily 元数据。
     * 适用于需要输出分位数统计的指标，通常会同时包含 quantile、sum、count，例如延迟的 p95、p99。
     */
    protected void writeSummaryFamily(PrintWriter writer, String metricName, String help) {
        writeMetricFamily(writer, metricName, help, "summary");
    }

    /**
     * 以 unknown 类型输出 MetricFamily 元数据。
     * 适用于暂时无法明确归类或需要兼容历史语义的指标；若能确定为 gauge/counter/histogram/summary，应优先使用明确类型。
     */
    protected void writeUnknownFamily(PrintWriter writer, String metricName, String help) {
        writeMetricFamily(writer, metricName, help, "unknown");
    }

    /**
     * 输出 MetricFamily 的 UNIT 元数据行。
     */
    protected void writeUnit(PrintWriter writer, String metricName, String unit) {
        writer.append("# UNIT ").append(metricName).append(' ').append(unit == null ? "" : unit).append('\n');
    }

    /**
     * 输出一个不带标签的样本行。
     */
    protected void writeSample(PrintWriter writer, String metricName, Number value) {
        writeSample(writer, metricName, null, value);
    }

    /**
     * 输出一个带标签的样本行。
     */
    protected void writeSample(PrintWriter writer, String metricName, Map<String, String> labels, Number value) {
        writer.append(buildSampleLine(metricName, labels, value)).append('\n');
    }

    /**
     * 输出 OpenMetrics 结束标记。
     */
    protected void writeEof(PrintWriter writer) {
        writer.append("# EOF").append('\n');
    }

    /**
     * 将成对的 key/value 参数转换为标签集合，方便业务代码就地声明标签。
     */
    protected Map<String, String> labels(String... keyValuePairs) {
        Map<String, String> labelMap = new LinkedHashMap<>();
        if (keyValuePairs == null) {
            return labelMap;
        }
        for (int i = 0; i + 1 < keyValuePairs.length; i += 2) {
            labelMap.put(keyValuePairs[i], keyValuePairs[i + 1] == null ? "" : keyValuePairs[i + 1]);
        }
        return labelMap;
    }

    /**
     * 按 OpenMetrics 文本格式拼装一条样本行。
     */
    private String buildSampleLine(String metricName, Map<String, String> labels, Number value) {
        StringBuilder builder = new StringBuilder(metricName);
        if (labels != null && !labels.isEmpty()) {
            builder.append('{');
            boolean first = true;
            for (Map.Entry<String, String> entry : labels.entrySet()) {
                if (!first) {
                    builder.append(',');
                }
                builder.append(entry.getKey())
                        .append("=\"")
                        .append(escapeLabelValue(entry.getValue()))
                        .append('"');
                first = false;
            }
            builder.append('}');
        }
        builder.append(' ').append(value == null ? "0" : String.valueOf(value));
        return builder.toString();
    }

    /**
     * 转义 HELP 文本中需要特殊处理的字符。
     */
    private String escapeHelp(String value) {
        return value == null ? "" : value.replace("\\", "\\\\").replace("\n", "\\n");
    }

    /**
     * 转义标签值中需要特殊处理的字符。
     */
    private String escapeLabelValue(String value) {
        return value == null ? "" : value.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
    }
}
