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

import neatlogic.framework.bootstrap.NeatLogicWebApplicationContext;
import neatlogic.framework.restful.enums.ApiType;
import org.apache.commons.lang3.StringUtils;

/**
 * private API 类型注册器抽象基类。
 * 收敛各类型接口在扫描、注解校验、处理器注册与 token 注册上的公共模板。
 */
public abstract class PrivateApiTypeRegistrarBase<T> implements IPrivateApiTypeRegistrar {

    /**
     * 返回当前注册器负责扫描的 Spring Bean 类型。
     */
    protected abstract Class<T> getComponentClass();

    /**
     * 返回当前注册器对应的接口类型。
     */
    protected abstract ApiType getApiType();

    /**
     * 获取组件类名。
     */
    protected abstract String getClassName(T component);

    /**
     * 获取组件名称。
     */
    protected abstract String getName(T component);

    /**
     * 获取组件配置。
     */
    protected abstract String getConfig(T component);

    /**
     * 获取组件 token。
     */
    protected abstract String getToken(T component);

    /**
     * 获取组件描述。
     */
    protected abstract String getDescription(T component);

    /**
     * 获取组件审计配置。
     */
    protected abstract Integer getNeedAudit(T component);

    /**
     * 判断组件是否支持 Basic 认证。
     */
    protected abstract boolean isBasicSupport(T component);

    /**
     * 判断组件是否支持匿名访问。
     */
    protected abstract boolean isSupportAnonymousAccess(T component);

    /**
     * 统一注册当前类型的所有组件。
     */
    @Override
    public final void register(NeatLogicWebApplicationContext context) {
        for (T component : context.getBeansOfType(getComponentClass()).values()) {
            String className = getClassName(component);
            if (className == null) {
                continue;
            }
            PrivateApiComponentFactory.checkAnnotation(component, context);
            PrivateApiComponentFactory.registerComponent(getApiType(), className, component);
            PrivateApiComponentFactory.registerApiHandler(
                    PrivateApiComponentFactory.createApiHandlerVo(className, getName(component), getConfig(component), context.getId(), getApiType())
            );
            String token = getToken(component);
            if (StringUtils.isNotBlank(token)) {
                token = PrivateApiComponentFactory.normalizeToken(token);
                PrivateApiComponentFactory.registerApiToken(
                        token,
                        PrivateApiComponentFactory.createSystemApiVo(
                                token,
                                className,
                                getName(component),
                                getDescription(component),
                                context,
                                getApiType(),
                                getNeedAudit(component),
                                isBasicSupport(component),
                                isSupportAnonymousAccess(component)
                        )
                );
            }
        }
    }
}
