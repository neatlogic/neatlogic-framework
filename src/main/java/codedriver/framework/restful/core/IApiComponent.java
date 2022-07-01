/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.restful.core;

import codedriver.framework.dto.FieldValidResultVo;
import codedriver.framework.restful.dto.ApiVo;
import com.alibaba.fastjson.JSONObject;
import org.springframework.util.ClassUtils;

import javax.servlet.http.HttpServletResponse;

public interface IApiComponent {

    /**
     * 实现类全名
     *
     * @return 实现类全名
     */
    default String getClassName() {
        return ClassUtils.getUserClass(this.getClass()).getName();
    }

    /**
     * true时返回格式不再包裹固定格式，固定格式是:{Status:"OK",Return:{},Message:"error"}
     *
     * @return true false
     */
    default boolean isRaw() {
        return false;
    }

    /**
     * 接口中文名
     *
     * @return 中文名
     */
    String getName();

    /**
     * 额外配置
     *
     * @return 配置json
     */
    String getConfig();

    /**
     * 是否需要审计
     *
     * @return true false
     */
    int needAudit();

    /**
     * 服务主入口
     *
     * @param apiVo    api
     * @param jsonObj  参数
     * @param response response对象
     * @return 返回值
     * @throws Exception 异常
     */
    Object doService(ApiVo apiVo, JSONObject jsonObj, HttpServletResponse response) throws Exception;

    /**
     * 获取帮助信息
     *
     * @return 帮助信息json
     */
    JSONObject help();

    /**
     * 获取参数范例
     *
     * @return 参数范例json
     */
    default JSONObject example() {
        return null;
    }

    /**
     * 校验入参特殊规则，如：去重
     *
     * @param interfaceVo 接口
     * @param paramObj    参数
     * @param validField  校验字段
     * @return 校验结果
     * @throws Exception 异常
     */
    FieldValidResultVo doValid(ApiVo interfaceVo, JSONObject paramObj, String validField) throws Exception;

    /**
     * 是否支持匿名访问
     *
     * @return true false
     */
    default boolean supportAnonymousAccess() {
        return false;
    }

    /**
     * 是否禁用返回值循环引用检查
     *
     * @return true false
     */
    default boolean disableReturnCircularReferenceDetect() {
        return false;
    }
}
