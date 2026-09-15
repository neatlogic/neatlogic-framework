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

package neatlogic.framework.auth.label;

import neatlogic.framework.auth.core.AuthBase;

/** 权限名称与说明使用国际化键，权限标识及校验规则保持不变。 */
public class WORKTIME_MODIFY extends AuthBase {

	@Override
	public String getAuthDisplayName() {
		return "auth.worktime_modify.name";
	}

	@Override
	public String getAuthIntroduction() {
		return "auth.worktime_modify.description";
	}

	@Override
	public String getAuthGroup() {
		return "framework";
	}

	@Override
	public Integer getSort() {
		return 7;
	}

}
