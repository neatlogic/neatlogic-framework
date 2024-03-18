/*Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

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
