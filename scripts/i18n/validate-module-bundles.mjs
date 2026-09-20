import fs from 'node:fs';
import path from 'node:path';
import {fileURLToPath} from 'node:url';

const scriptDirectory = path.dirname(fileURLToPath(import.meta.url));
const workspace = process.argv[2] ?? path.resolve(scriptDirectory, '../../..');
const ignoredDirectories = new Set(['.git', '.idea', 'node_modules', 'target', 'dist', 'build', '__pycache__']);
const bundlePattern = /\/src\/main\/resources\/neatlogic\/resources\/([^/]+)\/i18n\/language_(zh|en)\.json$/;

/** 查找工作区内所有模块语言文件。 */
function walk(directory, result, predicate = file => bundlePattern.test(file.replaceAll(path.sep, '/'))) {
  for (const entry of fs.readdirSync(directory, {withFileTypes: true})) {
    if (ignoredDirectories.has(entry.name)) {
      continue;
    }
    const absolutePath = path.join(directory, entry.name);
    if (entry.isDirectory()) {
      walk(absolutePath, result, predicate);
    } else if (predicate(absolutePath)) {
      result.push(absolutePath);
    }
  }
}

/** 校验嵌套结构、字符串叶子和逐层字母排序，并返回扁平 key。 */
function flatten(value, file, prefix = '', result = new Map()) {
  if (value === null || typeof value !== 'object' || Array.isArray(value)) {
    throw new Error(`语言文件根节点或中间节点必须是对象: ${file}, key: ${prefix || '<root>'}`);
  }
  const keys = Object.keys(value);
  const sortedKeys = [...keys].sort();
  if (keys.some((key, index) => key !== sortedKeys[index])) {
    throw new Error(`语言文件 key 未按字母升序排列: ${file}, node: ${prefix || '<root>'}`);
  }
  for (const key of keys) {
    if (!key || key.includes('.')) {
      throw new Error(`语言文件字段名不能为空或包含点号: ${file}, node: ${prefix || '<root>'}, field: ${key}`);
    }
    const child = value[key];
    const childKey = prefix ? `${prefix}.${key}` : key;
    if (child !== null && typeof child === 'object' && !Array.isArray(child)) {
      flatten(child, file, childKey, result);
    } else if (typeof child === 'string') {
      if (result.has(childKey)) {
        throw new Error(`语言文件展开后存在重复 key: ${file}, key: ${childKey}`);
      }
      result.set(childKey, child);
    } else {
      throw new Error(`语言文件叶子节点必须是字符串: ${file}, key: ${childKey}`);
    }
  }
  return result;
}

/** 返回聚合构建中实际声明的模块目录；没有聚合 POM 时返回空集合。 */
function findReactorModuleDirectories() {
  const buildRootPom = path.join(workspace, 'neatlogic-build-root/pom.xml');
  if (!fs.existsSync(buildRootPom)) {
    return new Set();
  }
  const content = fs.readFileSync(buildRootPom, 'utf8');
  return new Set([...content.matchAll(/<module>\.\.\/([^<]+)<\/module>/g)]
    .map(match => path.join(workspace, match[1]))
    .filter(directory => fs.existsSync(directory)));
}

/** 检查包含模块 servlet 描述的 Reactor 模块是否完全遗漏语言资源。 */
function validateModuleCoverage(bundles) {
  const moduleDirectories = findReactorModuleDirectories();
  const bundledDirectories = new Set([...bundles.values()].map(bundle => bundle.moduleDirectory));
  const missing = [];
  for (const moduleDirectory of moduleDirectories) {
    const sourceDirectory = path.join(moduleDirectory, 'src/main/java');
    if (!fs.existsSync(sourceDirectory)) {
      continue;
    }
    const sourceFiles = [];
    walk(sourceDirectory, sourceFiles, file => /-servlet-context\.xml$/.test(file));
    const hasModuleDescriptor = sourceFiles.some(file => /-servlet-context\.xml$/.test(file));
    if (hasModuleDescriptor && !bundledDirectories.has(moduleDirectory.replaceAll(path.sep, '/'))) {
      missing.push(moduleDirectory);
    }
  }
  if (missing.length > 0) {
    throw new Error(`模块同时缺少中英文语言资源: ${missing.sort().join(', ')}`);
  }
}

const files = [];
walk(workspace, files);
const bundles = new Map();
for (const file of files.sort()) {
  const normalized = file.replaceAll(path.sep, '/');
  const match = normalized.match(bundlePattern);
  const owner = match[1];
  const language = match[2];
  const moduleDirectory = normalized.slice(0, normalized.indexOf('/src/main/resources/'));
  const expectedOwner = path.basename(moduleDirectory).replace(/^neatlogic-/, '');
  if (owner !== expectedOwner) {
    throw new Error(`语言资源 owner 与 artifact 目录不一致: ${file}, expected: ${expectedOwner}`);
  }
  const key = `${moduleDirectory}:${owner}`;
  const bundle = bundles.get(key) ?? {owner, moduleDirectory, languages: {}};
  if (bundle.languages[language]) {
    throw new Error(`模块语言文件重复: ${file}`);
  }
  bundle.languages[language] = {file, messages: flatten(JSON.parse(fs.readFileSync(file, 'utf8')), file)};
  bundles.set(key, bundle);
}

const globalKeys = {zh: new Map(), en: new Map()};
let keyCount = 0;
for (const bundle of bundles.values()) {
  if (!bundle.languages.zh || !bundle.languages.en) {
    throw new Error(`模块缺少中英文镜像: ${bundle.moduleDirectory}`);
  }
  const chineseKeys = [...bundle.languages.zh.messages.keys()].sort();
  const englishKeys = [...bundle.languages.en.messages.keys()].sort();
  if (JSON.stringify(chineseKeys) !== JSON.stringify(englishKeys)) {
    throw new Error(`模块中英文 key 不一致: ${bundle.moduleDirectory}`);
  }
  keyCount += chineseKeys.length;
  for (const language of ['zh', 'en']) {
    for (const messageKey of bundle.languages[language].messages.keys()) {
      const previous = globalKeys[language].get(messageKey);
      if (previous) {
        throw new Error(`跨模块重复 key: ${messageKey}, first: ${previous}, duplicate: ${bundle.moduleDirectory}`);
      }
      globalKeys[language].set(messageKey, bundle.moduleDirectory);
    }
  }
}

validateModuleCoverage(bundles);

process.stdout.write(`${JSON.stringify({
  moduleCount: bundles.size,
  languageFileCount: files.length,
  keyCount,
  chineseKeyCount: globalKeys.zh.size,
  englishKeyCount: globalKeys.en.size
}, null, 2)}\n`);
