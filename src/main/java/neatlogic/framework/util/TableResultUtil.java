/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.framework.util;

import neatlogic.framework.common.dto.BasePageVo;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

public class TableResultUtil {
    public static JSONObject getResult(List resultList, BasePageVo vo) {
        JSONObject returnObj = new JSONObject();
        returnObj.put("pageSize", vo.getPageSize());
        returnObj.put("pageCount", vo.getPageCount());
        returnObj.put("rowNum", vo.getRowNum());
        returnObj.put("currentPage", vo.getCurrentPage());
        returnObj.put("tbodyList", resultList);
        return returnObj;
    }

    public static JSONObject getResult(List theadList, List tbodyList, BasePageVo vo) {
        JSONObject returnObj = new JSONObject();
        returnObj.put("pageSize", vo.getPageSize());
        returnObj.put("pageCount", vo.getPageCount());
        returnObj.put("rowNum", vo.getRowNum());
        returnObj.put("currentPage", vo.getCurrentPage());
        returnObj.put("theadList", theadList);
        returnObj.put("tbodyList", tbodyList);
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

    public static JSONObject getResult(List resultList) {
        JSONObject returnObj = new JSONObject();
        returnObj.put("tbodyList", resultList);
        return returnObj;
    }

    public static JSONObject getOffsetResult(List theadList, List tbodyList, BasePageVo vo) {
        JSONObject returnObj = new JSONObject();
        returnObj.put("pageSize", vo.getPageSize());
        returnObj.put("rowNum", vo.getRowNum());
        returnObj.put("startPage", vo.getStartPage());
        returnObj.put("endPage", vo.getEndPage());
        returnObj.put("currentPage", vo.getCurrentPage());
        returnObj.put("theadList", theadList);
        returnObj.put("tbodyList", tbodyList);
        return returnObj;
    }

}
