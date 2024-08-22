/*
 * Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package neatlogic.framework.form.exception;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class FormExtendAttributeConfigIllegalException extends ApiRuntimeException {

    public FormExtendAttributeConfigIllegalException(String handler, String key, String field) {
        super("表单扩展数据中，{0}类型属性{1}的配置中缺少{2}字段", handler, key, field);
    }

    public FormExtendAttributeConfigIllegalException(String handler, String key, String field, String value) {
        super("表单扩展数据中，{0}类型属性{1}的配置中{2}字段值为{3}不合法", handler, key, field, value);
    }
}
