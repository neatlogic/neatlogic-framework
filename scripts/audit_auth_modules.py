#!/usr/bin/env python3
"""审计真实模块生产权限标签的显式归属，不修改任何源码。"""
import argparse
import collections
import json
from pathlib import Path
import re
import sys

# 此表来自已确认模块边界；新增模块须人工核对归属，禁止从包名或权限组猜测。
MODULE_OWNERS = {
    'neatlogic-framework': 'framework', 'neatlogic-ai-base': 'ai',
    'neatlogic-alert-base': 'alert', 'neatlogic-alert-commercial': 'alert',
    'neatlogic-approval-base': 'approval', 'neatlogic-autoexec-base': 'autoexec',
    'neatlogic-autoexec-commercial': 'autoexec', 'neatlogic-change': 'change',
    'neatlogic-cmdb-base': 'cmdb', 'neatlogic-cmdb-commercial': 'cmdb',
    'neatlogic-cmdb-transfer': 'cmdb', 'neatlogic-codehub-base': 'codehub',
    'neatlogic-credit': 'credit', 'neatlogic-dashboard-base': 'dashboard',
    'neatlogic-deploy-base': 'deploy', 'neatlogic-diagram': 'diagram',
    'neatlogic-dr-base': 'dr', 'neatlogic-edu': 'edu', 'neatlogic-eoa-base': 'eoa',
    'neatlogic-event': 'event', 'neatlogic-fdbank': 'fdbank',
    'neatlogic-fileservice-base': 'fileservice', 'neatlogic-haday': 'haday',
    'neatlogic-informant': 'informant', 'neatlogic-informant-base': 'informant',
    'neatlogic-inspect-base': 'inspect', 'neatlogic-itsm-base': 'process',
    'neatlogic-itsm-commercial': 'process', 'neatlogic-knowledge': 'knowledge',
    'neatlogic-knowledge-base': 'knowledge', 'neatlogic-master': 'master',
    'neatlogic-monitor-base': 'monitor', 'neatlogic-oncall': 'oncall',
    'neatlogic-pbc': 'pbc', 'neatlogic-rdm-base': 'rdm', 'neatlogic-report': 'report',
    'neatlogic-resourcepool': 'resourcepool', 'neatlogic-suzhoubank': 'suzhoubank',
    'neatlogic-tagent-base': 'tagent', 'neatlogic-tenant-commercial': 'tenant',
    'neatlogic-tlcbbank': 'tlcbbank',
}
EXCLUDED = {'neatlogic-itom-all', 'neatlogic-diagnosis', 'neatlogic-diagnosis-base'}
TOKEN = re.compile(r'//[^\n]*|/\*[\s\S]*?\*/|"(?:\\.|[^"\\])*"|\b\w+\b|[^\s]')
BASES = {'neatlogic.framework.auth.core.AuthBase', 'neatlogic.framework.auth.core.AuthCSBase'}


def tokens(source):
    """忽略注释但保留字符串为单独词元，避免注释中的示例干扰审计。"""
    return [m.group() for m in TOKEN.finditer(source) if not m.group().startswith(('//', '/*'))]


