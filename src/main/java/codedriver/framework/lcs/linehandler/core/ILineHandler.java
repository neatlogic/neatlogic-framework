/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.lcs.linehandler.core;

import codedriver.framework.lcs.BaseLineVo;
import org.jsoup.nodes.Element;

public interface ILineHandler {

    /**
     * 获取组件英文名
     *
     * @return 组件英文名
     */
    String getHandler();

    /**
     * 获取组件中文名
     *
     * @return 组件中文名
     */
    String getHandlerName();

    /**
     * 获取组件mainBody content|config
     *
     * @param line 行对象
     * @return mainBody content|config
     */
    String getMainBody(BaseLineVo line);

    /**
     * 设置组件mainBody content|config
     *
     * @param line     行对象
     * @param mainBody content|config
     */
    void setMainBody(BaseLineVo line, String mainBody);

    /**
     * 内容是否需要对比
     * @return
     */
    boolean needCompare();
//    /**
//     * 将contend 转为 html
//     *
//     * @param line 行对象
//     * @return html
//     */
//    String convertContentToHtml(BaseLineVo line);
//
//    /**
//     * 将html 转为content
//     *
//     * @param element element
//     * @return 知识内容
//     */
//    String convertHtmlToContent(Element element);
//
//
//    /**
//     * 将html 转为config
//     *
//     * @param element element
//     * @return 知识内容
//     */
//    String convertHtmlToConfig(Element element);
//
//    /**
//     * 获取实际的组件 兼容figure 等情况
//     *
//     * @return 组件英文名
//     */
//    String getRealHandler(Element element);
}
