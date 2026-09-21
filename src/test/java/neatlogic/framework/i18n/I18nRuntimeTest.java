package neatlogic.framework.i18n;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/** 验证翻译运行时遇到非法资源时的进程级失败行为。 */
public class I18nRuntimeTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    /** 重复 JSON 字段必须输出完整原因并以状态码 1 终止子 JVM。 */
    @Test
    public void exitsAndPrintsCauseWhenLanguageResourceIsInvalid() throws Exception {
        Path resourceRoot = temporaryFolder.newFolder("duplicate-language-resource").toPath();
        writeBundle(resourceRoot, "startup-test", "zh", "{\"nfae\":\"示例一\",\"nfae\":\"示例二\"}");
        writeBundle(resourceRoot, "startup-test", "en", "{\"nfae\":\"Sample\"}");
        String classPath = resourceRoot + File.pathSeparator + System.getProperty("java.class.path");
        Path javaExecutable = Path.of(System.getProperty("java.home"), "bin", "java");
        Process process = new ProcessBuilder(javaExecutable.toString(), "-cp", classPath,
                I18nRuntimeStartupProbe.class.getName()).start();

        if (!process.waitFor(15, TimeUnit.SECONDS)) {
            process.destroyForcibly();
            fail("翻译运行时校验子进程未在限时内退出");
        }
        String standardOutput = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        String errorOutput = new String(process.getErrorStream().readAllBytes(), StandardCharsets.UTF_8);

        assertEquals(errorOutput, 1, process.exitValue());
        assertTrue(standardOutput, standardOutput.contains("ModuleInitRuntimeException"));
        assertTrue(standardOutput, standardOutput.contains("读取模块语言资源失败"));
        assertTrue(standardOutput, standardOutput.contains("owner: startup-test"));
        assertTrue(standardOutput, standardOutput.contains("language: zh"));
        assertTrue(standardOutput, standardOutput.contains("language_zh.json"));
        assertTrue(standardOutput, standardOutput.contains("Duplicate field 'nfae'"));
        assertTrue(standardOutput, standardOutput.contains("line: 1, column:"));
        assertFalse(standardOutput, standardOutput.contains("I18N_RUNTIME_INITIALIZED"));
    }

    /** 写入符合模块资源约定的语言文件。 */
    private void writeBundle(Path root, String owner, String language, String content) throws Exception {
        Path directory = root.resolve("neatlogic/resources/" + owner + "/i18n");
        Files.createDirectories(directory);
        Files.writeString(directory.resolve("language_" + language + ".json"), content, StandardCharsets.UTF_8);
    }
}
