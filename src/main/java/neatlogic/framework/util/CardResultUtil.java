/*Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

package neatlogic.framework.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.common.dto.BasePageVo;

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
