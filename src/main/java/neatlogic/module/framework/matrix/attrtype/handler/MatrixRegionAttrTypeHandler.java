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

package neatlogic.module.framework.matrix.attrtype.handler;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.dao.mapper.region.RegionMapper;
import neatlogic.framework.dto.region.RegionVo;
import neatlogic.framework.matrix.constvalue.MatrixAttributeType;
import neatlogic.framework.matrix.core.MatrixAttrTypeBase;
import neatlogic.framework.matrix.dto.MatrixAttributeVo;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MatrixRegionAttrTypeHandler extends MatrixAttrTypeBase {
    @Resource
    RegionMapper regionMapper;

    @Override
    public String getHandler() {
        return MatrixAttributeType.REGION.getValue();
    }

    @Override
    public void getTextByValue(MatrixAttributeVo matrixAttribute, Object valueObj, JSONObject resultObj) {
        Long value = Long.valueOf(valueObj.toString());
        RegionVo regionVo = regionMapper.getRegionById(value);
        if (regionVo != null) {
            resultObj.put("text", regionVo.getUpwardNamePath());
        }
    }

    @Override
    public String getValueWhenExport(Object value, MatrixAttributeVo attributeVo) {
        RegionVo regionVo = regionMapper.getRegionById(Long.parseLong(value.toString()) );
        if (regionVo != null) {
            return regionVo.getUpwardNamePath();
        } else {
            return value.toString();
        }
    }

    @Override
    public Set<String> getRealValueBatch(MatrixAttributeVo matrixAttributeVo, Map<String, String> valueMap) {
        List<String> needSearchValue = new ArrayList<>(valueMap.keySet());
        Set<String> repeatValueSet = new HashSet<>();
        List<Long> regionIdList = new ArrayList<>();
        for (Map.Entry<String, String> entry : valueMap.entrySet()) {
            String key = entry.getKey();
            try {
                regionIdList.add(Long.parseLong(key));
            } catch (Exception ignored) {
            }
        }
        if (CollectionUtils.isNotEmpty(regionIdList)) {
            List<RegionVo> regionVos = regionMapper.getRegionListByIdList(regionIdList);
            if (CollectionUtils.isNotEmpty(regionVos)) {
                for (RegionVo regionVo : regionVos) {
                    valueMap.put(regionVo.getId().toString(), regionVo.getId().toString());
                    needSearchValue.remove(regionVo.getId().toString());
                }
            }
        }

        if (CollectionUtils.isEmpty(needSearchValue)) {
            return repeatValueSet;
        }
        //通过name查找
        List<RegionVo> regionVos = regionMapper.getRegionByNameList(new ArrayList<>(needSearchValue));
        if (CollectionUtils.isNotEmpty(regionVos)) {
            for (Map.Entry<String, String> entry : valueMap.entrySet()) {
                String key = entry.getKey();
                List<RegionVo> tmp = regionVos.stream().filter(o -> Objects.equals(o.getName(), key)).collect(Collectors.toList());
                if (!tmp.isEmpty()) {
                    if (tmp.size() == 1) {
                        valueMap.put(key, tmp.get(0).getId().toString());
                    } else {
                        repeatValueSet.add(key);
                    }
                    needSearchValue.remove(key);
                }
            }
        }
        if (CollectionUtils.isEmpty(needSearchValue)) {
            return repeatValueSet;
        }
        //通过upwardNamePath查找
        regionVos = regionMapper.getRegionByUpwardNamePathList(needSearchValue);
        if (CollectionUtils.isNotEmpty(regionVos)) {
            for (RegionVo regionVo : regionVos) {
                valueMap.put(regionVo.getUpwardNamePath(), regionVo.getId().toString());
            }
        }

        return repeatValueSet;
    }
}
