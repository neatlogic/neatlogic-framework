/*
 * Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package neatlogic.module.framework.startup;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.common.config.Config;
import neatlogic.framework.common.dto.BasePageVo;
import neatlogic.framework.common.util.PageUtil;
import neatlogic.framework.documentonline.dto.DocumentOnlineDirectoryVo;
import neatlogic.framework.documentonline.exception.DocumentOnlineIndexDirNotSetException;
import neatlogic.framework.startup.StartupBase;
import neatlogic.framework.util.TableResultUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.*;

@Component
public class DocumentOnlineInitializeIndexHandler extends StartupBase {

    private final Logger logger = LoggerFactory.getLogger(DocumentOnlineInitializeIndexHandler.class);

    /**
     * 在线帮助文档根目录
     */
    private static String classpathRoot = "documentonline/";
    /**
     * 在线帮助文档所属模块组集合
     */
    private static Set<String> moduleGroupSet = new LinkedHashSet<>();
    /**
     * 用于存储模块组与菜单映射关系，一个模块组可以有多个菜单
     */
    private static Map<String, Set<String>> moduleGroupToMenuSetMap = new HashMap<>();
    /**
     * 用于存储模块组与在线文档路径映射关系，一个模块组可以有多个在线文档
     */
    private static Map<String, Set<String>> moduleGroupToFilePathSetMap = new HashMap<>();
    /**
     * 用于存储菜单与在线文档路径映射关系，一个菜单可以有多个在线文档
     */
    private static Map<String, Set<String>> menuToFilePathSetMap = new HashMap<>();
    /**
     * 在documentonline-mapping.json配置文件中没有设置的在线文档文件路径集合，它们不属于任何模块、任何菜单
     */
    private static List<String> notConfiguredFilePathList = new ArrayList<>();
    /**
     * 用于存储documentonline-mapping.json配置文件中的数据，不同模块jar包中都可能存在documentonline-mapping.json配置文件，可能有多个
     */
    private static List<JSONObject> mappingConfigList = new ArrayList();

    public final static DocumentOnlineDirectoryVo DOCUMENT_ONLINE_DIRECTORY_ROOT = new DocumentOnlineDirectoryVo("root", false);
    /**
     * 生成在线文档树结构目录
     * @return
     */
    public static List<DocumentOnlineDirectoryVo> getDocumentOnlineDirectory() {
        return null;
//        JSONArray resultList = new JSONArray();
//        List<ModuleGroupVo> moduleGroupList = TenantContext.get().getActiveModuleGroupList();
//        for (ModuleGroupVo moduleGroupVo : moduleGroupList) {
//            String moduleGroup = moduleGroupVo.getGroup();
//            if (!moduleGroupSet.contains(moduleGroup)) {
//                continue;
//            }
//            JSONObject obj = new JSONObject();
//            obj.put("type", "moduleGroup");
//            obj.put("id", moduleGroup);
//            obj.put("name", moduleGroupVo.getGroupName());
//            List<JSONObject> children = new ArrayList<>();
//            obj.put("children", children);
//            Set<String> menuSet = moduleGroupToMenuSetMap.get(moduleGroup);
//            if (CollectionUtils.isNotEmpty(menuSet)) {
//                for (String menu : menuSet) {
//                    JSONObject menuObj = new JSONObject();
//                    menuObj.put("moduleGroup", moduleGroup);
//                    menuObj.put("type", "menu");
//                    menuObj.put("id", menu);
//                    List<JSONObject> menuChildren = new ArrayList<>();
//                    menuObj.put("children", menuChildren);
//                    children.add(menuObj);
//                    Set<String> filePathSet = menuToFilePathSetMap.get(menu);
//                    if (CollectionUtils.isNotEmpty(filePathSet)) {
//                        for (String filePath : filePathSet) {
//                            JSONObject fileInfoObj = new JSONObject();
//                            fileInfoObj.put("moduleGroup", moduleGroup);
//                            fileInfoObj.put("menu", menu);
//                            fileInfoObj.put("type", "file");
//                            fileInfoObj.put("id", filePath);
//                            fileInfoObj.put("name", getFileNameByFilePath(filePath));
//                            menuChildren.add(fileInfoObj);
//                        }
//                    }
//                }
//            }
//            Set<String> filePathSet = moduleGroupToFilePathSetMap.get(moduleGroup);
//            if (CollectionUtils.isNotEmpty(filePathSet)) {
//                for (String filePath : filePathSet) {
//                    JSONObject fileInfoObj = new JSONObject();
//                    fileInfoObj.put("moduleGroup", moduleGroup);
//                    fileInfoObj.put("type", "file");
//                    fileInfoObj.put("id", filePath);
//                    fileInfoObj.put("name", getFileNameByFilePath(filePath));
//                    children.add(fileInfoObj);
//                }
//            }
//            resultList.add(obj);
//        }
//        for (String filePath : notConfiguredFilePathList) {
//            JSONObject fileInfoObj = new JSONObject();
//            fileInfoObj.put("type", "file");
//            fileInfoObj.put("id", filePath);
//            fileInfoObj.put("name", getFileNameByFilePath(filePath));
//            resultList.add(fileInfoObj);
//        }
//        return resultList;
    }

    /**
     * 返回在线文档首页数据，包含各个模块的在线文档列表，支持分页
     * @param basePageVo
     * @return
     */
    public static JSONArray getDocumentOnlineTableList(BasePageVo basePageVo) {
        JSONArray resultList = new JSONArray();
//        List<ModuleGroupVo> moduleGroupList = TenantContext.get().getActiveModuleGroupList();
//        for (ModuleGroupVo moduleGroupVo : moduleGroupList) {
//            String moduleGroup = moduleGroupVo.getGroup();
//            if (!moduleGroupSet.contains(moduleGroup)) {
//                continue;
//            }
//            JSONObject tableObj = new JSONObject();
//            tableObj.put("moduleGroup", moduleGroup);
//            tableObj.put("moduleGroupName", moduleGroupVo.getGroupName());
//            List<JSONObject> tbodyList = new ArrayList<>();
//            Set<String> menuSet = moduleGroupToMenuSetMap.get(moduleGroup);
//            if (CollectionUtils.isNotEmpty(menuSet)) {
//                for (String menu : menuSet) {
//                    Set<String> filePathSet = menuToFilePathSetMap.get(menu);
//                    if (CollectionUtils.isNotEmpty(filePathSet)) {
//                        for (String filePath : filePathSet) {
//                            JSONObject tbodyObj = new JSONObject();
//                            tbodyObj.put("moduleGroup", moduleGroup);
//                            tbodyObj.put("menu", menu);
//                            tbodyObj.put("filePath", filePath);
//                            tbodyObj.put("fileName", getFileNameByFilePath(filePath));
//                            tbodyList.add(tbodyObj);
//                        }
//                    }
//                }
//            }
//            Set<String> filePathSet = moduleGroupToFilePathSetMap.get(moduleGroup);
//            if (CollectionUtils.isNotEmpty(filePathSet)) {
//                for (String filePath : filePathSet) {
//                    JSONObject tbodyObj = new JSONObject();
//                    tbodyObj.put("moduleGroup", moduleGroup);
//                    tbodyObj.put("filePath", filePath);
//                    tbodyObj.put("fileName", getFileNameByFilePath(filePath));
//                    tbodyList.add(tbodyObj);
//                }
//            }
//            basePageVo.setRowNum(tbodyList.size());
//            tableObj.put("currentPage", basePageVo.getCurrentPage());
//            tableObj.put("pageSize", basePageVo.getPageSize());
//            tableObj.put("pageCount", basePageVo.getPageCount());
//            tableObj.put("rowNum", basePageVo.getRowNum());
//            tbodyList = PageUtil.subList(tbodyList, basePageVo);
//            tableObj.put("tbodyList", tbodyList);
//            resultList.add(tableObj);
//        }
        return resultList;
    }

    /**
     * 根据模块组、菜单返回在线文档列表，支持分页
     * @param upwardNameList
     * @param basePageVo
     * @return
     */
    public static JSONObject getDocumentOnlineList(List<String> upwardNameList, BasePageVo basePageVo) {
        List<JSONObject> tbodyList = new ArrayList<>();
//        List<ModuleGroupVo> activeModuleGroupVoList = TenantContext.get().getActiveModuleGroupList();
//        List<String> activeModuleGroupList = activeModuleGroupVoList.stream().map(ModuleGroupVo::getGroup).collect(Collectors.toList());
//        if (!activeModuleGroupList.contains(moduleGroup)) {
//            return TableResultUtil.getResult(tbodyList, basePageVo);
//        }
//        Set<String> menuSet = moduleGroupToMenuSetMap.get(moduleGroup);
//        if (CollectionUtils.isNotEmpty(menuSet)) {
//            for (String func : menuSet) {
//                if (StringUtils.isNotBlank(menu) && !Objects.equals(menu, func)) {
//                    continue;
//                }
//                Set<String> filePathSet = menuToFilePathSetMap.get(func);
//                if (CollectionUtils.isNotEmpty(filePathSet)) {
//                    for (String filePath : filePathSet) {
//                        JSONObject tbodyObj = new JSONObject();
//                        tbodyObj.put("moduleGroup", moduleGroup);
//                        tbodyObj.put("menu", func);
//                        tbodyObj.put("filePath", filePath);
//                        tbodyObj.put("fileName", getFileNameByFilePath(filePath));
//                        tbodyList.add(tbodyObj);
//                    }
//                }
//            }
//        }
//        Set<String> filePathSet = moduleGroupToFilePathSetMap.get(moduleGroup);
//        if (CollectionUtils.isNotEmpty(filePathSet)) {
//            for (String filePath : filePathSet) {
//                JSONObject tbodyObj = new JSONObject();
//                tbodyObj.put("moduleGroup", moduleGroup);
//                tbodyObj.put("filePath", filePath);
//                tbodyObj.put("fileName", getFileNameByFilePath(filePath));
//                tbodyList.add(tbodyObj);
//            }
//        }
        basePageVo.setRowNum(tbodyList.size());
        return TableResultUtil.getResult(PageUtil.subList(tbodyList, basePageVo), basePageVo);
    }

    @Override
    public String getName() {
        return "初始化在线帮助文档索引";
    }

    @Override
    public int executeForAllTenant() throws Exception {
        if (StringUtils.isBlank(Config.DOCUMENT_ONLINE_INDEX_DIR())) {
            throw new DocumentOnlineIndexDirNotSetException();
        }
        IndexWriter indexWriter = null;
        try {
            // 在documentonline-mapping.json配置文件中已设置在线文档文件路径集合，用于防止同个在线文档重复配置
            List<String> configuredFilePathList = new ArrayList<>();
            ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] jsonResources = resolver.getResources("classpath*:" + classpathRoot + "**/documentonline-mapping.json");
            for (Resource resource : jsonResources) {
                String path = resource.getURL().getPath();
                StringWriter writer = new StringWriter();
                IOUtils.copy(resource.getInputStream(), writer, StandardCharsets.UTF_8);
                String content = writer.toString();
                if (StringUtils.isBlank(content)) {
                    continue;
                }
                try {
                    JSONArray mappingArray = JSONArray.parseArray(content);
                    for (int i = 0; i < mappingArray.size(); i++) {
                        JSONObject mappingObj = mappingArray.getJSONObject(i);
                        String filePath = mappingObj.getString("filePath");
                        if (StringUtils.isBlank(filePath)) {
                            logger.warn(path + "文件中第" + i + "个元素中的filePath字段没有设置值" + filePath);
                            continue;
                        }
//                        if (configuredFilePathList.contains(filePath)) {
//                            logger.warn(path + "文件中第" + i + "个元素中的filePath字段值为”" + filePath + "“，已在其他文件中配置，该配置将不生效");
//                            continue;
//                        }
                        mappingObj.put("source", path);
                        mappingConfigList.add(mappingObj);
                        configuredFilePathList.add(filePath);
                    }
                } catch (JSONException e) {
                    logger.error(e.getMessage(), e);
                }
            }

            // 存储所有在线文档的路径集合，用于判断不同jar包中有相同路径的文档
            List<String> existingFilePathList = new ArrayList<>();
            // 1.创建分词器
            Analyzer analyzer = new IKAnalyzer(true);
            // 2.创建Directory目录对象，目录对象表示索引库的位置
            Directory dir = FSDirectory.open(Paths.get(Config.DOCUMENT_ONLINE_INDEX_DIR()));
            // 3.创建IndexWriterConfig对象，这个对象中指定切分词使用的分词器
            IndexWriterConfig config = new IndexWriterConfig(analyzer);
            // 4.创建IndexWriter输出流对象，指定输出的位置和使用的config初始化对象
            indexWriter = new IndexWriter(dir, config);
            // 5.删除之前索引数据，然后在重新生成
            indexWriter.deleteAll();
            // 1.采集数据
            Resource[] mdResources = resolver.getResources("classpath*:" + classpathRoot + "**/*.md");
            for (Resource resource : mdResources) {
                List<String> moduleGroupList = new ArrayList<>();
                List<String> menuList = new ArrayList<>();
                String filename = resource.getFilename().substring(0, resource.getFilename().length() - 3);
                String path = resource.getURL().getPath();
                int classpathRootIndex = path.indexOf(classpathRoot);
                String filePath = path.substring(classpathRootIndex);
                if (existingFilePathList.contains(filePath)) {
                    logger.error("有两个文件路径相同，路径为：“" + filePath + "“，请将其中一个文件重命名文件或移动到不同路径中");
                    System.exit(1);
                }
                DocumentOnlineDirectoryVo directory = buildDirectory(DOCUMENT_ONLINE_DIRECTORY_ROOT, filePath);
                // 根据文件路径找到配置映射信息，分析出文件所属的模块组、菜单
                List<JSONObject> mappingConfigs = getMappingConfigByFilePath(filePath);
                if (CollectionUtils.isNotEmpty(mappingConfigs)) {
                    for (JSONObject mappingConfig : mappingConfigs) {
                        String moduleGroup = mappingConfig.getString("moduleGroup");
                        if (StringUtils.isNotBlank(moduleGroup)) {
                            moduleGroupList.add(moduleGroup);
                            moduleGroupSet.add(moduleGroup);
                            String menu = mappingConfig.getString("menu");
                            if (StringUtils.isNotBlank(menu)) {
                                menuList.add(menu);
                                menuToFilePathSetMap.computeIfAbsent(menu, k -> new LinkedHashSet<>()).add(filePath);
                                moduleGroupToMenuSetMap.computeIfAbsent(moduleGroup, k -> new LinkedHashSet<>()).add(menu);
                            } else {
                                moduleGroupToFilePathSetMap.computeIfAbsent(moduleGroup, k -> new LinkedHashSet<>()).add(filePath);
                            }
                        } else {
                            notConfiguredFilePathList.add(filePath);
                        }
                    }
                } else {
                    notConfiguredFilePathList.add(filePath);
                }
                // 读取文件内容
                StringWriter writer = new StringWriter();
                IOUtils.copy(resource.getInputStream(), writer, StandardCharsets.UTF_8);
                String str = writer.toString();

                Document document = new Document();
                // 创建域对象并且放入到文档中
                if (CollectionUtils.isNotEmpty(moduleGroupList)) {
                    // 模块组字段不分词
                    document.add(new TextField("moduleGroup", StringUtils.join(" ", moduleGroupList), Field.Store.YES));
                }
                if (CollectionUtils.isNotEmpty(menuList)) {
                    // 菜单字段不分词
                    document.add(new TextField("menu", StringUtils.join(" ", menuList), Field.Store.YES));
                }
                // 文件路径字段不分词
                document.add(new StringField("filePath", filePath, Field.Store.YES));
                // 上层名称列表字段不分词
                document.add(new StringField("upwardNameList", JSONArray.toJSONString(directory.getUpwardNameList()), Field.Store.YES));
                // 文件名称字段分词
                document.add(new TextField("fileName", filename, Field.Store.YES));
                // 文件内容字段分词
                document.add(new TextField("content", str, Field.Store.YES));
                indexWriter.addDocument(document);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        } finally {
            // 8.释放资源
            if (indexWriter != null) {
                indexWriter.close();
            }
        }
        return 1;
    }

    @Override
    public int sort() {
        return 10;
    }

    /**
     * 根据文件路径找到documentonline-mapping.json配置文件设置的映射信息
     * @param filePath 文件路径
     * @return 映射信息
     */
    private List<JSONObject> getMappingConfigByFilePath(String filePath) {
        List<JSONObject> resultList = new ArrayList<>();
        for (JSONObject mappingConfig : mappingConfigList) {
            if (filePath.startsWith(mappingConfig.getString("filePath"))) {
                resultList.add(mappingConfig);
            }
        }
        return resultList;
    }

    /**
     * 从文件路径中截取文件名称
     * @param filePath 文件路径
     * @return 文件名称
     */
    private static String getFileNameByFilePath(String filePath) {
        int index = filePath.lastIndexOf("/");
        return filePath.substring(index + 1, filePath.length() - 3);
    }

    /**
     * 构建在线帮助文档目录
     * @param root
     * @param filePath
     */
    private DocumentOnlineDirectoryVo buildDirectory(DocumentOnlineDirectoryVo root, String filePath) {
        String path = filePath.substring(classpathRoot.length());
        List<String> nameList = new ArrayList<>();
        DocumentOnlineDirectoryVo parent = root;
        String[] split = path.split("/");
        for (String name : split) {
            boolean isFile = false;
            if (name.endsWith(".md")) {
                isFile = true;
                name = name.substring(0, name.length() - 3);
            }
            nameList.add(name);
            if (isFile) {
                DocumentOnlineDirectoryVo child = new DocumentOnlineDirectoryVo(name, true);
                List<String> upwardNameList = new LinkedList<>(nameList);
                upwardNameList.remove(0);
                child.setUpwardNameList(upwardNameList);
                child.setFilePath(filePath);
                parent.addChild(child);
                return child;
            }
            DocumentOnlineDirectoryVo child = null;
            List<DocumentOnlineDirectoryVo> children = parent.getChildren();
            for (DocumentOnlineDirectoryVo childVo : children) {
                if (Objects.equals(childVo.getName(), name)) {
                    child = childVo;
                }
            }
            if (child == null) {
                child = new DocumentOnlineDirectoryVo(name, false);
                List<String> upwardNameList = new LinkedList<>(nameList);
                upwardNameList.remove(0);
                child.setUpwardNameList(upwardNameList);
                parent.addChild(child);
            }
            parent = child;
        }
        return null;
    }
}
