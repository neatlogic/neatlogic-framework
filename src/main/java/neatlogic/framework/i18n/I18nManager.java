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
