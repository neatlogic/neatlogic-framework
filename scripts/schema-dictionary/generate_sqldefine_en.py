#!/usr/bin/env python3
import argparse
import copy
import json
import re
from pathlib import Path


TRANSLATABLE_KEYS = {"label", "description"}
FORBIDDEN_KEYS = {"labelKey", "labelEn", "descriptionEn"}
FORBIDDEN_PHRASES = (
    "stores data used by",
    "data type:",
)
FORBIDDEN_PATTERNS = (
    re.compile(r"\bstores the .+ value for records\b", re.IGNORECASE),
)
CHINESE_PATTERN = re.compile(r"[\u3400-\u9fff]")
CHINESE_PUNCTUATION_PATTERN = re.compile(r"[。；，：（）【】、]")
ENGLISH_WORD_PATTERN = re.compile(r"[A-Za-z][A-Za-z-]*")
TRANSLATION_FILE = Path(__file__).with_name("sqldefine_en_translations.json")
INDEX_TEMPLATES = (
    (
        re.compile(r"^主键索引，覆盖 (.+) 字段，适合用于唯一定位记录和主表关联查询。$"),
        "Primary key index covering the {columns} {field_word}, suitable for uniquely identifying records and queries that join the primary table.",
    ),
    (
        re.compile(r"^主键索引，覆盖 (.+) 字段，用于记录唯一定位及主表关联查询。$"),
        "Primary key index covering the {columns} {field_word}, used to uniquely identify records and query associations with the primary table.",
    ),
    (
        re.compile(r"^唯一索引，覆盖 (.+) 字段，适合用于精确检索、唯一性判断和去重查询。$"),
        "Unique index covering the {columns} {field_word}, suitable for exact-match retrieval, uniqueness checks, and duplicate detection.",
    ),
    (
        re.compile(r"^普通索引，覆盖 (.+) 字段，适合用于过滤、关联或排序查询。$"),
        "Standard index covering the {columns} {field_word}, suitable for filtering, join, and sort queries.",
    ),
)
FIELD_TEMPLATES = (
    (
        re.compile(r"^(.+)，作为当前表记录的主键或唯一定位字段。$"),
        "{prefix}, used as the primary key or unique identifier for records in this table.",
    ),
    (
        re.compile(r"^(.+)，已建立普通索引，适合用于过滤、关联或排序查询。$"),
        "{prefix}. A standard index is defined on this field, making it suitable for filtering, join, and sort queries.",
    ),
    (
        re.compile(r"^(.+)，数据库中要求唯一，适合用于精确查询和去重判断。$"),
        "{prefix}. This value must be unique in the database and is suitable for exact-match queries and duplicate detection.",
    ),
)


def load_translations():
    """加载按中文原文维护且经过审核的英文翻译记忆。"""
    data = json.loads(TRANSLATION_FILE.read_text(encoding="utf-8"))
    descriptions_by_path = data.get("descriptionsByPath")
    labels = data.get("labels")
    fragments = data.get("fragments")
    if not isinstance(descriptions_by_path, dict) or not isinstance(labels, dict) or not isinstance(fragments, dict):
        raise SystemExit(f"invalid translation file: {TRANSLATION_FILE}")
    return descriptions_by_path, labels, fragments


def translation_path_key(source_path, json_path):
    """生成不依赖工作区绝对路径的翻译位置键。"""
    path_parts = source_path.parts
    repository_index = next(
        (index for index, part in enumerate(path_parts) if part.startswith("neatlogic-")),
        None,
    )
    relative_path = Path(*path_parts[repository_index:]) if repository_index is not None else source_path
    return f"{relative_path.as_posix()}#{json_path}"


def resolve_translation(source, source_path, json_path, descriptions_by_path, labels, fragments, key):
    """优先读取明确译文，并仅对完整命中的标准句式应用严格模板。"""
    if key == "label":
        target = labels.get(source)
        if isinstance(target, str) and target.strip():
            return target
        return None
    path_key = translation_path_key(source_path, json_path)
    path_translation = descriptions_by_path.get(path_key)
    if isinstance(path_translation, dict):
        if path_translation.get("source") != source:
            raise SystemExit(
                f"translation source mismatch: file={source_path}, jsonPath={json_path}, source={source!r}"
            )
        target = path_translation.get("target")
        if isinstance(target, str) and target.strip():
            return target
    for pattern, template in INDEX_TEMPLATES:
        match = pattern.match(source)
        if match:
            columns = match.group(1).replace("、", ", ")
            column_count = len([column for column in columns.split(",") if column.strip()])
            return template.format(
                columns=columns,
                field_word="field" if column_count == 1 else "fields",
            )
    for pattern, template in FIELD_TEMPLATES:
        match = pattern.match(source)
        if match:
            prefix = fragments.get(match.group(1))
            if isinstance(prefix, str) and prefix.strip():
                return template.format(prefix=prefix)
    return None


