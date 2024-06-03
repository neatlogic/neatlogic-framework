/*
 * Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.
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

package neatlogic.framework.dynamicplugin;

import neatlogic.framework.common.util.FileUtil;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class PluginClassLoader extends ClassLoader {

    public PluginClassLoader(ClassLoader parent) {
        super(parent);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        System.out.println("name = " + name);
        byte[] classBytes = this.readClassBytes(name);
        if (classBytes.length == 0) {
            throw new ClassNotFoundException("Can not load the class " + name);
        }
        return this.defineClass(null, classBytes, 0, classBytes.length);
    }


    private byte[] readClassBytes(String name) throws ClassNotFoundException {
        try (InputStream in = FileUtil.getData(name);
             ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) != -1) {
                baos.write(buffer, 0, length);
            }
            baos.flush();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new ClassNotFoundException("The class " + name + " not found.", e);
        }
    }
}
