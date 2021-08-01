/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.exception.file;

import codedriver.framework.exception.core.ApiRuntimeException;

public class FileStorageMediumHandlerNotFoundException extends ApiRuntimeException {
    public FileStorageMediumHandlerNotFoundException(String type) {
        super("存储介质类型：" + type + "找不到相应的控制器，请通知管理员或厂商解决");
    }

}
