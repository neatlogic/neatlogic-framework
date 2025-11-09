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

package neatlogic.framework.form.attribute.core;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;

public abstract class ControlHandlerBase implements IFormAttributeHandler {
    @Override
    public final String getType() {
        return "control";
    }

    @Override
    public List<String> indexFieldContentList(String data) {
        List<String> contentList = myIndexFieldContentList(data);
        if (CollectionUtils.isEmpty(contentList)) {
            contentList = Collections.singletonList(data);
        }
        return contentList;
    }

    protected List<String> myIndexFieldContentList(String data) {
        return null;
    }

    @Override
    public Boolean isNeedSliceWord() {
        return true;
    }

    @Override
    public Boolean isUseFormConfig() {
        return true;
    }

    @Override
    public void validateExtendAttributeConfig(String key, JSONObject config) {
    }
}
