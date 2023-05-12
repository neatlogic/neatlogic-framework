/*
 * Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package neatlogic.framework.util;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.common.dto.BasePageVo;

import java.util.List;

public class TableResultUtil {
    public static JSONObject getResult(List resultList, BasePageVo vo) {
        return getResult(null, resultList, vo);
    }

    public static JSONObject getResult(List theadList, List tbodyList) {
        return getResult(theadList, tbodyList, null);
    }

    public static JSONObject getResult(List theadList, List tbodyList, BasePageVo vo) {
        JSONObject returnObj = new JSONObject();
        if (vo != null) {
            returnObj.put("pageSize", vo.getPageSize());
            returnObj.put("pageCount", vo.getPageCount());
            returnObj.put("rowNum", vo.getRowNum());
            returnObj.put("currentPage", vo.getCurrentPage());
        }
        returnObj.put("theadList", theadList);
        returnObj.put("tbodyList", tbodyList);
        return returnObj;
    }

    public static JSONObject getResult(List resultList) {
        return getResult(null, resultList, null);
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
