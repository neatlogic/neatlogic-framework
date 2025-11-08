/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */

package neatlogic.framework.i18n;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.support.MessageSourceAccessor;

@DependsOn("springContextUtil")
@Configuration
public class I18nManager {

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
