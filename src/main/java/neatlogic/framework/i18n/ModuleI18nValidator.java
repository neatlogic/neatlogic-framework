package neatlogic.framework.i18n;

import com.fasterxml.jackson.databind.JsonNode;
import neatlogic.framework.exception.module.ModuleInitRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * 校验模块语言资源的结构、模板和多语言镜像关系，并诊断 key 排序。
 */
final class ModuleI18nValidator {
    private static final Logger logger = LoggerFactory.getLogger(ModuleI18nValidator.class);

    /** 将嵌套 JSON 展开为点号 key，并执行单文件校验。 */
    Map<String, String> flatten(JsonNode root, String origin, String language, String owner) {
        if (root == null || !root.isObject()) {
            throw new ModuleInitRuntimeException("模块语言资源根节点必须是对象，resource: " + origin);
        }
        Map<String, String> result = new LinkedHashMap<>();
        flattenNode(root, "", result, origin, language, owner);
        return result;
    }

    /** 检查每个模块的语言文件与 key、参数结构完全一致。 */
    void validateLanguageStructure(Map<String, Map<String, Set<String>>> ownerKeyMap,
                                   Map<String, Map<String, Map<String, Set<Integer>>>> ownerArgumentMap) {
        Map<String, Set<String>> chineseOwnerKeys = ownerKeyMap.get("zh");
        Map<String, Set<String>> englishOwnerKeys = ownerKeyMap.get("en");
        Set<String> owners = new HashSet<>(chineseOwnerKeys.keySet());
        owners.addAll(englishOwnerKeys.keySet());
        for (String owner : owners) {
            Set<String> chineseKeys = chineseOwnerKeys.get(owner);
            Set<String> englishKeys = englishOwnerKeys.get(owner);
            if (chineseKeys == null || englishKeys == null) {
                throw new ModuleInitRuntimeException("模块缺少中英文语言镜像，owner: " + owner
                        + ", missing: " + (chineseKeys == null ? "zh" : "en"));
            }
            if (!chineseKeys.equals(englishKeys)) {
                Set<String> onlyChinese = new HashSet<>(chineseKeys);
                onlyChinese.removeAll(englishKeys);
                Set<String> onlyEnglish = new HashSet<>(englishKeys);
                onlyEnglish.removeAll(chineseKeys);
                throw new ModuleInitRuntimeException("模块中英文 key 不一致，owner: " + owner
                        + ", onlyZh: " + onlyChinese + ", onlyEn: " + onlyEnglish);
            }
            for (String key : chineseKeys) {
                Set<Integer> chineseArguments = ownerArgumentMap.get("zh").get(owner).get(key);
                Set<Integer> englishArguments = ownerArgumentMap.get("en").get(owner).get(key);
                if (!chineseArguments.equals(englishArguments)) {
                    throw new ModuleInitRuntimeException("模块中英文模板参数不一致，owner: " + owner + ", key: " + key
                            + ", zh: " + chineseArguments + ", en: " + englishArguments);
                }
            }
        }
    }

    /** 递归展开 JSON，并诊断逐层排序、检查叶子类型和消息模板。 */
    private void flattenNode(JsonNode node, String prefix, Map<String, String> result,
                             String origin, String language, String owner) {
        String previousName = null;
        var fields = node.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> field = fields.next();
            if (field.getKey().isEmpty() || field.getKey().indexOf('.') >= 0) {
                throw new ModuleInitRuntimeException("模块语言资源字段名不能为空或包含点号，resource: " + origin
                        + ", node: " + (prefix.isEmpty() ? "<root>" : prefix) + ", field: " + field.getKey());
            }
            if (previousName != null && previousName.compareTo(field.getKey()) > 0) {
                logger.warn("模块语言资源 key 未按字母升序排列，resource: {}, node: {}, previous: {}, current: {}",
                        origin, prefix.isEmpty() ? "<root>" : prefix, previousName, field.getKey());
            }
            previousName = field.getKey();
            String key = prefix.isEmpty() ? field.getKey() : prefix + '.' + field.getKey();
            JsonNode value = field.getValue();
            if (value.isObject()) {
                flattenNode(value, key, result, origin, language, owner);
            } else if (value.isTextual()) {
                validateTemplate(value.textValue(), language, key, owner, origin);
                String previous = result.putIfAbsent(key, value.textValue());
                if (previous != null) {
                    throw new ModuleInitRuntimeException("模块语言资源展开后存在重复 key，resource: " + origin
                            + ", key: " + key);
                }
            } else {
                throw new ModuleInitRuntimeException("模块语言资源叶子节点必须是字符串，resource: " + origin
                        + ", key: " + key + ", type: " + value.getNodeType());
            }
        }
    }

    /** 校验模板语法和参数索引连续性。 */
    void validateTemplate(String message, String language, String key, String owner, String origin) {
        Set<Integer> indexes;
        try {
            indexes = I18nMessageTemplate.extractArgumentIndexes(message);
            if (indexes.isEmpty()) {
                return;
            }
            new I18nMessageTemplate(message, language);
        } catch (IllegalArgumentException ex) {
            throw new ModuleInitRuntimeException("模块语言资源模板非法，owner: " + owner + ", language: " + language
                    + ", key: " + key + ", resource: " + origin, ex);
        }
        int maximumIndex = indexes.stream().mapToInt(Integer::intValue).max().orElse(-1);
        for (int index = 0; index <= maximumIndex; index++) {
            if (!indexes.contains(index)) {
                throw new ModuleInitRuntimeException("模块语言资源模板参数索引不连续，owner: " + owner
                        + ", language: " + language + ", key: " + key + ", missingIndex: " + index
                        + ", resource: " + origin);
            }
        }
    }
}
