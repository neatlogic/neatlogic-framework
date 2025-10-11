/*
 * Copyright (C) 2025  深圳极向量科技有限公司 All Rights Reserved.
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
