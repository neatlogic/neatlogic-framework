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

package neatlogic.framework.restful.core.privateapi.metric;

import neatlogic.framework.common.RootComponent;
import neatlogic.framework.restful.core.privateapi.PrivateApiTypeRegistrarBase;
import neatlogic.framework.restful.enums.ApiType;

/**
 * 指标 private API 注册器。
 */
@RootComponent
public class PrivateMetricApiRegistrar extends PrivateApiTypeRegistrarBase<IPrivateMetricApiComponent> {

    @Override
    public int getOrder() {
        return 35;
    }

    @Override
    protected Class<IPrivateMetricApiComponent> getComponentClass() {
        return IPrivateMetricApiComponent.class;
    }

    @Override
    protected ApiType getApiType() {
        return ApiType.METRIC;
    }

    @Override
    protected String getClassName(IPrivateMetricApiComponent component) {
        return component.getClassName();
    }

    @Override
    protected String getName(IPrivateMetricApiComponent component) {
        return component.getName();
    }

    @Override
    protected String getConfig(IPrivateMetricApiComponent component) {
        return component.getConfig();
    }

    @Override
    protected String getToken(IPrivateMetricApiComponent component) {
        return component.getToken();
    }

    @Override
    protected String getDescription(IPrivateMetricApiComponent component) {
        return component.getDescription();
    }

    @Override
    protected Integer getNeedAudit(IPrivateMetricApiComponent component) {
        return component.needAudit();
    }

    @Override
    protected boolean isBasicSupport(IPrivateMetricApiComponent component) {
        return component.isBasicSupport();
    }

    @Override
    protected boolean isSupportAnonymousAccess(IPrivateMetricApiComponent component) {
        return component.supportAnonymousAccess().isSupportAnonymousAccess();
    }
}
