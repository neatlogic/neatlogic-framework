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

package neatlogic.framework.dto.elasticsearch;

import neatlogic.framework.common.dto.BasePageVo;

import java.util.List;

public class IndexResultVo extends BasePageVo {
    private List<String> idList;
    private List<IndexResultHighlightVo> highlightList;

    public List<String> getIdList() {
        return idList;
    }

    public void setIdList(List<String> idList) {
        this.idList = idList;
    }

    public List<IndexResultHighlightVo> getHighlightList() {
        return highlightList;
    }

    public void setHighlightList(List<IndexResultHighlightVo> highlightList) {
        this.highlightList = highlightList;
    }
}