def classes_in_file(path, workspace):
    """提取类的父类和直接注解，并记录各自类体，不把父类声明当作子类声明。"""
    source = path.read_text(encoding='utf-8')
    ts = tokens(source)
    package_match = re.search(r'\bpackage\s+([\w.]+)\s*;', source)
    if not package_match:
        return []
    package = package_match.group(1)
    imports = {name.rsplit('.', 1)[-1]: name for name in re.findall(r'\bimport\s+([\w.]+)\s*;', source)}
    wildcard_imports = re.findall(r'\bimport\s+([\w.]+)\.\*\s*;', source)
    braces = {}
    stack = []
    for i, token in enumerate(ts):
        if token == '{': stack.append(i)
        elif token == '}' and stack: braces[stack.pop()] = i
    found = []
    for i, token in enumerate(ts):
        if token != 'class' or i == 0 or ts[i - 1] == '.':
            continue
        name = ts[i + 1]
        try: opening = ts.index('{', i + 2)
        except ValueError: continue
        header = ts[i + 2:opening]
        if 'extends' not in header:
            continue
        index = header.index('extends') + 1
        parent_parts = [header[index]]
        while index + 2 < len(header) and header[index + 1] == '.':
            parent_parts.extend(header[index + 1:index + 3])
            index += 2
        parent = ''.join(parent_parts)
        if '.' not in parent:
            if parent in ('AuthBase', 'AuthCSBase'):
                parent = 'neatlogic.framework.auth.core.' + parent
            else:
                parent = imports.get(parent, package + '.' + parent)
        start = i - 1
        while start >= 0 and ts[start] not in (';', '{', '}'): start -= 1
        declaration = ts[start + 1:i]
        declaration_text = ' '.join(declaration)
        annotation = re.search(r'@\s*(?:[\w]+\s*\.\s*)*AuthModule\s*\(\s*(?:value\s*=\s*)?("(?:\\.|[^"\\])*")\s*\)', declaration_text)
        module = None
        if annotation:
            try: module = json.loads(annotation.group(1)).strip()
            except ValueError: pass
        enclosing = [c for c in found if c['_open'] < i < c['_close']]
        full_name = package + '.' + '.'.join([c['_name'] for c in enclosing] + [name])
        body = ' '.join(ts[opening + 1:braces.get(opening, len(ts))])
        found.append({'class': full_name, 'path': str(path.relative_to(workspace)),
                      'repo': path.relative_to(workspace).parts[0], 'module': module,
                      'abstract': 'abstract' in declaration, 'parent': parent,
                      '_parent_candidates': [prefix + '.' + header[header.index('extends') + 1] for prefix in wildcard_imports],
                      'override': bool(re.search(r'\b(?:public|protected|private)\s+(?:(?:final|static)\s+)*String\s+getAuthModule\s*\(', body)),
                      '_name': name, '_open': opening, '_close': braces.get(opening, len(ts))})
    return found


def audit(workspace):
    """只扫描一级真实仓库；排除聚合副本、符号链接和废弃 Diagnosis。"""
    classes = {}
    seen = set()
    for repo in sorted(workspace.iterdir()):
        if not repo.is_dir() or repo.is_symlink() or repo.name in EXCLUDED:
            continue
        main = repo / 'src/main/java'
        if not main.is_dir(): continue
        for path in sorted(main.rglob('*.java')):
            if path.is_symlink() or path.resolve() in seen: continue
            seen.add(path.resolve())
            for item in classes_in_file(path, workspace): classes[item['class']] = item
    known = set(BASES)
    while True:
        derived = {name for name, item in classes.items()
                   if item['parent'] in known or any(parent in known for parent in item['_parent_candidates'])}
        if derived <= known: break
        known |= derived
    result = []
    for name in sorted(known - BASES):
        item = classes.get(name)
        if not item or item['abstract']: continue
        row = {key: item[key] for key in ('path', 'class', 'module', 'repo')}
        errors = []
        if not item['module']: errors.append('缺少具体类的直接非空 AuthModule 字符串声明')
        expected = MODULE_OWNERS.get(item['repo'])
        if expected is None: errors.append('仓库尚未登记已确认模块归属')
        elif item['module'] and item['module'] != expected: errors.append('声明归属与已确认模块不一致，期望 ' + expected)
        if item['override']: errors.append('仍覆盖 getAuthModule，必须迁移至注解')
        row['errors'] = errors
        result.append(row)
    return result


def main():
    """打印统计及错误；可将完整迁移清单写到调用方指定的报告路径。"""
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--workspace', type=Path, default=Path(__file__).resolve().parents[2])
    parser.add_argument('--json', type=Path, help='输出逐标签 JSON 审计清单')
    args = parser.parse_args()
    rows = audit(args.workspace.resolve())
    if args.json:
        args.json.write_text(json.dumps(rows, ensure_ascii=False, indent=2) + '\n', encoding='utf-8')
    counts = collections.Counter(row['module'] or '<未声明>' for row in rows)
    for module, count in sorted(counts.items()): print(f'{module}: {count}')
    errors = [(row, error) for row in rows for error in row['errors']]
    for row, error in errors: print(f"ERROR {row['path']} [{row['class']}]: {error}", file=sys.stderr)
    print(f'生产权限标签: {len(rows)}，仓库: {len({row["repo"] for row in rows})}，错误: {len(errors)}')
    return 1 if errors or not rows else 0


if __name__ == '__main__':
    sys.exit(main())
