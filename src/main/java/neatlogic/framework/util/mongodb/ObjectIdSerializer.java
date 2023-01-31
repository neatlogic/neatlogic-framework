/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

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
