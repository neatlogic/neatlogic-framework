/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.util;

import codedriver.framework.common.dto.BasePageVo;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

public class TableResultUtil {
    public static JSONObject getResult(List<? extends BasePageVo> resultList, BasePageVo vo) {
        JSONObject returnObj = new JSONObject();
        returnObj.put("pageSize", vo.getPageSize());
        returnObj.put("pageCount", vo.getPageCount());
        returnObj.put("rowNum", vo.getRowNum());
        returnObj.put("currentPage", vo.getCurrentPage());
        returnObj.put("tbodyList", resultList);
        return returnObj;
    }

    public static JSONObject getResult(JSONArray resultList, BasePageVo vo) {
        JSONObject returnObj = new JSONObject();
        returnObj.put("pageSize", vo.getPageSize());
        returnObj.put("pageCount", vo.getPageCount());
        returnObj.put("rowNum", vo.getRowNum());
        returnObj.put("currentPage", vo.getCurrentPage());
        returnObj.put("tbodyList", resultList);
        return returnObj;
    }

}