def remove_forbidden_keys(value):
    """移除独立语言文件中禁止出现的混合语言字段。"""
    if isinstance(value, dict):
        for key in FORBIDDEN_KEYS:
            value.pop(key, None)
        for child in value.values():
            remove_forbidden_keys(child)
    elif isinstance(value, list):
        for child in value:
            remove_forbidden_keys(child)


def translate_value(value, source_path, descriptions_by_path, labels, fragments, json_path=""):
    """按 JSON 路径逐项翻译文案，缺少明确译文时立即终止。"""
    if isinstance(value, dict):
        for key, child in value.items():
            child_path = f"{json_path}.{key}" if json_path else key
            if key in TRANSLATABLE_KEYS and isinstance(child, str):
                target = resolve_translation(
                    child, source_path, child_path, descriptions_by_path, labels, fragments, key
                )
                if not isinstance(target, str) or not target.strip():
                    raise SystemExit(
                        f"missing translation: file={source_path}, jsonPath={child_path}, source={child!r}"
                    )
                value[key] = target
            else:
                translate_value(child, source_path, descriptions_by_path, labels, fragments, child_path)
    elif isinstance(value, list):
        for index, child in enumerate(value):
            translate_value(child, source_path, descriptions_by_path, labels, fragments, f"{json_path}[{index}]")


def translate_document(source, source_path, descriptions_by_path, labels, fragments):
    """从同位置中文原文生成结构完全一致的英文定义。"""
    target = copy.deepcopy(source)
    remove_forbidden_keys(target)
    translate_value(target, source_path, descriptions_by_path, labels, fragments)
    return target


def english_path(source_path):
    """返回中文定义对应的 -en.json 路径。"""
    return source_path.with_name(f"{source_path.stem}-en.json")


def list_source_files(workspace):
    """列出工作区中全部无语言后缀的 SQL 定义文件。"""
    pattern = "neatlogic-*/src/main/resources/neatlogic/resources/*/sqldefine/**/*.json"
    return sorted(path for path in workspace.glob(pattern) if not path.name.endswith("-en.json"))


def write_english_files(workspace, descriptions_by_path, labels, fragments):
    """使用翻译记忆重建全部英文镜像文件。"""
    source_files = list_source_files(workspace)
    for source_path in source_files:
        source = json.loads(source_path.read_text(encoding="utf-8"))
        target = translate_document(source, source_path, descriptions_by_path, labels, fragments)
        english_path(source_path).write_text(
            json.dumps(target, ensure_ascii=False, indent=2) + "\n",
            encoding="utf-8",
        )
    return len(source_files)


def structural_value(value):
    """移除可翻译文案后生成用于结构比较的值。"""
    if isinstance(value, dict):
        return {
            key: structural_value(child)
            for key, child in value.items()
            if key not in TRANSLATABLE_KEYS and key not in FORBIDDEN_KEYS
        }
    if isinstance(value, list):
        return [structural_value(child) for child in value]
    return value


def has_title_case_sentence(value):
    """识别误将完整说明写成标题格式的明显情形。"""
    words = ENGLISH_WORD_PATTERN.findall(value)
    capitalized_words = [
        word for word in words[1:]
        if len(word) > 2 and word[0].isupper() and not word.isupper()
    ]
    return len(capitalized_words) >= 3 and len(capitalized_words) / max(1, len(words)) > 0.25


