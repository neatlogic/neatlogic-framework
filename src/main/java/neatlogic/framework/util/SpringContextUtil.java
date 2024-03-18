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

package neatlogic.framework.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;

@Component
public class SpringContextUtil implements ApplicationContextAware {

    private static ApplicationContext ctx;

    @Override
    public void setApplicationContext(@Nonnull ApplicationContext applicationContext) {
        ctx = applicationContext;
    }

    /**
     * 根据 Class 类找到在 Spring 中注册的 Bean
     * @param clazz 类
     * @param <T>   泛型
     * @return spring 中的单例
     */
    public static <T> T getBean(@Nonnull Class<T> clazz) {
        return ctx.getBean(clazz);
    }

    /**
     * 根据注册的 beanName 找到在 Spring 中注册的 Bean
     * @param beanName beanName
     * @return spring 中的单例
     */
    public static Object getBean(String beanName) {
        return ctx.getBean(beanName);
    }

    /**
     * 根据注册的 beanName 和 Class 类型找到在 Spring 中注册的 Bean
     * @param beanName beanName
     * @param clazz    类
     * @return spring 中的单例
     */
    public static <T> T getBean(String beanName, Class<T> clazz) {
        return ctx.getBean(beanName, clazz);
    }
}
