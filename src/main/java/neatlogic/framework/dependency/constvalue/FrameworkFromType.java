/*
Copyright(c) $today.year NeatLogic Co., Ltd. All Rights Reserved.

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

package neatlogic.framework.dependency.constvalue;

import neatlogic.framework.dependency.core.IFromType;

/**
 * 被引用者（上游）类型
 *
 * @author: linbq
 * @since: 2021/4/2 10:30
 **/
public enum FrameworkFromType implements IFromType {
    MATRIX("matrix", "矩阵"),
    MATRIXATTR("matrixattr", "矩阵属性"),
    FORM("form", "表单"),
    FORMSCENE("formscene", "表单场景"),
    FORMATTR("formattr", "表单属性"),
    INTEGRATION("integration", "集成"),
    CMDBCI("cmdbci", "cmdb模型"),
    CMDBCIATTR("cmdbciattr", "cmdb模型属性"),
    WORKTIME("worktime", "服务窗口"),
    NOTIFY_POLICY("notifypolicy", "通知策略");

    private String value;
    private String text;

    FrameworkFromType(String value, String text) {
        this.value = value;
        this.text = text;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String getText() {
        return text;
    }
}
