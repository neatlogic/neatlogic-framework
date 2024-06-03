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

package neatlogic.framework.dynamicplugin;

import neatlogic.framework.crossover.CrossoverServiceFactory;
import neatlogic.framework.dynamicplugin.crossover.IDynamicPluginCrossoverMapper;
import neatlogic.framework.dynamicplugin.dto.DynamicPluginVo;
import neatlogic.framework.exception.file.FileNotFoundException;
import neatlogic.framework.file.dao.mapper.FileMapper;
import neatlogic.framework.file.dto.FileVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Service
public class DynamicPluginManager {

    private static FileMapper fileMapper;

    @Resource
    public void setFileMapper(FileMapper _fileMapper) {
        fileMapper = _fileMapper;
    }

    private final static Map<Long, Class<?>> fileId2ClassMap = new HashMap<>();

    public static Class<?> getClassByDynamicPluginId(Long dynamicPluginId) throws Exception {
        IDynamicPluginCrossoverMapper dynamicPluginCrossoverMapper = CrossoverServiceFactory.getApi(IDynamicPluginCrossoverMapper.class);
        DynamicPluginVo dynamicPlugin = dynamicPluginCrossoverMapper.getDynamicPluginById(dynamicPluginId);
        Class<?> aClass = getClassByFileId(dynamicPlugin.getFileId());
        return aClass;
    }

    public static Class<?> getClassByDynamicPluginKey(Long dynamicPluginKey) throws Exception {
        IDynamicPluginCrossoverMapper dynamicPluginCrossoverMapper = CrossoverServiceFactory.getApi(IDynamicPluginCrossoverMapper.class);
        DynamicPluginVo dynamicPlugin = dynamicPluginCrossoverMapper.getDynamicPluginById(dynamicPluginKey);
        Class<?> aClass = getClassByFileId(dynamicPlugin.getFileId());
        return aClass;
    }

    public static Class<?> getClassByFileId(Long fileId) throws Exception {
        Class<?> aClass = fileId2ClassMap.get(fileId);
        if (aClass == null) {
            FileVo fileVo = fileMapper.getFileById(fileId);
            if (fileVo == null) {
                throw new FileNotFoundException(fileId);
            }
            return loadPlugin(fileVo);
        }
        return aClass;
    }

    public static Class<?> loadPlugin(FileVo fileVo) throws ClassNotFoundException {
        PluginClassLoader pluginClassLoader = new PluginClassLoader(DynamicPluginManager.class.getClassLoader());
        Class<?> aClass = pluginClassLoader.loadClass(fileVo.getPath());
        if (aClass != null) {
            fileId2ClassMap.put(fileVo.getId(), aClass);
        }
        return aClass;
    }

    public static boolean removePlugin(Long fileId) {
        return fileId2ClassMap.remove(fileId) != null;
    }
}
