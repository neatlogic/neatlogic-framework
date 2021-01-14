package codedriver.framework.common.constvalue;

import java.util.List;


/**
 * @Title: IEnum
 * @Package: codedriver.framework.common.constvalue
 * @Description: 实现此接口的枚举类，可纳入EnumFactory管理
 * @Author: laiwt
 * @Date: 2021/1/11 19:03
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 **/
public interface IEnum {
    /**
     * @Description: 不同的枚举类，返回不同的枚举值，可自由组合成List<>或者JSONArray
     * @Author: laiwt
     * @Date: 2021/1/12 14:57
     * @Params: []
     * @Returns: java.util.List
    **/
    List getValueTextList();
}
