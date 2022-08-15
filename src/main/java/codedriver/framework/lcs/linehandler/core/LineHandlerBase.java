/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.lcs.linehandler.core;

import codedriver.framework.lcs.BaseLineVo;
import org.jsoup.nodes.Element;

public abstract class LineHandlerBase implements ILineHandler {

    /**
     * 将contend 转为 html
     *
     * @param line 行对象
     * @return html
     */
    @Override
    public String convertContentToHtml(BaseLineVo line) {
        return myConvertContentToHtml(line);
    }

    protected String myConvertContentToHtml(BaseLineVo line) {
        return "<" + this.getHandler() + ">"
                + (line.getContent() != null ? line.getContent() : "")
                + "</" + this.getHandler() + ">";
    }

    /**
     * 将html 转为content
     *
     * @param element html
     * @return 知识内容
     */
    @Override
    public String convertHtmlToContent(Element element) {
        return myConvertHtmlToContent(element);
    }

    protected String myConvertHtmlToContent(Element element) {
        return element.html();
    }


    @Override
    public String convertHtmlToConfig(Element element) {
        return myConvertHtmlToConfig(element);
    }

    protected String myConvertHtmlToConfig(Element element) {
        return null;
    }

    @Override
    public String getRealHandler(Element element){
        return  myRealHandler(element);
    }

    protected String myRealHandler(Element element){
        return getHandler();
    }
}
