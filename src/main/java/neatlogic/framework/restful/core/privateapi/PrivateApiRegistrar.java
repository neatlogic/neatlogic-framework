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

package neatlogic.framework.restful.core.privateapi;

import neatlogic.framework.common.RootComponent;
import neatlogic.framework.restful.enums.ApiType;

/**
 * 对象型 private API 注册器。
 */
@RootComponent
public class PrivateApiRegistrar extends PrivateApiTypeRegistrarBase<IPrivateApiComponent> {

    @Override
    public int getOrder() {
        return 10;
    }

    @Override
    protected Class<IPrivateApiComponent> getComponentClass() {
        return IPrivateApiComponent.class;
    }

    @Override
    protected ApiType getApiType() {
        return ApiType.OBJECT;
    }

    @Override
    protected String getClassName(IPrivateApiComponent component) {
        return component.getClassName();
    }

    @Override
    protected String getName(IPrivateApiComponent component) {
        return component.getName();
    }

    @Override
    protected String getConfig(IPrivateApiComponent component) {
        return component.getConfig();
    }

    @Override
    protected String getToken(IPrivateApiComponent component) {
        return component.getToken();
    }

    @Override
    protected String getDescription(IPrivateApiComponent component) {
        return component.getDescription();
    }

    @Override
    protected Integer getNeedAudit(IPrivateApiComponent component) {
        return component.needAudit();
    }

    @Override
    protected boolean isMcp(IPrivateApiComponent component) {
        return component.isMcp();
    }

    @Override
    protected boolean isBasicSupport(IPrivateApiComponent component) {
        return component.isBasicSupport();
    }

    @Override
    protected boolean isSupportAnonymousAccess(IPrivateApiComponent component) {
        return component.supportAnonymousAccess().isSupportAnonymousAccess();
    }
}
