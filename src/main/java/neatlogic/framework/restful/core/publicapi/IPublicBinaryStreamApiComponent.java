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

package neatlogic.framework.restful.core.publicapi;

import neatlogic.framework.restful.core.IBinaryStreamApiComponent;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Author
 * @Time Aug 26,2020
 * @Description: 外部接口
 */
@Deprecated
public interface IPublicBinaryStreamApiComponent extends IBinaryStreamApiComponent {
    /**
     * 接口唯一标识，也是访问URI
     *
     * @return token
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    String getToken();
}
