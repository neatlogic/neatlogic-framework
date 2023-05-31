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
