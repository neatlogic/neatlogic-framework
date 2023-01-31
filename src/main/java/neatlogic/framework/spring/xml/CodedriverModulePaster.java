/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.framework.spring.xml;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.w3c.dom.Element;

import neatlogic.framework.dto.module.ModuleVo;

public class CodedriverModulePaster extends AbstractSingleBeanDefinitionParser {
    @SuppressWarnings({"rawtypes"})
    protected Class getBeanClass(Element element) {
        return ModuleVo.class;
    }

    @Override
    protected void doParse(Element element, BeanDefinitionBuilder bean) {
        String id = element.getAttribute("id");
        String name = element.getAttribute("name");
        String description = element.getAttribute("description");
        bean.addPropertyValue("id", id);
        bean.addPropertyValue("name", name);
        bean.addPropertyValue("description", description);
        bean.setLazyInit(true);
    }
}
