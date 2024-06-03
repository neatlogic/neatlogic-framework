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

package neatlogic.module.framework.dynamicplugin.startup;

import neatlogic.framework.common.dto.BasePageVo;
import neatlogic.framework.dynamicplugin.DynamicPluginManager;
import neatlogic.framework.dynamicplugin.dto.DynamicPluginVo;
import neatlogic.framework.exception.file.FileNotFoundException;
import neatlogic.framework.file.dao.mapper.FileMapper;
import neatlogic.framework.file.dto.FileVo;
import neatlogic.framework.startup.StartupBase;
import neatlogic.module.framework.dao.mapper.dynamicplugin.DynamicPluginMapper;
import neatlogic.module.framework.dynamicplugin.service.DynamicPluginService;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class LoadDynamicPluginStartupHandler extends StartupBase {

    private Logger logger = LoggerFactory.getLogger(LoadDynamicPluginStartupHandler.class);
    @Resource
    private DynamicPluginService dynamicPluginService;

    @Resource
    private DynamicPluginMapper dynamicPluginMapper;

    @Resource
    private FileMapper fileMapper;

    @Override
    public String getName() {
        return "";
    }

    @Override
    public int executeForAllTenant() {
        List<DynamicPluginVo> allDynamicPluginList = new ArrayList<>();
        int currentPage = 1;
        int pageSize = 100;
        BasePageVo search = new BasePageVo();
        search.setPageSize(pageSize);
        do {
            search.setCurrentPage(currentPage);
            List<DynamicPluginVo> dynamicPluginList = dynamicPluginService.searchDynamicPluginList(search);
            allDynamicPluginList.addAll(dynamicPluginList);
            currentPage++;
        } while(currentPage > search.getPageCount());

        Map<Long, FileVo> fileMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(allDynamicPluginList)) {
            List<Long> fileIdList = allDynamicPluginList.stream().map(DynamicPluginVo::getFileId).collect(Collectors.toList());
            List<FileVo> fileList = fileMapper.getFileListByIdList(fileIdList);
            fileList.stream().collect(Collectors.toMap(e -> e.getId(), e -> e));
        }
        List<DynamicPluginVo> needUpdateDynamicPluginList = new ArrayList<>();
        for (DynamicPluginVo dynamicPluginVo : allDynamicPluginList) {
            try {
                FileVo fileVo = fileMap.get(dynamicPluginVo.getFileId());
                if (fileVo == null) {
                    throw new FileNotFoundException(dynamicPluginVo.getFileId());
                }
                Class<?> aClass = DynamicPluginManager.getClassByFileId(fileVo.getId());
                if (aClass == null) {
                    DynamicPluginManager.loadPlugin(fileVo);
                }
            } catch (Exception e) {
                dynamicPluginVo.setIsActive(0);
                dynamicPluginVo.setError(e.getMessage());
                needUpdateDynamicPluginList.add(dynamicPluginVo);
                logger.error(e.getMessage(), e);
            }
        }
        if (CollectionUtils.isNotEmpty(needUpdateDynamicPluginList)) {
            for (DynamicPluginVo dynamicPluginVo : needUpdateDynamicPluginList) {
                dynamicPluginMapper.insertDynamicPlugin(dynamicPluginVo);
            }
        }
        return 1;
    }

    @Override
    public int sort() {
        return 12;
    }
}
