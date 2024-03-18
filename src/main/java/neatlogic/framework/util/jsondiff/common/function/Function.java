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

package neatlogic.framework.util.jsondiff.common.function;

import java.util.HashSet;

@FunctionalInterface
public interface Function {

    /**
     * 根据当前的路径。判断是否需要进行指定key联系对象
     *
     * @param exPath 期望对象的路径
     * @param acPath 真实对象的路径
     * @return 返回一个key的集合
     */
    HashSet<String> apply(String exPath, String acPath);
}

