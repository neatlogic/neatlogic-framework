/*
 * Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.
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

package neatlogic.module.framework.dynamicplugin.service;

import neatlogic.framework.common.dto.BasePageVo;
import neatlogic.framework.dynamicplugin.crossover.IDynamicPluginCrossoverService;
import neatlogic.framework.dynamicplugin.dto.DynamicPluginVo;
import neatlogic.module.framework.dao.mapper.dynamicplugin.DynamicPluginMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class DynamicPluginServiceImpl implements DynamicPluginService, IDynamicPluginCrossoverService {

    @Resource
    private DynamicPluginMapper dynamicPluginMapper;

    @Override
    public List<DynamicPluginVo> searchDynamicPluginList(BasePageVo search) {
        List<DynamicPluginVo> tbodyList = dynamicPluginMapper.searchDynamicPluginList(search);
        if (Objects.equals(search.getRowNum(), 0) && (CollectionUtils.isNotEmpty(tbodyList) || search.getCurrentPage() > 1)) {
            int rowNum = dynamicPluginMapper.getDynamicPluginCount(search);
            if (rowNum == 0) {
                return new ArrayList<>();
            }
            search.setRowNum(rowNum);
        }
        return tbodyList;
    }
}
