/*Copyright (C) $today.year  深圳极向量科技有限公司 All Rights Reserved.

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

package neatlogic.framework.exception.integration;

import neatlogic.framework.exception.core.ApiRuntimeException;

import java.util.List;

/**
 * @author linbq
 * @since 2021/11/23 12:12
 **/
public class IntegrationRequestResultFieldNotExistsException extends ApiRuntimeException {

    private static final long serialVersionUID = 1161502312345475176L;

    public IntegrationRequestResultFieldNotExistsException(String field) {
        super("集成请求结果中：“{0}”字段不存在", field);
    }

    public IntegrationRequestResultFieldNotExistsException(List<String> fieldList) {
        super("集成请求结果中：'" + String.join("、", fieldList) + "'字段不存在", String.join("、", fieldList));
    }
}
