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

import org.apache.commons.fileupload.FileItem;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import java.util.List;

/** Spring 上传视图的最小扩展，认证依赖此契约而不依赖解析器的具体实现。 */
public interface ParsedMultipartRequest extends MultipartHttpServletRequest {
    /** 返回按原始顺序排列的普通表单项，保留 FileItem 解码语义，文件内容不在此列表中。 */
    List<FileItem> getFormItems();
}
