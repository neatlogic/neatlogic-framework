package neatlogic.framework.i18n;

import neatlogic.framework.exception.module.ModuleInitRuntimeException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/** 验证构建入口对工作区布局和模块覆盖范围的约束。 */
public class ModuleI18nBuildValidatorTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    /** 规范双语资源应返回完整构建摘要。 */
    @Test
    public void validatesWorkspaceAndReturnsSummary() throws Exception {
        Path workspace = temporaryFolder.newFolder("valid-workspace").toPath();
        writeBuildRootPom(workspace, "neatlogic-sample");
        writeBundle(workspace, "neatlogic-sample", "sample", "zh", "{\"sample\":{\"name\":\"示例\"}}");
        writeBundle(workspace, "neatlogic-sample", "sample", "en", "{\"sample\":{\"name\":\"Sample\"}}");

        ModuleI18nBuildValidator.ValidationSummary summary = ModuleI18nBuildValidator.validateWorkspace(workspace);

        assertEquals(1, summary.moduleCount());
        assertEquals(2, summary.languageFileCount());
        assertEquals(1, summary.keyCount());
    }

    /** 资源 owner 必须与 artifact 目录名称一致。 */
    @Test
    public void rejectsOwnerThatDoesNotMatchModuleDirectory() throws Exception {
        Path workspace = temporaryFolder.newFolder("owner-mismatch").toPath();
        writeBuildRootPom(workspace, "neatlogic-sample");
        writeBundle(workspace, "neatlogic-sample", "wrong", "zh", "{\"sample\":\"示例\"}");
        writeBundle(workspace, "neatlogic-sample", "wrong", "en", "{\"sample\":\"Sample\"}");

        assertValidationFails(workspace, "owner 与 artifact 目录不一致");
    }

    /** Reactor 模块同时缺少中英文资源时仅警告，不阻断其他模块校验。 */
    @Test
    public void warnsAndContinuesForModuleWithoutLanguageResources() throws Exception {
        Path workspace = temporaryFolder.newFolder("missing-bundle").toPath();
        writeBuildRootPom(workspace, "neatlogic-sample", "neatlogic-missing");
        writeBundle(workspace, "neatlogic-sample", "sample", "zh", "{\"sample\":\"示例\"}");
        writeBundle(workspace, "neatlogic-sample", "sample", "en", "{\"sample\":\"Sample\"}");
        Path descriptor = workspace.resolve(
                "neatlogic-missing/src/main/java/neatlogic/module/missing/missing-servlet-context.xml");
        Files.createDirectories(descriptor.getParent());
        Files.writeString(descriptor, "<beans/>", StandardCharsets.UTF_8);

        ModuleI18nBuildValidator.ValidationSummary summary = ModuleI18nBuildValidator.validateWorkspace(workspace);

        assertEquals(1, summary.moduleCount());
        assertEquals(2, summary.languageFileCount());
        assertEquals(1, summary.keyCount());
    }

    /** 单语言资源必须由运行时加载器拒绝。 */
    @Test
    public void rejectsMissingLanguageMirror() throws Exception {
        Path workspace = temporaryFolder.newFolder("missing-language").toPath();
        writeBuildRootPom(workspace, "neatlogic-sample");
        writeBundle(workspace, "neatlogic-sample", "sample", "zh", "{\"sample\":\"示例\"}");

        assertValidationFails(workspace, "未找到模块语言资源，language: en");
    }

    /** 中英文 key 不一致时必须由运行时校验规则拒绝。 */
    @Test
    public void rejectsDifferentLanguageKeys() throws Exception {
        Path workspace = temporaryFolder.newFolder("key-mismatch").toPath();
        writeBuildRootPom(workspace, "neatlogic-sample");
        writeBundle(workspace, "neatlogic-sample", "sample", "zh", "{\"sample\":\"示例\"}");
        writeBundle(workspace, "neatlogic-sample", "sample", "en", "{\"other\":\"Sample\"}");

        assertValidationFails(workspace, "模块中英文 key 不一致");
    }

    /** 构建入口必须向标准输出打印包含资源上下文的完整异常链。 */
    @Test
    public void printsDuplicateFieldFailureToStandardOutputAndRethrows() throws Exception {
        Path workspace = temporaryFolder.newFolder("duplicate-field").toPath();
        writeBuildRootPom(workspace, "neatlogic-sample");
        writeBundle(workspace, "neatlogic-sample", "sample", "zh",
                "{\"nfae\":\"示例一\",\"nfae\":\"示例二\"}");
        writeBundle(workspace, "neatlogic-sample", "sample", "en", "{\"nfae\":\"Sample\"}");
        PrintStream originalOutput = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
            ModuleI18nBuildValidator.main(new String[]{workspace.toString()});
            fail("重复 JSON 字段未导致构建校验失败");
        } catch (ModuleInitRuntimeException ex) {
            assertTrue(ex.getMessage(), ex.getMessage().contains("读取模块语言资源失败"));
        } finally {
            System.setOut(originalOutput);
        }

        String consoleOutput = output.toString(StandardCharsets.UTF_8);
        assertTrue(consoleOutput, consoleOutput.contains("读取模块语言资源失败"));
        assertTrue(consoleOutput, consoleOutput.contains("Duplicate field 'nfae'"));
        assertTrue(consoleOutput, consoleOutput.contains("owner: sample"));
        assertTrue(consoleOutput, consoleOutput.contains("language: zh"));
        assertTrue(consoleOutput, consoleOutput.contains("language_zh.json"));
    }

    /** 写入只包含目标模块的聚合 POM。 */
    private void writeBuildRootPom(Path workspace, String... modules) throws Exception {
        StringBuilder content = new StringBuilder("<project><modules>");
        for (String module : modules) {
            content.append("<module>../").append(module).append("</module>");
            Files.createDirectories(workspace.resolve(module));
        }
        content.append("</modules></project>");
        Path pom = workspace.resolve("neatlogic-build-root/pom.xml");
        Files.createDirectories(pom.getParent());
        Files.writeString(pom, content, StandardCharsets.UTF_8);
    }

    /** 写入符合模块资源路径约定的语言文件。 */
    private void writeBundle(Path workspace, String module, String owner, String language, String content)
            throws Exception {
        Path directory = workspace.resolve(module + "/src/main/resources/neatlogic/resources/" + owner + "/i18n");
        Files.createDirectories(directory);
        Files.writeString(directory.resolve("language_" + language + ".json"), content, StandardCharsets.UTF_8);
    }

    /** 断言工作区校验失败且包含可定位原因。 */
    private void assertValidationFails(Path workspace, String expectedMessage) throws Exception {
        try {
            ModuleI18nBuildValidator.validateWorkspace(workspace);
            fail("非法工作区未被拒绝");
        } catch (ModuleInitRuntimeException ex) {
            assertTrue(ex.getMessage(), ex.getMessage().contains(expectedMessage));
        }
    }
}
