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

package neatlogic.framework.restful.core.privateapi.raw;

import neatlogic.framework.common.RootComponent;
import neatlogic.framework.restful.core.privateapi.PrivateApiTypeRegistrarBase;
import neatlogic.framework.restful.enums.ApiType;

/**
 * raw private API 注册器。
 */
@RootComponent
public class PrivateRawApiRegistrar extends PrivateApiTypeRegistrarBase<IPrivateRawApiComponent> {

    @Override
    public int getOrder() {
        return 40;
    }

    @Override
    protected Class<IPrivateRawApiComponent> getComponentClass() {
        return IPrivateRawApiComponent.class;
    }

    @Override
    protected ApiType getApiType() {
        return ApiType.RAW;
    }

    @Override
    protected String getClassName(IPrivateRawApiComponent component) {
        return component.getClassName();
    }

    @Override
    protected String getName(IPrivateRawApiComponent component) {
        return component.getName();
    }

    @Override
    protected String getConfig(IPrivateRawApiComponent component) {
        return component.getConfig();
    }

    @Override
    protected String getToken(IPrivateRawApiComponent component) {
        return component.getToken();
    }

    @Override
    protected String getDescription(IPrivateRawApiComponent component) {
        return component.getDescription();
    }

    @Override
    protected Integer getNeedAudit(IPrivateRawApiComponent component) {
        return component.needAudit();
    }

    @Override
    protected boolean isBasicSupport(IPrivateRawApiComponent component) {
        return component.isBasicSupport();
    }

    @Override
    protected boolean isSupportAnonymousAccess(IPrivateRawApiComponent component) {
        return component.supportAnonymousAccess().isSupportAnonymousAccess();
    }
}
