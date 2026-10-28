package neatlogic.framework.file.dto;

import com.alibaba.fastjson.annotation.JSONField;
import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.restful.annotation.EntityField;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

/** 一次下载的文件流及元信息；调用方关闭结果时同时释放文件流拥有的资源。 */
public class FileDownloadVo implements Closeable {
    @JSONField(serialize = false)
    private final InputStream inputStream;
    @EntityField(name = "下载文件名称", type = ApiParamType.STRING)
    private final String fileName;
    @EntityField(name = "下载内容类型", type = ApiParamType.STRING)
    private final String contentType;

    /** 元信息允许为空，由下载入口使用原附件记录补全。 */
    public FileDownloadVo(InputStream inputStream, String fileName, String contentType) {
        this.inputStream = inputStream;
        this.fileName = fileName;
        this.contentType = contentType;
    }

    /** 返回本次下载的文件流，不额外读取或复制其内容。 */
    public InputStream getInputStream() { return inputStream; }

    /** 返回存储服务提供的文件名称，包含实际后缀。 */
    public String getFileName() { return fileName; }

    /** 返回存储服务提供的内容类型。 */
    public String getContentType() { return contentType; }

    /** 关闭下载流；远端存储流负责同时释放其 HTTP 连接。 */
    @Override
    public void close() throws IOException {
        if (inputStream != null) {
            inputStream.close();
        }
    }
}
