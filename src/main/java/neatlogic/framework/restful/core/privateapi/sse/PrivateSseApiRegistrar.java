/*
 * Copyright (C) 2026  深圳极向量科技有限公司 All Rights Reserved.
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

package neatlogic.framework.restful.core.privateapi.sse;

import neatlogic.framework.common.RootComponent;
import neatlogic.framework.restful.core.privateapi.PrivateApiTypeRegistrarBase;
import neatlogic.framework.restful.enums.ApiType;

@RootComponent
public class PrivateSseApiRegistrar extends PrivateApiTypeRegistrarBase<IPrivateSseApiComponent> {

    @Override
    public int getOrder() {
        return 25;
    }

    @Override
    protected Class<IPrivateSseApiComponent> getComponentClass() {
        return IPrivateSseApiComponent.class;
    }

    @Override
    protected ApiType getApiType() {
        return ApiType.SSE;
    }

    @Override
    protected String getClassName(IPrivateSseApiComponent component) {
        return component.getClassName();
    }

    @Override
    protected String getName(IPrivateSseApiComponent component) {
        return component.getName();
    }

    @Override
    protected String getConfig(IPrivateSseApiComponent component) {
        return component.getConfig();
    }

    @Override
    protected String getToken(IPrivateSseApiComponent component) {
        return component.getToken();
    }

    @Override
    protected String getDescription(IPrivateSseApiComponent component) {
        return component.getDescription();
    }

    @Override
    protected Integer getNeedAudit(IPrivateSseApiComponent component) {
        return component.needAudit();
    }

    @Override
    protected boolean isBasicSupport(IPrivateSseApiComponent component) {
        return component.isBasicSupport();
    }

    @Override
    protected boolean isSupportAnonymousAccess(IPrivateSseApiComponent component) {
        return component.supportAnonymousAccess().isSupportAnonymousAccess();
    }
}
