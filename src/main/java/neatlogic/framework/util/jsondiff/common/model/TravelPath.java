/*
 * Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
