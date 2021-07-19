/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.common.constvalue;

import java.util.List;


public interface IEnum<T> {
    /**
     * 不同的枚举类，返回不同的枚举值，可自由组合成List<>或者JSONArray
     *
     * @return 枚举列表
     */
    List<T> getValueTextList();
}
