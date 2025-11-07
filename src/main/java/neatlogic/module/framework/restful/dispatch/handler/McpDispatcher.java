package neatlogic.module.framework.restful.dispatch.handler;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.restful.core.IApiComponent;
import neatlogic.framework.restful.core.privateapi.PrivateApiComponentFactory;
import neatlogic.framework.restful.dto.ApiVo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/mcp")
public class McpDispatcher {


    @PostMapping
    public void dispatch(@RequestBody JSONObject req, HttpServletResponse response) throws IOException {
        String id = req.getString("id");
        String method = req.getString("method");
        JSONObject result = new JSONObject();

        try {
            switch (method) {
                case "list_tools":
                    result.put("modules", listTools());
                    break;

                case "invoke_tool":
                    JSONObject params = req.getJSONObject("params");
                    String token = params.getString("name");
                    JSONObject arguments = params.getJSONObject("arguments");
                    result.put("output", invokeApi(token, arguments));
                    break;

                default:
                    throw new IllegalArgumentException("unknown method: " + method);
            }

            JSONObject resp = new JSONObject();
            resp.put("jsonrpc", "2.0");
            resp.put("result", result);
            resp.put("id", id);
            write(response, resp);

        } catch (Exception e) {
            JSONObject error = new JSONObject();
            error.put("code", -32000);
            error.put("message", e.getMessage());
            JSONObject resp = new JSONObject();
            resp.put("jsonrpc", "2.0");
            resp.put("error", error);
            resp.put("id", id);
            write(response, resp);
        }
    }

    private JSONArray listTools() {
        JSONArray moduleList = new JSONArray();
        Map<String, List<ApiVo>> moduleMap = new HashMap<>();
        for (ApiVo api : PrivateApiComponentFactory.getMcpApiList()) {
            if (!moduleMap.containsKey(api.getModuleGroup())) {
                moduleMap.put(api.getModuleGroup(), new ArrayList<>());
            }
            moduleMap.get(api.getModuleGroup()).add(api);
        }
        for (Map.Entry<String, List<ApiVo>> entry : moduleMap.entrySet()) {
            JSONObject moduleObj = new JSONObject();
            moduleObj.put("module", entry.getKey());
            moduleObj.put("tools", new JSONArray());
            for (ApiVo api : entry.getValue()) {
                JSONObject apiObj = new JSONObject();
                apiObj.put("name", api.getToken());
                apiObj.put("description", api.getDescription());
                moduleObj.getJSONArray("tools").add(apiObj);
            }
            moduleList.add(moduleObj);
        }
        return moduleList;
    }

    private Object invokeApi(String token, JSONObject arguments) throws Exception {
        ApiVo apiVo = PrivateApiComponentFactory.getApiByToken(token);
        if (apiVo == null) {
            throw new IllegalArgumentException("api not found: " + token);
        }
        IApiComponent comp = PrivateApiComponentFactory.getInstance(apiVo.getHandler());
        return comp.doService(apiVo, arguments, null);
    }

    private void write(HttpServletResponse response, JSONObject json) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().print(json.toJSONString());
    }
}