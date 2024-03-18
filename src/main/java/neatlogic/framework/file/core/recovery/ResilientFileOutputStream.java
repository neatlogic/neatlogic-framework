/*Copyright (C) $today.year  深圳极向量科技有限公司 All Rights Reserved.

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

package neatlogic.framework.file.core.recovery;

import java.io.*;
import java.nio.channels.FileChannel;

/**
 * 弹性文件输出流
 */
public class ResilientFileOutputStream extends ResilientOutputStreamBase {

    private File file;
    private FileOutputStream fos;

    public ResilientFileOutputStream(File file, boolean append, long bufferSize) throws FileNotFoundException {
        this.file = file;
        fos = new FileOutputStream(file, append);
        this.os = new BufferedOutputStream(fos, (int) bufferSize);
        this.presumedClean = true;
    }

    public FileChannel getChannel() {
        if (os == null) {
            return null;
        }
        return fos.getChannel();
    }

    @Override
    OutputStream openNewOutputStream() throws IOException {
        fos = new FileOutputStream(file, true);
        return new BufferedOutputStream(fos);
    }

    @Override
    String getDescription() {
        return "file [" + file + "]";
    }

    @Override
    public String toString() {
        return "c.q.l.c.recovery.ResilientFileOutputStream@" + System.identityHashCode(this);
    }

}
