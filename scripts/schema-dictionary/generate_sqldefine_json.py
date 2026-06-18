#!/usr/bin/env python3
import argparse
import base64
import json
import os
import re
import shutil
import subprocess
import sys
import xml.etree.ElementTree as ET
from collections import defaultdict
from pathlib import Path


MODULE_TAG = "{http://neatlogic.com/schema/neatlogic}module"

PREFIX_ALIASES = {
    "processtask": "process",
    "reportinstance": "report",
}

FRAMEWORK_PREFIXES = (
    "user",
    "team",
    "role",
    "worktime",
    "region",
    "matrix",
    "notify",
    "schedule",
    "system",
)

COMMON_LABELS = {
    "id": "主键ID",
    "name": "唯一标识",
    "label": "名称",
    "description": "描述",
    "type": "类型",
    "status": "状态",
    "is_active": "是否激活",
    "is_default": "是否默认",
    "sort": "排序",
    "config": "配置",
    "fcu": "创建人",
    "fcd": "创建时间",
    "lcu": "修改人",
    "lcd": "修改时间",
    "uuid": "唯一UUID",
    "user_uuid": "用户UUID",
    "team_uuid": "分组UUID",
    "role_uuid": "角色UUID",
}

DOMAIN_LABELS = {
    "ai": "AI",
    "alert": "告警",
    "autoexec": "自动化",
    "change": "变更",
    "cmdb": "配置管理",
    "codehub": "代码仓库",
    "dashboard": "仪表板",
    "deploy": "发布",
    "diagram": "图形",
    "dr": "容灾",
    "event": "事件",
    "fileservice": "文件服务",
    "framework": "框架",
    "informant": "数据采集",
    "inspect": "巡检",
    "knowledge": "知识库",
    "monitor": "监控",
    "pbc": "PBC",
    "process": "流程",
    "rdm": "研发管理",
    "report": "报表",
    "resourcepool": "资源池",
    "tagent": "Tagent",
    "tenant": "租户",
    "tlcbbank": "泰隆银行",
}


def run_mysql(args, sql):
    cmd = [
        args.mysql_bin,
        f"--host={args.host}",
        f"--port={args.port}",
        f"--user={args.user}",
        "--batch",
        "--raw",
        "--skip-column-names",
        "--default-character-set=utf8mb4",
        "--execute",
        sql,
    ]
    env = os.environ.copy()
    if args.password:
        env["MYSQL_PWD"] = args.password
    proc = subprocess.run(cmd, check=False, text=True, capture_output=True, env=env)
    if proc.returncode != 0:
        sys.stderr.write(proc.stderr)
        raise SystemExit(proc.returncode)
    return [line.split("\t") for line in proc.stdout.splitlines() if line]


def decode_b64(value):
    if not value or value == "NULL":
        return ""
    return base64.b64decode(value).decode("utf-8", errors="replace")


def find_modules(workspace):
    module_by_id = {}
    for context in sorted(workspace.glob("neatlogic-*/src/main/java/**/*-servlet-context.xml")):
        if "/target/" in str(context):
            continue
        try:
            tree = ET.parse(context)
        except ET.ParseError:
            continue
        module_node = tree.getroot().find(f".//{MODULE_TAG}")
        if module_node is None:
            continue
        module_id = module_node.attrib.get("id")
        if not module_id:
            continue
        project = context.relative_to(workspace).parts[0]
        # Keep the first module id mapping stable. A duplicated id means the
        # module declaration itself is ambiguous, so the generator should not
        # guess a later project.
        module_by_id.setdefault(module_id, workspace / project)
    return module_by_id


