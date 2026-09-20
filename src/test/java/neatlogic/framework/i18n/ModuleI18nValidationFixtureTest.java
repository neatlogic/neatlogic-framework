package neatlogic.framework.i18n;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import neatlogic.framework.exception.module.ModuleInitRuntimeException;
import org.junit.Test;

import java.io.InputStream;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/** 使用共享 fixture 验证 Java 与工作区脚本的模板规则保持一致。 */
public class ModuleI18nValidationFixtureTest {

    /** fixture 中的正反例必须分别通过和拒绝。 */
    @Test
    public void validatesSharedCases() throws Exception {
        try (InputStream inputStream = getClass().getResourceAsStream("/i18n/validation-cases.json")) {
            assertNotNull(inputStream);
            JsonNode root = new ObjectMapper().readTree(inputStream);
            ModuleI18nValidator validator = new ModuleI18nValidator();
            for (JsonNode value : root.get("valid")) {
                validator.validateTemplate(value.asText(), "en", "fixture", "test", "fixture");
            }
            for (JsonNode value : root.get("invalid")) {
                try {
                    validator.validateTemplate(value.asText(), "en", "fixture", "test", "fixture");
                    fail("非法模板未被拒绝: " + value.asText());
                } catch (ModuleInitRuntimeException ignored) {
                    // fixture 明确标记为非法，命中预期异常即通过。
                }
            }
        }
    }
}
