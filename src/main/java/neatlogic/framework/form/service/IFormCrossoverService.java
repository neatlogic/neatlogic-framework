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

package neatlogic.framework.form.service;

import com.alibaba.fastjson.JSONArray;
import neatlogic.framework.crossover.ICrossoverService;
import neatlogic.framework.form.dto.FormAttributeVo;
import com.alibaba.fastjson.JSONObject;

public interface IFormCrossoverService extends ICrossoverService {

    /**
     * 保存表单属性与其他功能的引用关系
     * @param formAttributeVo
     */
    void saveDependency(FormAttributeVo formAttributeVo);

    /**
     * 表格输入组件密码类型加密
     * @param data
     */
    JSONArray staticListPasswordEncrypt(JSONArray data, JSONObject config);
}