def load_tables(args):
    table_sql = f"""
        SELECT TABLE_NAME, REPLACE(TO_BASE64(IFNULL(TABLE_COMMENT, '')), '\n', '')
        FROM information_schema.TABLES
        WHERE TABLE_SCHEMA = '{escape_sql(args.database)}'
        ORDER BY TABLE_NAME
    """
    column_sql = f"""
        SELECT TABLE_NAME, COLUMN_NAME, COLUMN_TYPE, IS_NULLABLE, COLUMN_KEY,
               REPLACE(TO_BASE64(IFNULL(COLUMN_COMMENT, '')), '\n', ''), ORDINAL_POSITION
        FROM information_schema.COLUMNS
        WHERE TABLE_SCHEMA = '{escape_sql(args.database)}'
        ORDER BY TABLE_NAME, ORDINAL_POSITION
    """
    index_sql = f"""
        SELECT TABLE_NAME, INDEX_NAME, NON_UNIQUE, SEQ_IN_INDEX, COLUMN_NAME,
               IFNULL(INDEX_TYPE, ''), REPLACE(TO_BASE64(IFNULL(INDEX_COMMENT, '')), '\n', '')
        FROM information_schema.STATISTICS
        WHERE TABLE_SCHEMA = '{escape_sql(args.database)}'
        ORDER BY TABLE_NAME, INDEX_NAME, SEQ_IN_INDEX
    """
    tables = {}
    for table_name, comment_b64 in run_mysql(args, table_sql):
        tables[table_name] = {
            "name": table_name,
            "sourceComment": decode_b64(comment_b64),
            "columns": [],
        }
    for row in run_mysql(args, column_sql):
        table_name, column_name, column_type, nullable, key, comment_b64, _ = row
        if table_name not in tables:
            continue
        tables[table_name]["columns"].append(
            {
                "name": column_name,
                "type": column_type,
                "nullable": nullable == "YES",
                "key": key,
                "sourceComment": decode_b64(comment_b64),
            }
        )
    indexes_by_table = defaultdict(dict)
    for row in run_mysql(args, index_sql):
        table_name, index_name, non_unique, seq_in_index, column_name, index_type, comment_b64 = row
        if table_name not in tables:
            continue
        index = indexes_by_table[table_name].setdefault(
            index_name,
            {
                "name": index_name,
                "type": index_type or "BTREE",
                "unique": non_unique == "0",
                "primary": index_name == "PRIMARY",
                "columns": [],
                "sourceComment": decode_b64(comment_b64),
            },
        )
        index["columns"].append((int(seq_in_index), column_name))
    for table_name, index_map in indexes_by_table.items():
        tables[table_name]["indexes"] = []
        for index in index_map.values():
            index["columns"] = [column for _, column in sorted(index["columns"])]
            tables[table_name]["indexes"].append(index)
        tables[table_name]["indexes"].sort(key=lambda item: (not item["primary"], item["name"]))
    for table in tables.values():
        table.setdefault("indexes", [])
    return tables


def escape_sql(value):
    return value.replace("\\", "\\\\").replace("'", "\\'")


def assign_module(table_name, module_ids):
    for prefix, module_id in PREFIX_ALIASES.items():
        if table_name == prefix or table_name.startswith(prefix + "_"):
            return module_id
    for module_id in sorted(module_ids, key=len, reverse=True):
        if table_name == module_id or table_name.startswith(module_id + "_"):
            return module_id
    for prefix in FRAMEWORK_PREFIXES:
        if table_name.startswith(prefix):
            return "framework"
    return None


def clean_comment(comment):
    comment = (comment or "").strip()
    comment = re.sub(r"\s+", " ", comment)
    return comment


def words_from_identifier(identifier):
    return [word for word in re.split(r"[_\W]+", identifier.lower()) if word]


def label_from_name(name, source_comment, module_id, is_table=False):
    comment = clean_comment(source_comment)
    if comment and not looks_garbled(comment):
        short = re.split(r"[，。,.;；\s]", comment, maxsplit=1)[0]
        if 1 <= len(short) <= 30:
            return short
    if not is_table and name in COMMON_LABELS:
        return COMMON_LABELS[name]
    domain = DOMAIN_LABELS.get(module_id, module_id)
    words = words_from_identifier(name)
    if is_table:
        meaningful = [w for w in words if w != module_id]
        if not meaningful:
            return f"{domain}表"
        return f"{domain}{'_'.join(meaningful)}表"
    return name


