/*
Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License. 
 */

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
