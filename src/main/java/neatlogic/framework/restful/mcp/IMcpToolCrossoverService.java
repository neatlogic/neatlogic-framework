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

package neatlogic.framework.restful.mcp;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.util.List;
import neatlogic.framework.crossover.ICrossoverService;

/** MCP 工具公共契约，身份来自当前线程上下文，调用方不能传入替代身份。 */
public interface IMcpToolCrossoverService extends ICrossoverService {
    /** 按当前租户的模块、scope 和 MCP 配置发现工具。 */
    JSONArray listTools(String scope) throws CloneNotSupportedException;

    /** 返回当前有效目录的检索资料，不包含示例请求值或完整 Schema。 */
    JSONArray listToolSearchMetadata(String scope) throws CloneNotSupportedException;

    /** 按当前线程语言返回全部已注册对象接口的静态资料，不读取租户配置或执行示例。 */
    JSONArray listRegisteredToolSearchMetadata();

    /** 仅返回当前租户、模块、scope 和启停规则允许的工具名，不生成帮助定义。 */
    List<String> listAvailableToolNames(String scope) throws CloneNotSupportedException;

    /** 仅为指定名称生成完整定义；空名称列表返回空结果，不扩大为全目录。 */
    JSONArray listTools(String scope, List<String> names) throws CloneNotSupportedException;

    /** 执行已启用的 MCP API，返回标准 content、structuredContent 和 isError。 */
    JSONObject callTool(String scope, String name, JSONObject arguments);
}
