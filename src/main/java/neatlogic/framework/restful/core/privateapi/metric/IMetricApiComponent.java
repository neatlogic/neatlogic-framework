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

import neatlogic.framework.restful.core.privateapi.binarystream.IBinaryStreamApiComponent;

/**
 * 指标接口组件。
 * 复用二进制流接口的请求/响应签名，专门承载 OpenMetrics/Prometheus 文本输出。
 */
public interface IMetricApiComponent extends IBinaryStreamApiComponent {
}
