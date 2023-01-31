/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.framework.util;

import neatlogic.framework.common.dto.BasePageVo;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

public class CardResultUtil {
    public static JSONObject getResult(List<? extends BasePageVo> resultList, BasePageVo vo) {
        JSONObject returnObj = new JSONObject();
        returnObj.put("pageSize", vo.getPageSize());
        returnObj.put("pageCount", vo.getPageCount());
        returnObj.put("rowNum", vo.getRowNum());
        returnObj.put("currentPage", vo.getCurrentPage());
        returnObj.put("cardList", resultList);
        return returnObj;
    }

    public static JSONObject getResult(JSONArray resultList, BasePageVo vo) {
        JSONObject returnObj = new JSONObject();
        returnObj.put("pageSize", vo.getPageSize());
        returnObj.put("pageCount", vo.getPageCount());
        returnObj.put("rowNum", vo.getRowNum());
        returnObj.put("currentPage", vo.getCurrentPage());
        returnObj.put("cardList", resultList);
        return returnObj;
    }

}
