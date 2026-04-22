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

package neatlogic.framework.restful.core.privateapi.jsonstream;

import neatlogic.framework.common.RootComponent;
import neatlogic.framework.restful.core.privateapi.PrivateApiTypeRegistrarBase;
import neatlogic.framework.restful.enums.ApiType;

/**
 * JSON 流 private API 注册器。
 */
@RootComponent
public class PrivateJsonStreamApiRegistrar extends PrivateApiTypeRegistrarBase<IPrivateJsonStreamApiComponent> {

    @Override
    public int getOrder() {
        return 20;
    }

    @Override
    protected Class<IPrivateJsonStreamApiComponent> getComponentClass() {
        return IPrivateJsonStreamApiComponent.class;
    }

    @Override
    protected ApiType getApiType() {
        return ApiType.STREAM;
    }

    @Override
    protected String getClassName(IPrivateJsonStreamApiComponent component) {
        return component.getClassName();
    }

    @Override
    protected String getName(IPrivateJsonStreamApiComponent component) {
        return component.getName();
    }

    @Override
    protected String getConfig(IPrivateJsonStreamApiComponent component) {
        return component.getConfig();
    }

    @Override
    protected String getToken(IPrivateJsonStreamApiComponent component) {
        return component.getToken();
    }

    @Override
    protected String getDescription(IPrivateJsonStreamApiComponent component) {
        return component.getDescription();
    }

    @Override
    protected Integer getNeedAudit(IPrivateJsonStreamApiComponent component) {
        return component.needAudit();
    }

    @Override
    protected boolean isBasicSupport(IPrivateJsonStreamApiComponent component) {
        return component.isBasicSupport();
    }

    @Override
    protected boolean isSupportAnonymousAccess(IPrivateJsonStreamApiComponent component) {
        return component.supportAnonymousAccess().isSupportAnonymousAccess();
    }
}
