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

package neatlogic.module.framework.dao.mapper.dynamicplugin;

import neatlogic.framework.common.dto.BasePageVo;
import neatlogic.framework.dynamicplugin.crossover.IDynamicPluginCrossoverMapper;
import neatlogic.framework.dynamicplugin.dto.DynamicPluginVo;

import java.util.List;

public interface DynamicPluginMapper extends IDynamicPluginCrossoverMapper {

    List<DynamicPluginVo> getDynamicPluginList();

    DynamicPluginVo getDynamicPluginById(Long id);

    DynamicPluginVo getDynamicPluginByKey(String key);

    List<DynamicPluginVo> getDynamicPluginListByIdList(List<Long> idList);

    int getDynamicPluginCount(BasePageVo search);

    List<DynamicPluginVo> searchDynamicPluginList(BasePageVo search);

    int insertDynamicPlugin(DynamicPluginVo dynamicPluginVo);

    int deleteDynamicPluginById(Long id);
}
