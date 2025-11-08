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

package neatlogic.framework.dao.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class CacheUtils {
    private static final Logger logger = LoggerFactory.getLogger(CacheUtils.class);

    /**
     * MyBatis 的默认实现是通过序列化来做深拷贝
     * 等效于 org.apache.ibatis.cache.decorators.SerializedCache
     */
    @SuppressWarnings("unchecked")
    public static <T> T deepCopy(T obj) {
        if (obj == null) {
            return null;
        }
        if (!(obj instanceof Serializable)) {
            // 如果对象不可序列化，直接返回原始对象（退化为只读缓存）
            logger.debug("对象 {} 未实现 Serializable，直接返回引用", obj.getClass().getName());
            return obj;
        }

        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            // 序列化
            oos.writeObject(obj);
            oos.flush();

            try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()))) {
                // 反序列化
                return (T) ois.readObject();
            }
        } catch (Exception e) {
            logger.warn("对象序列化拷贝失败，直接返回原始对象: {}", e.getMessage());
            return obj;
        }
    }

}