def validate_text_pair(source, target, path, descriptions_by_path, labels, fragments, errors, json_path=""):
    """校验每个英文文案均直接对应同位置中文原文。"""
    position_count = 0
    if isinstance(source, dict) and isinstance(target, dict):
        for key, source_child in source.items():
            if key in FORBIDDEN_KEYS:
                continue
            child_path = f"{json_path}.{key}" if json_path else key
            if key in TRANSLATABLE_KEYS and isinstance(source_child, str):
                position_count += 1
                target_child = target.get(key)
                expected = resolve_translation(
                    source_child, path, child_path, descriptions_by_path, labels, fragments, key
                )
                if not isinstance(expected, str) or not expected.strip():
                    errors.append(
                        f"missing translation: file={path}, jsonPath={child_path}, source={source_child!r}"
                    )
                elif target_child != expected:
                    errors.append(f"translation mismatch: {path}#{child_path}")
                if not isinstance(target_child, str) or not target_child.strip():
                    errors.append(f"empty English text: {path}#{child_path}")
                elif CHINESE_PATTERN.search(target_child):
                    errors.append(f"Chinese text remains: {path}#{child_path}")
                elif CHINESE_PUNCTUATION_PATTERN.search(target_child):
                    errors.append(f"Chinese punctuation remains: {path}#{child_path}")
                elif key == "description" and has_title_case_sentence(target_child):
                    errors.append(f"description is not Sentence case: {path}#{child_path}: {target_child!r}")
                elif any(phrase in target_child.lower() for phrase in FORBIDDEN_PHRASES) or any(
                    pattern.search(target_child) for pattern in FORBIDDEN_PATTERNS
                ):
                    errors.append(f"forbidden generated phrase: {path}#{child_path}: {target_child!r}")
            elif key in target:
                position_count += validate_text_pair(
                    source_child, target[key], path, descriptions_by_path, labels, fragments, errors, child_path
                )
    elif isinstance(source, list) and isinstance(target, list):
        for index, (source_child, target_child) in enumerate(zip(source, target)):
            position_count += validate_text_pair(
                source_child, target_child, path, descriptions_by_path, labels, fragments, errors, f"{json_path}[{index}]"
            )
    return position_count


def collect_forbidden_keys(value, path, errors, json_path=""):
    """检查英文文件是否混入语言字段。"""
    if isinstance(value, dict):
        for key, child in value.items():
            child_path = f"{json_path}.{key}" if json_path else key
            if key in FORBIDDEN_KEYS:
                errors.append(f"forbidden key: {path}#{child_path}")
            collect_forbidden_keys(child, path, errors, child_path)
    elif isinstance(value, list):
        for index, child in enumerate(value):
            collect_forbidden_keys(child, path, errors, f"{json_path}[{index}]")


def validate_english_files(workspace, descriptions_by_path, labels, fragments):
    """校验文件配对、技术结构、原文覆盖及英文文案。"""
    errors = []
    position_count = 0
    source_files = list_source_files(workspace)
    source_set = set(source_files)
    english_files = set(
        workspace.glob("neatlogic-*/src/main/resources/neatlogic/resources/*/sqldefine/**/*-en.json")
    )
    expected_english_set = {english_path(path) for path in source_files}
    for missing in sorted(expected_english_set - english_files):
        errors.append(f"missing English file: {missing}")
    for orphan in sorted(english_files - expected_english_set):
        errors.append(f"orphan English file: {orphan}")
    for source_path in sorted(source_set):
        target_path = english_path(source_path)
        if not target_path.exists():
            continue
        source = json.loads(source_path.read_text(encoding="utf-8"))
        target = json.loads(target_path.read_text(encoding="utf-8"))
        if structural_value(source) != structural_value(target):
            errors.append(f"structure mismatch: {target_path}")
        collect_forbidden_keys(target, target_path, errors)
        position_count += validate_text_pair(
            source, target, source_path, descriptions_by_path, labels, fragments, errors
        )
    if errors:
        raise SystemExit("\n".join(errors))
    return len(source_files), len(english_files), position_count


def main():
    """处理英文 SQL 定义的生成和校验命令。"""
    parser = argparse.ArgumentParser(description="Generate and validate English sqldefine JSON mirrors.")
    parser.add_argument("--workspace", default=str(Path(__file__).resolve().parents[3]))
    parser.add_argument("--write", action="store_true")
    parser.add_argument("--check", action="store_true")
    args = parser.parse_args()
    workspace = Path(args.workspace).resolve()
    descriptions_by_path, labels, fragments = load_translations()
    if not args.write and not args.check:
        parser.error("specify --write or --check")
    if args.write:
        print(f"generatedEnglishFiles={write_english_files(workspace, descriptions_by_path, labels, fragments)}")
    if args.check:
        source_count, english_count, position_count = validate_english_files(
            workspace, descriptions_by_path, labels, fragments
        )
        print(f"sourceFiles={source_count}")
        print(f"englishFiles={english_count}")
        print(f"translatedPositions={position_count}")


if __name__ == "__main__":
    main()
