/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.file.core.recovery;

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
//            System.out.println("A:" + os.getClass().getName());//java.io.BufferedOutputStream
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
//        addStatusIfCountNotOverLimit(new ErrorStatus("IO failure while writing to " + getDescription(), this, e));
        presumedClean = false;
        if (recoveryCoordinator == null) {
            recoveryCoordinator = new RecoveryCoordinator();
        }
        logger.error("写入" + getDescription() + "时IO故障");
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
//            addStatusIfCountNotOverLimit(new ErrorStatus("Failed to open " + getDescription(), this, e));
        }
    }

}
