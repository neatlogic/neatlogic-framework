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

package neatlogic.framework.util.mongodb;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import org.bson.types.ObjectId;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * mongodb的_id字段是个ObjectId对象，此转换器让fastjson可以正确转换ObjectId中的id值
 */
public class ObjectIdSerializer implements IJsonSerializer {
    @Override
    public void write(JSONSerializer jsonSerializer, Object o, Object o1, Type type, int i) throws IOException {
        SerializeWriter out = jsonSerializer.out;
        if (o instanceof ObjectId) {
            ObjectId objectId = (ObjectId) o;
            out.writeString(objectId.toString());
        }
    }

    public Class<?> getType() {
        return ObjectId.class;
    }
}
