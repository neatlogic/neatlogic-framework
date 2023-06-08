/*
 * Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package neatlogic.framework.i18n;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.Locale;

@DependsOn("springContextUtil")
@Configuration
public class I18nManager {
    @Bean
    public AcceptHeaderLocaleResolver localeResolver() {
        final AcceptHeaderLocaleResolver resolver = new AcceptHeaderLocaleResolver();
        resolver.setDefaultLocale(Locale.CHINESE);
        return resolver;
    }

    @Bean
    public MessageSourceAccessor messageSourceAccessor() {
        //ReloadableResourceBundleMessageSource source = new ReloadableResourceBundleMessageSource();
        ReloadableJsonBundleMessageSource source = new ReloadableJsonBundleMessageSource();
        source.setBasename("classpath:i18n/language");
        source.setCacheSeconds(1000);
        source.setDefaultEncoding("utf-8");
        return new MessageSourceAccessor(source);
    }
}
