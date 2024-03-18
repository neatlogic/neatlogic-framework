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

package neatlogic.framework.exception.type;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class LicenseExpiredException extends ApiRuntimeException {
    public LicenseExpiredException(String moduleId) {
        super("当前许可缺少访问模块 {0} 的授权，尝试访问会被拒绝。原因是许可过期，请联系厂商更换，或者避免访问该模块下的任何功能。", moduleId);
    }

}
