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
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileCountLimitExceededException;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.MultipartStream;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.multipart.support.DefaultMultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/** 扩展现有 Spring resolver；仅受管理的 JWT 请求采用请求级限制和清理。 */
public class RequestMultipartResolver extends CommonsMultipartResolver {
    private static final Logger LOG = LoggerFactory.getLogger(RequestMultipartResolver.class);

    /** 创建入口持有的惰性视图；Spring 会识别已有 multipart，不重复解析或接管清理。 */
    ManagedMultipartRequest createManagedRequest(HttpServletRequest request, RequestBodyResources resources) {
        return new ManagedMultipartRequest(request, resources);
    }

    /** 同一请求的普通表单项与 Spring 视图共用 FileItem，签名算法留在认证模块。 */
    public final class ManagedMultipartRequest extends DefaultMultipartHttpServletRequest implements ParsedMultipartRequest {
        private final RequestBodyResources resources;
        private List<FileItem> formItems = Collections.emptyList();

        /** 保存资源所有者，构造时不接收上传内容。 */
        private ManagedMultipartRequest(HttpServletRequest request, RequestBodyResources resources) {
            super(request);
            this.resources = resources;
        }

        /** 原始 FileItem 顺序、解码方法保留给认证；业务参数仍用 Spring 转换。 */
        @Override public List<FileItem> getFormItems() { getMultiFileMap(); return formItems; }

        /** 每个请求使用独立解析器，避免更改共享配置时产生并发串扰。 */
        @Override protected void initializeMultipart() {
            if (resources.getFailure() != null) { throw resources.getFailure(); }
            final long[] fieldBytes = {0};
            DiskFileItemFactory factory = new DiskFileItemFactory(resources.partMemoryThreshold, resources.directory) {
                /** 仅扩展普通字段累计额度，项数及解析失败清理由 Commons FileUpload 负责。 */
                @Override public FileItem createItem(String field, String contentType, boolean formField, String fileName) {
                    return new DiskFileItem(field, contentType, formField, fileName,
                            resources.partMemoryThreshold, resources.directory) {
                        /** 文件内容仍由 Commons IO 的阈值输出流管理，字段只增加计数。 */
                        @Override public OutputStream getOutputStream() throws IOException {
                            OutputStream output = super.getOutputStream();
                            if (!formField) { return output; }
                            return new FilterOutputStream(output) {
                                /** 单字节写入按字段累计额度计数。 */
                                @Override public void write(int value) throws IOException { account(1); out.write(value); }
                                /** 批量写入只计数一次，不经逐字节包装重复累计。 */
                                @Override public void write(byte[] bytes, int offset, int length) throws IOException {
                                    account(length); out.write(bytes, offset, length);
                                }
                                /** 超限立即失败，禁止先生成完整字符串再做长度判断。 */
                                private void account(int length) {
                                    fieldBytes[0] += length;
                                    if (fieldBytes[0] > resources.fieldsMax) {
                                        throw resources.failed(new RequestBodyException("multipart.fields", resources.fieldsMax, fieldBytes[0]));
                                    }
                                }
                            };
                        }
                    };
                }
            };
            ServletFileUpload upload = new ServletFileUpload(factory);
            upload.setSizeMax(resources.multipartMax);
            upload.setFileCountMax(resources.partsMax);
            upload.setHeaderEncoding(determineEncoding(this));
            try {
                List<FileItem> items = upload.parseRequest((HttpServletRequest) getRequest());
                // 解析成功才转交所有权，后续 Spring 视图转换或认证失败仍由入口统一释放。
                resources.items.addAll(items);
                formItems = Collections.unmodifiableList(items.stream().filter(FileItem::isFormField)
                        .collect(Collectors.toList()));
                MultipartParsingResult result = parseFileItems(items, determineEncoding(this));
                setMultipartFiles(result.getMultipartFiles());
                setMultipartParameters(result.getMultipartParameters());
                setMultipartParameterContentTypes(result.getMultipartParameterContentTypes());
            } catch (RequestBodyException ex) {
                LOG.error("Multipart request rejected: {}", getRequestURI(), ex);
                throw resources.failed(ex);
            } catch (FileUploadBase.SizeLimitExceededException ex) {
                LOG.error("Multipart request exceeds limit: {}", getRequestURI(), ex);
                throw resources.failed(new RequestBodyException("multipart.body", ex.getPermittedSize(), ex.getActualSize()));
            } catch (FileCountLimitExceededException ex) {
                LOG.error("Multipart item count exceeds limit: {}", getRequestURI(), ex);
                throw resources.failed(new RequestBodyException("multipart.parts", ex.getLimit(), ex.getLimit() + 1));
            } catch (FileUploadBase.IOFileUploadException ex) {
                LOG.error("Cannot read or store multipart request: {}", getRequestURI(), ex);
                if (resources.getFailure() != null) { throw resources.getFailure(); }
                int status = ex.getCause() instanceof MultipartStream.MalformedStreamException ? 400 : 500;
                throw resources.failed(new RequestBodyException(status, "read-multipart", getRequestURI(), ex));
            } catch (FileUploadException | RuntimeException ex) {
                LOG.error("Cannot parse multipart request: {}", getRequestURI(), ex);
                if (resources.getFailure() != null) { throw resources.getFailure(); }
                throw resources.failed(new RequestBodyException(400, "parse-multipart", getRequestURI(), ex));
            }
        }
    }
}
