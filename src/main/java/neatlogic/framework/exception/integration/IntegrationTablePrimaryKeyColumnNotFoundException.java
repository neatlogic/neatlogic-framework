/*Copyright (C) 2023  深圳极向量科技有限公司 All Rights Reserved.

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

/**
 * @author linbq
 * @since 2021/10/22 18:25
 **/
public class IntegrationTablePrimaryKeyColumnNotFoundException extends ApiRuntimeException {

    private static final long serialVersionUID = 1061691151919475176L;

    public IntegrationTablePrimaryKeyColumnNotFoundException(String integration) {
        super("在{0}集成配置输出转换theadList列表中找不到主键列", integration);
    }
}
