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

import com.alibaba.fastjson.JSON;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PropertiesToJson {
    public static void main(String[] args) {
        Properties prop = new Properties();
        InputStream input = null;

        try {//input = PropertiesToJson.class.getResourceAsStream("i18n/message_zh.properties");
            input = new FileInputStream("/Users/cocokong/IdeaProjects/neatlogic-webroot/localconfig/i18n/message_zh.properties");
            BufferedReader aa = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));
            prop.load(aa);

            Map<String, Object> jsonMap = new HashMap<>();
            for (Map.Entry<Object, Object> entry : prop.entrySet()) {
                String key = entry.getKey().toString();
                String value = entry.getValue().toString();
                put(jsonMap, key.split("\\."), value);
            }

            String json = JSON.toJSONString(jsonMap);
            System.out.println(json);

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void put(Map<String, Object> map, String[] keys, Object value) {
        for (int i = 0; i < keys.length - 1; i++) {
            String key = keys[i];
            if (!map.containsKey(key)) {
                map.put(key, new HashMap<String, Object>());
            }
            try {
                map = (Map<String, Object>) map.get(key);
            } catch (Exception ex) {
                System.out.println(String.join(".", keys) + ":" + map.get(key));
                throw ex;
            }
        }
        map.put(keys[keys.length - 1], value);
    }
}
