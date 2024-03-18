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

package neatlogic.framework.apiparam.validator;

import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.param.validate.core.ApiParamValidatorBase;
import neatlogic.framework.util.$;
import org.apache.commons.lang3.StringUtils;

public class BooleanApiParam extends ApiParamValidatorBase {

    @Override
    public String getName() {
        return $.t("common.boolean");
    }

    @Override
    public boolean validate(Object param, String rule) {
        if (param != null && StringUtils.isNotBlank(param.toString())) {
            return Boolean.TRUE.toString().equalsIgnoreCase(param.toString()) || Boolean.FALSE.toString().equalsIgnoreCase(param.toString());
        }
        return true;
    }

    @Override
    public ApiParamType getType() {
        return ApiParamType.BOOLEAN;
    }

}
