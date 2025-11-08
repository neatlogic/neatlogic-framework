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
import neatlogic.framework.dao.mapper.UserMapper;
import neatlogic.framework.dto.UserVo;
import neatlogic.framework.matrix.constvalue.MatrixAttributeType;
import neatlogic.framework.matrix.core.MatrixAttrTypeBase;
import neatlogic.framework.matrix.dto.MatrixAttributeVo;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MatrixUserAttrTypeHandler extends MatrixAttrTypeBase {
    @Resource
    UserMapper userMapper;

    @Override
    public String getHandler() {
        return MatrixAttributeType.USER.getValue();
    }

    @Override
    public void getTextByValue(MatrixAttributeVo matrixAttribute, Object valueObj, JSONObject resultObj) {
        String value = valueObj.toString();
        UserVo userVo = userMapper.getUserBaseInfoByUuid(value);
        if (userVo != null) {
            resultObj.put("text", userVo.getUserName());
            resultObj.put("avatar", userVo.getAvatar());
            resultObj.put("pinyin", userVo.getPinyin());
            resultObj.put("vipLevel", userVo.getVipLevel());
        }
    }

    @Override
    public String getValueWhenExport(Object value, MatrixAttributeVo attributeVo) {
        UserVo user = userMapper.getUserBaseInfoByUuid(value.toString());
        if (user != null) {
            return user.getUserName();
        } else {
            return value.toString();
        }
    }

    @Override
    public Set<String> getRealValueBatch(MatrixAttributeVo matrixAttributeVo, Map<String, String> valueMap) {
        List<String> needSearchValue = new ArrayList<>(valueMap.keySet());
        //通过uuid搜
        if (CollectionUtils.isNotEmpty(needSearchValue)) {
            List<UserVo> userVos = userMapper.getUserByUserUuidList(needSearchValue);
            if (CollectionUtils.isNotEmpty(userVos)) {
                List<String> userUuidList = userVos.stream().map(UserVo::getUuid).collect(Collectors.toList());
                for (Map.Entry<String, String> entry : valueMap.entrySet()) {
                    if (userUuidList.contains(entry.getKey())) {
                        valueMap.put(entry.getKey(), entry.getKey());
                        needSearchValue.remove(entry.getKey());
                    }
                }
            }
        }
        if(CollectionUtils.isEmpty(needSearchValue)){
            return Collections.emptySet();
        }
        //通过userId搜
        if (CollectionUtils.isNotEmpty(needSearchValue)) {
            List<UserVo> userVos = userMapper.getUserByUserIdList(needSearchValue);
            if (CollectionUtils.isNotEmpty(userVos)) {
                Map<String, String> userIdUuidMap = userVos.stream().collect(Collectors.toMap(UserVo::getUserId, UserVo::getUuid));
                for (Map.Entry<String, String> entry : valueMap.entrySet()) {
                    if (userIdUuidMap.containsKey(entry.getKey())) {
                        valueMap.put(entry.getKey(), userIdUuidMap.get(entry.getKey()));
                        needSearchValue.remove(entry.getKey());
                    }
                }
            }
        }
        //通过userName搜
        if (CollectionUtils.isNotEmpty(needSearchValue)) {
            List<UserVo> userVos = userMapper.getUserByUserNameList(needSearchValue);
            if (CollectionUtils.isNotEmpty(userVos)) {
                Map<String, String> userName2UuidMap = new HashMap<>();
                for (UserVo userVo : userVos) {
                    if (!userName2UuidMap.containsKey(userVo.getUserName())) {
                        userName2UuidMap.put(userVo.getUserName(), userVo.getUuid());
                    }
                }
                for (Map.Entry<String, String> entry : valueMap.entrySet()) {
                    if (userName2UuidMap.containsKey(entry.getKey())) {
                        valueMap.put(entry.getKey(), userName2UuidMap.get(entry.getKey()));
                        needSearchValue.remove(entry.getKey());
                    }
                }
            }
        }
        return Collections.emptySet();
    }
}
