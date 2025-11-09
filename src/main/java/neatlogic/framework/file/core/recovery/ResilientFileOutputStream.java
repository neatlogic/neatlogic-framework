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
