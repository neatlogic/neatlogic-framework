package codedriver.framework.restful.core;

import codedriver.framework.dto.FieldValidResultVo;
import codedriver.framework.restful.dto.ApiVo;
import com.alibaba.fastjson.JSONObject;
import org.springframework.util.ClassUtils;

import javax.servlet.http.HttpServletResponse;

public interface IApiComponent {

    /**
     * @param @return
     * @return String
     * @Author: chenqiwei
     * @Time:Jun 19, 2020
     * @Description: 实现类全名
     */
    public default String getClassName() {
        return ClassUtils.getUserClass(this.getClass()).getName();
    }

    /**
     * @param @return
     * @return boolean
     * @Author: chenqiwei
     * @Time:Jun 19, 2020
     * @Description: true时返回格式不再包裹固定格式，固定格式是:{Status:"OK",Return:{},Message:"error"}
     */
    public default boolean isRaw() {
        return false;
    }

    /**
     * @param @return
     * @return String
     * @Author: chenqiwei
     * @Time:Jun 19, 2020
     * @Description: 接口中文名
     */
    public String getName();

    /**
     * @param @return
     * @return String
     * @Author: chenqiwei
     * @Time:Jun 19, 2020
     * @Description: 额外配置
     */
    public String getConfig();

    /**
     * @param @return
     * @return int
     * @Author: chenqiwei
     * @Time:Jun 19, 2020
     * @Description: 是否需要审计
     */
    public int needAudit();

    /**
     * @param @param  apiVo
     * @param @param  jsonObj
     * @param @return
     * @param @throws Exception
     * @return Object
     * @Author: chenqiwei
     * @Time:Jun 19, 2020
     * @Description: 服务主入口
     */
    public Object doService(ApiVo apiVo, JSONObject jsonObj, HttpServletResponse response) throws Exception;

    /**
     * @param @return
     * @return JSONObject
     * @Author: chenqiwei
     * @Time:Jun 19, 2020
     * @Description: 获取帮助信息
     */
    public JSONObject help();

    /**
     * @Description: 校验入参特殊规则，如：去重
     * @Author: 89770
     * @Date: 2021/2/20 16:09
     * @Params: [interfaceVo, paramObj, validField]
     * @Returns: void
     **/
    FieldValidResultVo doValid(ApiVo interfaceVo, JSONObject paramObj, String validField) throws Exception;
}
