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

public class EnumApiParam extends ApiParamValidatorBase {

    @Override
    public String getName() {
        return $.t("common.enum");
    }

    @Override
    public boolean validate(Object param, String rule) {
        if (StringUtils.isNotBlank(rule)) {
            if (rule.contains(",")) {
                for (String r : rule.split(",")) {
                    if (param.toString().equalsIgnoreCase(r)) {
                        return true;
                    }
                }
            } else {
                return param.toString().equalsIgnoreCase(rule);
            }
        }
        return false;
    }

    @Override
    public ApiParamType getType() {
        return ApiParamType.ENUM;
    }

}
