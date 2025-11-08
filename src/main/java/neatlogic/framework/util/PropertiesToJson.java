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
