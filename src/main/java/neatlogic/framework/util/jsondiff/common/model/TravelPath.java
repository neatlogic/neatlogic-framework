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

import neatlogic.framework.util.jsondiff.common.utils.PathUtil;

public class TravelPath {

    /**
     * 期望对象遍历地址
     */
    private String expectTravelPath;

    /**
     * 实际对象遍历的地址
     */
    private String actualTravelPath;

    /**
     * 索引地址。一个抽象的地址
     */
    private String abstractTravelPath;


    public TravelPath(TravelPath parentPath, MappingKey mappingKey) {
        // 抽象的路径
        this.abstractTravelPath = PathUtil.getObjectPath(parentPath.getAbstractTravelPath()) + (mappingKey.getExpectKey() != null ? mappingKey.getExpectKey() : mappingKey.getActualKey());
        // 实际遍历的路径
        this.actualTravelPath = PathUtil.getObjectPath(parentPath.actualTravelPath) + mappingKey.getActualKey();
        this.expectTravelPath = PathUtil.getObjectPath(parentPath.getExpectTravelPath()) + mappingKey.getExpectKey();
    }

    public TravelPath(TravelPath parentPath, int expectIndex, int actualIndex) {
        // 抽象的路径
        this.abstractTravelPath = parentPath.getAbstractTravelPath() + PathUtil.getIndexPath("");
        // 实际遍历的路径
        this.actualTravelPath = parentPath.getActualTravelPath() + PathUtil.getIndexPath(String.valueOf(actualIndex));
        this.expectTravelPath = parentPath.getExpectTravelPath() + PathUtil.getIndexPath(String.valueOf(expectIndex));
    }

    public TravelPath(String abstractTravelPath) {
        this.abstractTravelPath = abstractTravelPath;
        this.actualTravelPath = abstractTravelPath;
        this.expectTravelPath = abstractTravelPath;
    }


    public TravelPath(TravelPath travel) {
        this.abstractTravelPath = travel.getAbstractTravelPath();
    }


    public String getExpectTravelPath() {
        return expectTravelPath;
    }

    public void setExpectTravelPath(String expectTravelPath) {
        this.expectTravelPath = expectTravelPath;
    }

    public String getActualTravelPath() {
        return actualTravelPath;
    }

    public void setActualTravelPath(String actualTravelPath) {
        this.actualTravelPath = actualTravelPath;
    }

    public String getAbstractTravelPath() {
        return abstractTravelPath;
    }

    public void setAbstractTravelPath(String abstractTravelPath) {
        this.abstractTravelPath = abstractTravelPath;
    }

}
