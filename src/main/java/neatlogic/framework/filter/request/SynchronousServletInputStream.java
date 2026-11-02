/*
 * Copyright (C) 2026  深圳极向量科技有限公司 All Rights Reserved.
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

package neatlogic.framework.filter.request;

import neatlogic.framework.exception.request.RequestBodyException;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import java.io.IOException;
import java.io.InputStream;

/** 同步 Servlet 输入流适配器；延迟打开，避免获取句柄就触发缓存。 */
public abstract class SynchronousServletInputStream extends ServletInputStream {
    private InputStream delegate;
    private boolean finished;
    private boolean closed;

    /** 实际读取时提供底层流，允许缓存层在此完成初始化。 */
    protected abstract InputStream open() throws IOException;

    /** 同一流句柄只打开一次；不同句柄的重放游标互相独立。 */
    private InputStream stream() throws IOException {
        if (closed) { throw new RequestBodyException(500, "read-closed-stream", "", null); }
        if (delegate == null) { delegate = open(); }
        return delegate;
    }

    /** 单字节读取也经过统一的底层限制。 */
    @Override public int read() throws IOException {
        int value = stream().read();
        finished = value < 0;
        return value;
    }

    /** 覆盖批量读取，避免 ServletInputStream 默认逐字节拷贝大文件。 */
    @Override public int read(byte[] bytes, int offset, int length) throws IOException {
        int count = stream().read(bytes, offset, length);
        if (count < 0) { finished = true; }
        return count;
    }

    /** 关闭句柄不能触发尚未执行的请求读取。 */
    @Override public void close() throws IOException {
        closed = true;
        if (delegate != null) { delegate.close(); }
    }

    /** 只有实际读取到 EOF 才标记同步流结束。 */
    @Override public boolean isFinished() { return finished; }
    /** 同步流关闭后不可继续读取。 */
    @Override public boolean isReady() { return !closed; }

    /** 当前过滤器链未启用异步 Servlet，明确拒绝非阻塞模式。 */
    @Override public void setReadListener(ReadListener listener) {
        throw new RequestBodyException(400, "nonblocking-read", "", null);
    }
}
