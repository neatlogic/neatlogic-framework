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

package neatlogic.framework.util.jsondiff.common.model;

public class MappingKey {

    /**
     * 期望的key
     */
    private final String expectKey;

    /**
     * 实际key
     */
    private final String actualKey;

    public MappingKey(String expectKey, String actualKey) {
        this.expectKey = expectKey;
        this.actualKey = actualKey;
    }

    public String getExpectKey() {
        return expectKey;
    }

    public String getActualKey() {
        return actualKey;
    }

}
