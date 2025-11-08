/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
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
            returnObj.put("wordList", vo.getWordList());
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
        returnObj.put("wordList", vo.getWordList());
        returnObj.put("theadList", theadList);
        returnObj.put("tbodyList", tbodyList);
        return returnObj;
    }

}
