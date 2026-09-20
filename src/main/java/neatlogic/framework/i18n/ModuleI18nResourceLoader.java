package neatlogic.framework.i18n;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.StreamReadFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import neatlogic.framework.exception.module.ModuleInitRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 发现并读取各模块语言资源，不承担运行时翻译和缺失诊断职责。
 */
final class ModuleI18nResourceLoader {
    private static final Logger logger = LoggerFactory.getLogger(ModuleI18nResourceLoader.class);
    private static final String RESOURCE_PATTERN = "classpath*:neatlogic/resources/*/i18n/language_%s.json";
    private static final List<String> SUPPORTED_LANGUAGES = List.of("zh", "en");

    private final ResourcePatternResolver resolver;
    private final ObjectMapper mapper;
    private final ModuleI18nValidator validator = new ModuleI18nValidator();

    /** 使用指定 ClassLoader 创建资源加载器。 */
    ModuleI18nResourceLoader(ClassLoader classLoader) {
        this.resolver = new PathMatchingResourcePatternResolver(classLoader);
        this.mapper = new ObjectMapper(JsonFactory.builder()
                .enable(StreamReadFeature.STRICT_DUPLICATE_DETECTION)
                .build());
    }

    /** 加载全部受支持语言，并完成跨语言结构校验。 */
    LoadedResources load() {
        Map<String, Map<String, Map<String, String>>> ownerLanguageMessages = new LinkedHashMap<>();
        Map<String, Map<String, String>> globalMessages = new LinkedHashMap<>();
        Map<String, Map<String, String>> globalOrigins = new LinkedHashMap<>();
        Map<String, Map<String, Set<String>>> ownerKeyMap = new LinkedHashMap<>();
        Map<String, Map<String, Map<String, Set<Integer>>>> ownerArgumentMap = new LinkedHashMap<>();
        for (String language : SUPPORTED_LANGUAGES) {
            LanguageResources resources = loadLanguage(language);
            globalMessages.put(language, resources.messages);
            globalOrigins.put(language, resources.origins);
            ownerKeyMap.put(language, resources.ownerKeys);
            ownerArgumentMap.put(language, resources.ownerArguments);
            for (Map.Entry<String, Map<String, String>> entry : resources.ownerMessages.entrySet()) {
                ownerLanguageMessages.computeIfAbsent(entry.getKey(), key -> new LinkedHashMap<>())
                        .put(language, entry.getValue());
            }
        }
        validator.validateLanguageStructure(ownerKeyMap, ownerArgumentMap);
        return new LoadedResources(ownerLanguageMessages, globalMessages, globalOrigins);
    }

    /** 加载一种语言的全部模块资源并检查跨模块重复 key。 */
    private LanguageResources loadLanguage(String language) {
        List<Resource> resources = new ArrayList<>();
        try {
            Collections.addAll(resources, resolver.getResources(String.format(RESOURCE_PATTERN, language)));
            resources.sort((left, right) -> resourceName(left).compareTo(resourceName(right)));
        } catch (IOException ex) {
            logger.error("扫描模块语言资源失败，language: {}", language, ex);
            throw new ModuleInitRuntimeException("扫描模块语言资源失败，language: " + language, ex);
        }
        if (resources.isEmpty()) {
            throw new ModuleInitRuntimeException("未找到模块语言资源，language: " + language
                    + ", pattern: " + RESOURCE_PATTERN);
        }
        Map<String, String> messages = new LinkedHashMap<>();
        Map<String, String> origins = new LinkedHashMap<>();
        Map<String, Map<String, String>> ownerMessages = new LinkedHashMap<>();
        Map<String, Set<String>> ownerKeys = new LinkedHashMap<>();
        Map<String, Map<String, Set<Integer>>> ownerArguments = new LinkedHashMap<>();
        for (Resource resource : resources) {
            String origin = resourceName(resource);
            String owner = resolveOwner(origin);
            Map<String, String> resourceMessages = readResource(resource, language, owner, origin);
            ownerKeys.computeIfAbsent(owner, key -> new HashSet<>()).addAll(resourceMessages.keySet());
            ownerMessages.computeIfAbsent(owner, key -> new LinkedHashMap<>()).putAll(resourceMessages);
            Map<String, Set<Integer>> argumentMap = ownerArguments.computeIfAbsent(owner, key -> new HashMap<>());
            for (Map.Entry<String, String> entry : resourceMessages.entrySet()) {
                String previousOrigin = origins.putIfAbsent(entry.getKey(), origin);
                if (previousOrigin != null) {
                    throw new ModuleInitRuntimeException("模块语言资源存在重复 key，language: " + language
                            + ", key: " + entry.getKey() + ", first: " + previousOrigin + ", duplicate: " + origin);
                }
                messages.put(entry.getKey(), entry.getValue());
                argumentMap.put(entry.getKey(), I18nMessageTemplate.extractArgumentIndexes(entry.getValue()));
            }
        }
        return new LanguageResources(messages, origins, ownerMessages, ownerKeys, ownerArguments);
    }

    /** 读取单个模块语言文件并展开为点号路径。 */
    private Map<String, String> readResource(Resource resource, String language, String owner, String origin) {
        try (InputStream inputStream = resource.getInputStream()) {
            JsonNode root = mapper.readTree(inputStream);
            return validator.flatten(root, origin, language, owner);
        } catch (IOException ex) {
            logger.error("读取模块语言资源失败，owner: {}, language: {}, resource: {}", owner, language, origin, ex);
            throw new ModuleInitRuntimeException("读取模块语言资源失败，owner: " + owner + ", language: "
                    + language + ", resource: " + origin, ex);
        }
    }

    /** 从规范资源路径中提取模块所有者。 */
    private String resolveOwner(String resourceName) {
        String marker = "neatlogic/resources/";
        int start = resourceName.indexOf(marker);
        int end = start < 0 ? -1 : resourceName.indexOf("/i18n/", start + marker.length());
        if (start < 0 || end < 0) {
            throw new ModuleInitRuntimeException("模块语言资源路径不符合规范，resource: " + resourceName);
        }
        return resourceName.substring(start + marker.length(), end);
    }

    /** 获取稳定的资源标识。 */
    private static String resourceName(Resource resource) {
        try {
            return resource.getURL().toExternalForm();
        } catch (IOException ex) {
            throw new ModuleInitRuntimeException("获取模块语言资源 URL 失败，resource: " + resource.getDescription(), ex);
        }
    }

    /** 加载后的不可变目录原料。 */
    record LoadedResources(Map<String, Map<String, Map<String, String>>> ownerLanguageMessages,
                           Map<String, Map<String, String>> globalMessages,
                           Map<String, Map<String, String>> globalOrigins) {
    }

    /** 单语言资源加载结果。 */
    private record LanguageResources(Map<String, String> messages,
                                     Map<String, String> origins,
                                     Map<String, Map<String, String>> ownerMessages,
                                     Map<String, Set<String>> ownerKeys,
                                     Map<String, Map<String, Set<Integer>>> ownerArguments) {
    }
}