def looks_garbled(comment):
    if not comment:
        return False
    suspicious = sum(1 for ch in comment if ch in "�鑻绯枃€")
    return suspicious >= max(2, len(comment) // 4)


def table_description(table, module_id, label):
    comment = clean_comment(table["sourceComment"])
    domain = DOMAIN_LABELS.get(module_id, module_id)
    if comment and not looks_garbled(comment):
        base = comment
    else:
        base = f"{label}，属于{domain}模块的数据表。"
    field_names = {column["name"] for column in table["columns"]}
    usages = []
    if "status" in field_names:
        usages.append("状态跟踪")
    if "is_active" in field_names:
        usages.append("启用状态筛选")
    if {"fcd", "lcd"} & field_names or {"create_time", "update_time"} & field_names:
        usages.append("时间维度统计")
    if any(name.endswith("_id") for name in field_names):
        usages.append("关联查询")
    if usages:
        return f"{base} 可用于{domain}相关的{ '、'.join(usages) }等报表查询场景。"
    return f"{base} 可用于{domain}相关数据的查询、筛选和报表展示。"


def field_description(table_label, column):
    source_comment = clean_comment(column["sourceComment"])
    name = column["name"]
    if source_comment and not looks_garbled(source_comment):
        base = source_comment
    elif name in COMMON_LABELS:
        base = COMMON_LABELS[name]
    elif name.endswith("_id"):
        base = f"{name[:-3]}关联ID"
    elif name.endswith("_uuid"):
        base = f"{name[:-5]}关联UUID"
    elif name.endswith("_time"):
        base = "时间字段"
    elif name.startswith("is_"):
        base = "布尔标记字段"
    else:
        base = f"{table_label}的{name}字段"

    key = column["key"]
    if key == "PRI":
        return f"{base}，作为当前表记录的主键或唯一定位字段。"
    if key == "UNI":
        return f"{base}，数据库中要求唯一，适合用于精确查询和去重判断。"
    if key == "MUL":
        return f"{base}，已建立普通索引，适合用于过滤、关联或排序查询。"
    return f"{base}。"


def index_description(index):
    comment = clean_comment(index["sourceComment"])
    if comment and not looks_garbled(comment):
        return comment
    column_text = "、".join(index["columns"])
    if index["primary"]:
        return f"主键索引，覆盖 {column_text} 字段，适合用于唯一定位记录和主表关联查询。"
    if index["unique"]:
        return f"唯一索引，覆盖 {column_text} 字段，适合用于精确检索、唯一性判断和去重查询。"
    return f"普通索引，覆盖 {column_text} 字段，适合用于过滤、关联或排序查询。"


def build_table_json(table, module_id):
    label = label_from_name(table["name"], table["sourceComment"], module_id, is_table=True)
    return {
        "moduleId": module_id,
        "name": table["name"],
        "label": label,
        "description": table_description(table, module_id, label),
        "fields": [
            {
                "name": column["name"],
                "type": column["type"],
                "description": field_description(label, column),
            }
            for column in table["columns"]
        ],
        "indexes": [
            {
                "name": index["name"],
                "type": index["type"],
                "unique": index["unique"],
                "primary": index["primary"],
                "columns": index["columns"],
                "description": index_description(index),
            }
            for index in table["indexes"]
        ],
    }


def prepare_output_dir(path, force):
    if path.exists():
        if not force:
            raise SystemExit(f"Refusing to overwrite existing directory without --force: {path}")
        shutil.rmtree(path)
    path.mkdir(parents=True, exist_ok=True)


def write_json(path, data):
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(
        json.dumps(data, ensure_ascii=False, indent=2, sort_keys=False) + "\n",
        encoding="utf-8",
    )


def main():
    parser = argparse.ArgumentParser(description="Generate packaged sqldefine JSON files from database metadata.")
    parser.add_argument("--workspace", default="/Users/chenqiwei/idea_project/codedriver")
    parser.add_argument("--database", default="neatlogic_tlcb35")
    parser.add_argument("--mysql-bin", default="/usr/local/mysql/bin/mysql")
    parser.add_argument("--host", default="127.0.0.1")
    parser.add_argument("--port", default="3306")
    parser.add_argument("--user", default="root")
    parser.add_argument("--password", default=os.environ.get("MYSQL_PWD", ""))
    parser.add_argument("--force", action="store_true")
    args = parser.parse_args()

    workspace = Path(args.workspace).resolve()
    module_by_id = find_modules(workspace)
    if "framework" not in module_by_id:
        raise SystemExit("Cannot find framework module from *-servlet-context.xml")

    tables = load_tables(args)
    generated_by_module = defaultdict(list)
    skipped = []

    for table_name, table in sorted(tables.items()):
        module_id = assign_module(table_name, module_by_id.keys())
        if not module_id or module_id not in module_by_id:
            skipped.append(table_name)
            continue
        table_json = build_table_json(table, module_id)
        module_project = module_by_id[module_id]
        output_root = module_project / "src/main/resources/neatlogic/resources" / module_id / "sqldefine"
        table_dir = output_root / "tables"
        if table_dir.exists() and not args.force:
            raise SystemExit(f"Refusing to overwrite existing sqldefine tables without --force: {table_dir}")
        generated_by_module[module_id].append((table_name, table_json, output_root))

    for module_id, items in generated_by_module.items():
        output_root = items[0][2]
        prepare_output_dir(output_root / "tables", args.force)
        index_tables = []
        for table_name, table_json, _ in items:
            write_json(output_root / "tables" / f"{table_name}.json", table_json)
            index_tables.append(
                {
                    "name": table_json["name"],
                    "label": table_json["label"],
                    "description": table_json["description"],
                }
            )
        write_json(
            output_root / "index.json",
            {
                "moduleId": module_id,
                "tables": sorted(index_tables, key=lambda item: item["name"]),
            },
        )

    print(f"modules={len(generated_by_module)}")
    print(f"generatedTables={sum(len(items) for items in generated_by_module.values())}")
    print(f"skippedTables={len(skipped)}")
    if skipped:
        print("skippedSample=" + ",".join(skipped[:50]))


if __name__ == "__main__":
    main()
