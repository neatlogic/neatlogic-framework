/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.file.core;

import codedriver.framework.file.dto.FileVo;
import com.alibaba.fastjson.JSONObject;
import org.springframework.web.multipart.MultipartFile;

public interface IFileTypeHandler {
    /**
     * 校验附件是否允许访问
     *
     * @param userUuid 用户uuid
     * @param fileVo   附件信息
     * @param jsonObj  校验所需参数
     * @return 是否允许访问
     */
    boolean valid(String userUuid, FileVo fileVo, JSONObject jsonObj);

    String getName();

    String getDisplayName();

    void deleteFile(FileVo fileVo, JSONObject paramObj) throws Exception;

    /**
     * 上传前校验，执行在分析黑白名单之前
     *
     * @param jsonObj 上传参数
     */
    default boolean beforeUpload(JSONObject jsonObj) {
        return true;
    }

    /**
     * 上传完毕的后续动作
     *
     * @param fileVo  文件对象
     * @param jsonObj 传入参数
     */
    default void afterUpload(FileVo fileVo, JSONObject jsonObj) {
    }

    /**
     * 是否需要保存附件（某些上传或分析的场景可能不需要保存附件）
     *
     * @return 是否需要保存
     */
    default boolean needSave() {
        return true;
    }

    /**
     * 分析附件，一般在导入或分析的场景使用，支持流式处理附件内容，由于文件流只能读一次，所以要配合needSave=false使用
     */
    default void analyze(MultipartFile multipartFile) throws Exception {

    }
}
