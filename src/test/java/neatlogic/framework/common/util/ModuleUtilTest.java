package neatlogic.framework.common.util;

import neatlogic.framework.asynchronization.threadlocal.RequestContext;
import neatlogic.framework.dto.module.ModuleGroupVo;
import neatlogic.framework.dto.module.ModuleVo;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/** 验证模块分组聚合过程始终保存原始国际化键。 */
public class ModuleUtilTest {

    /** 模块分组名称应只在最终读取时翻译一次。 */
    @Test
    public void shouldTranslateModuleGroupNameOnlyOnce() throws Exception {
        RequestContext context = RequestContext.init((RequestContext) null);
        ModuleVo moduleVo = new ModuleVo(
                "module-group-translation-test",
                "test.module.name",
                null,
                "test.module.description",
                "module-group-translation-test",
                "test.module.group.name",
                "1",
                "test.module.group.description",
                null,
                null,
                false
        );
        try {
            context.setLocale(Locale.ENGLISH);
            ModuleUtil.addModule(moduleVo);

            List<ModuleGroupVo> groupList = ModuleUtil.getAllModuleGroupList();
            ModuleGroupVo groupFromList = groupList.stream()
                    .filter(group -> moduleVo.getGroup().equals(group.getGroup()))
                    .findFirst()
                    .orElseThrow(AssertionError::new);
            ModuleGroupVo group = ModuleUtil.getModuleGroup(moduleVo.getGroup());
            Map<String, ModuleGroupVo> groupMap = ModuleUtil.getModuleGroupMap();

            Assert.assertEquals("AI", groupFromList.getGroupName());
            Assert.assertEquals("AI", group.getGroupName());
            Assert.assertEquals("AI", groupMap.get(moduleVo.getGroup()).getGroupName());
        } finally {
            ModuleUtil.removeModule(moduleVo);
            context.release();
        }
    }
}
