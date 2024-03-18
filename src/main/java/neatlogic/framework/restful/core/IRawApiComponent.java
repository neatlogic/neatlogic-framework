/*Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

package neatlogic.framework.restful.core;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.dto.FieldValidResultVo;
import neatlogic.framework.restful.constvalue.ApiAnonymousAccessSupportEnum;
import neatlogic.framework.restful.dto.ApiVo;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ClassUtils;

import javax.servlet.http.HttpServletResponse;

public interface IRawApiComponent {

    /**
     * 实现类全名
     *
     * @return 实现类全名
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    default String getClassName() {
        return ClassUtils.getUserClass(this.getClass()).getName();
    }

    /**
     * true时返回格式不再包裹固定格式，固定格式是:{Status:"OK",Return:{},Message:"error"}
     *
     * @return true false
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    default boolean isRaw() {
        return false;
    }

    /**
     * 接口中文名
     *
     * @return 中文名
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    String getName();

    /**
     * 额外配置
     *
     * @return 配置json
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    default String getConfig() {
        return null;
    }

    ;

    /**
     * 是否需要审计
     *
     * @return true false
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    int needAudit();

    /**
     * 服务主入口
     *
     * @param apiVo    api
     * @param param  参数
     * @param response response对象
     * @return 返回值
     * @throws Exception 异常
     */
    Object doService(ApiVo apiVo, String param, HttpServletResponse response) throws Exception;

    /**
     * 获取帮助信息
     *
     * @return 帮助信息json
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    JSONObject help();

    /**
     * 获取参数范例
     *
     * @return 参数范例json
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    default JSONObject example() {
        return null;
    }

    /**
     * 校验入参特殊规则，如：去重
     *
     * @param interfaceVo 接口
     * @param param    参数
     * @param validField  校验字段
     * @return 校验结果
     * @throws Exception 异常
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    FieldValidResultVo doValid(ApiVo interfaceVo, String param, String validField) throws Exception;

    /**
     * 是否支持匿名访问
     *
     * @return true false
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    default ApiAnonymousAccessSupportEnum supportAnonymousAccess() {
        return ApiAnonymousAccessSupportEnum.ANONYMOUS_ACCESS_FORBIDDEN;
    }

    /**
     * 是否禁用返回值循环引用检查
     *
     * @return true false
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    default boolean disableReturnCircularReferenceDetect() {
        return false;
    }
}
