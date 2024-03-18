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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class JsonResourceBundleControl extends ResourceBundle.Control {
    private static final String FORMAT_JSON = "json";

    @Override
    public List<String> getFormats(String baseName) {
        return Collections.singletonList(FORMAT_JSON);
    }

    @Override
    public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload) throws IOException, IllegalAccessException, InstantiationException {
        if (!FORMAT_JSON.equals(format)) {
            return super.newBundle(baseName, locale, format, loader, reload);
        }
        if (locale == null || StringUtils.isBlank(locale.getLanguage())) {
            locale = Locale.getDefault();
        }
        String bundleName = toBundleName(baseName, locale);
        String resourceName = toResourceName(bundleName, format);
        InputStream stream = loader.getResourceAsStream(resourceName);
        if (stream == null) {
            return null;
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> map = mapper.readValue(stream, Map.class);
            Properties props = new Properties();
            String key = StringUtils.EMPTY;
            getProps(map, props, key);
            // 将 Properties 对象转换为字节数组输出流
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            props.store(byteArrayOutputStream, "comments");

            // 将字节数组输出流转换为输入流
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            return new PropertyResourceBundle(byteArrayInputStream);
        } finally {
            stream.close();
        }
    }

    /**
     * 递归获取props
     *
     * @param map   原始数据
     * @param props 返回的props
     */
    private void getProps(Map<String, Object> map, Properties props, String key) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String tmpKey = StringUtils.EMPTY;
            if (StringUtils.isNotBlank(key)) {
                tmpKey = key + ".";
            }
            Object value = entry.getValue();
            if (value instanceof Map) {
                getProps((Map) value, props, tmpKey + entry.getKey());
            } else {
                props.put(tmpKey + entry.getKey(), entry.getValue().toString());
            }
        }
    }
}
