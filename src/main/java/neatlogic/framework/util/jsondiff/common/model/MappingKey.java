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
