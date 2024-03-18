/*Copyright (C) 2023  深圳极向量科技有限公司 All Rights Reserved.

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;

public abstract class ResilientOutputStreamBase extends OutputStream {

    private Logger logger = LoggerFactory.getLogger(ResilientOutputStreamBase.class);

    private RecoveryCoordinator recoveryCoordinator;

    protected OutputStream os;
    // 假定清洁
    protected boolean presumedClean = true;

    private boolean isPresumedInError() {
        // recoveryCoordinator的存在表示失败状态
        return (recoveryCoordinator != null && !presumedClean);
    }

    @Override
    public void write(byte b[], int off, int len) {
//        System.out.println(6);
        if (isPresumedInError()) {
            if (!recoveryCoordinator.isTooSoon()) {
                attemptRecovery();
            }
            // 返回，而不管恢复尝试是否成功
            return;
        }

        try {
            //java.io.BufferedOutputStream
            os.write(b, off, len);
            postSuccessfulWrite();
        } catch (IOException e) {
            postIOFailure(e);
        }
    }

    @Override
    public void write(int b) {
        if (isPresumedInError()) {
            if (!recoveryCoordinator.isTooSoon()) {
                attemptRecovery();
            }
            return; // 返回，而不管恢复尝试是否成功
        }
        try {
            os.write(b);
            postSuccessfulWrite();
        } catch (IOException e) {
            postIOFailure(e);
        }
    }

    @Override
    public void flush() {
        if (os != null) {
            try {
                os.flush();
                postSuccessfulWrite();
            } catch (IOException e) {
                postIOFailure(e);
            }
        }
    }

    abstract String getDescription();

    abstract OutputStream openNewOutputStream() throws IOException;

    private void postSuccessfulWrite() {
        if (recoveryCoordinator != null) {
            recoveryCoordinator = null;
        }
    }

    public void postIOFailure(IOException e) {
        presumedClean = false;
        if (recoveryCoordinator == null) {
            recoveryCoordinator = new RecoveryCoordinator();
        }
        logger.error("写入" + getDescription() + "时IO故障");
        logger.error(e.getMessage(), e);
    }

    @Override
    public void close() throws IOException {
        if (os != null) {
            os.close();
        }
    }

    /**
     * 尝试恢复，重新打开文件
     */
    void attemptRecovery() {
        try {
            close();
        } catch (IOException e) {
        }

        // 后续写入必须始终处于追加模式
        try {
            os = openNewOutputStream();
            presumedClean = true;
        } catch (IOException e) {
            logger.error("打开" + getDescription() + "时IO故障");
            logger.error(e.getMessage(), e);
        }
    }

}
