/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.file.core.recovery;

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
