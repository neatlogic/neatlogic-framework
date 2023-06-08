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
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.common.config.Config;
import neatlogic.framework.common.dto.BasePageVo;
import neatlogic.framework.common.util.PageUtil;
import neatlogic.framework.documentonline.exception.DocumentOnlineIndexDirNotSetException;
import neatlogic.framework.dto.module.ModuleGroupVo;
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
import java.util.stream.Collectors;

@Component
public class InitializeIndexHandler extends StartupBase {

    private final Logger logger = LoggerFactory.getLogger(InitializeIndexHandler.class);

    private final static Set<String> moduleGroupSet = new LinkedHashSet<>();

    private final static Map<String, Set<String>> moduleGroupToFunctionSetMap = new HashMap<>();

    private final static Map<String, Set<String>> moduleGroupToFilePathSetMap = new HashMap<>();

    private final static Map<String, Set<String>> functionToFilePathSetMap = new HashMap<>();

    private final static List<String> notConfiguredFilePathList = new ArrayList<>();

    private final static List<JSONObject> mappingConfigList = new ArrayList();

    private final static List<String> configuredFilePathList = new ArrayList<>();

    public static JSONArray getDocumentOnlineDirectory() {
        JSONArray resultList = new JSONArray();
        List<ModuleGroupVo> moduleGroupList = TenantContext.get().getActiveModuleGroupList();
        for (ModuleGroupVo moduleGroupVo : moduleGroupList) {
            String moduleGroup = moduleGroupVo.getGroup();
            if (!moduleGroupSet.contains(moduleGroup)) {
                continue;
            }
            JSONObject obj = new JSONObject();
            obj.put("type", "moduleGroup");
            obj.put("id", moduleGroup);
            obj.put("name", moduleGroupVo.getGroupName());
            List<JSONObject> children = new ArrayList<>();
            obj.put("children", children);
            Set<String> functionSet = moduleGroupToFunctionSetMap.get(moduleGroup);
            if (CollectionUtils.isNotEmpty(functionSet)) {
                for (String function : functionSet) {
                    JSONObject functionObj = new JSONObject();
                    functionObj.put("moduleGroup", moduleGroup);
                    functionObj.put("type", "function");
                    functionObj.put("id", function);
                    List<JSONObject> functionChildren = new ArrayList<>();
                    functionObj.put("children", functionChildren);
                    children.add(functionObj);
                    Set<String> filePathSet = functionToFilePathSetMap.get(function);
                    if (CollectionUtils.isNotEmpty(filePathSet)) {
                        for (String filePath : filePathSet) {
                            JSONObject fileInfoObj = new JSONObject();
                            fileInfoObj.put("moduleGroup", moduleGroup);
                            fileInfoObj.put("function", function);
                            fileInfoObj.put("type", "file");
                            fileInfoObj.put("id", filePath);
                            fileInfoObj.put("name", getFileNameByFilePath(filePath));
                            functionChildren.add(fileInfoObj);
                        }
                    }
                }
            }
            Set<String> filePathSet = moduleGroupToFilePathSetMap.get(moduleGroup);
            if (CollectionUtils.isNotEmpty(filePathSet)) {
                for (String filePath : filePathSet) {
                    JSONObject fileInfoObj = new JSONObject();
                    fileInfoObj.put("moduleGroup", moduleGroup);
                    fileInfoObj.put("type", "file");
                    fileInfoObj.put("id", filePath);
                    fileInfoObj.put("name", getFileNameByFilePath(filePath));
                    children.add(fileInfoObj);
                }
            }
            resultList.add(obj);
        }
        for (String filePath : notConfiguredFilePathList) {
            JSONObject fileInfoObj = new JSONObject();
            fileInfoObj.put("type", "file");
            fileInfoObj.put("id", filePath);
            fileInfoObj.put("name", getFileNameByFilePath(filePath));
            resultList.add(fileInfoObj);
        }
        return resultList;
    }

    public static JSONArray getDocumentOnlineTableList(BasePageVo basePageVo) {
        JSONArray resultList = new JSONArray();
        List<ModuleGroupVo> moduleGroupList = TenantContext.get().getActiveModuleGroupList();
        for (ModuleGroupVo moduleGroupVo : moduleGroupList) {
            String moduleGroup = moduleGroupVo.getGroup();
            if (!moduleGroupSet.contains(moduleGroup)) {
                continue;
            }
            JSONObject tableObj = new JSONObject();
            tableObj.put("moduleGroup", moduleGroup);
            tableObj.put("moduleGroupName", moduleGroupVo.getGroupName());
            List<JSONObject> tbodyList = new ArrayList<>();
            Set<String> functionSet = moduleGroupToFunctionSetMap.get(moduleGroup);
            if (CollectionUtils.isNotEmpty(functionSet)) {
                for (String function : functionSet) {
                    Set<String> filePathSet = functionToFilePathSetMap.get(function);
                    if (CollectionUtils.isNotEmpty(filePathSet)) {
                        for (String filePath : filePathSet) {
                            JSONObject tbodyObj = new JSONObject();
                            tbodyObj.put("moduleGroup", moduleGroup);
                            tbodyObj.put("function", function);
                            tbodyObj.put("filePath", filePath);
                            tbodyObj.put("fileName", getFileNameByFilePath(filePath));
                            tbodyList.add(tbodyObj);
                        }
                    }
                }
            }
            Set<String> filePathSet = moduleGroupToFilePathSetMap.get(moduleGroup);
            if (CollectionUtils.isNotEmpty(filePathSet)) {
                for (String filePath : filePathSet) {
                    JSONObject tbodyObj = new JSONObject();
                    tbodyObj.put("moduleGroup", moduleGroup);
                    tbodyObj.put("filePath", filePath);
                    tbodyObj.put("fileName", getFileNameByFilePath(filePath));
                    tbodyList.add(tbodyObj);
                }
            }
            basePageVo.setRowNum(tbodyList.size());
            tableObj.put("currentPage", basePageVo.getCurrentPage());
            tableObj.put("pageSize", basePageVo.getPageSize());
            tableObj.put("pageCount", basePageVo.getPageCount());
            tableObj.put("rowNum", basePageVo.getRowNum());
            tbodyList = PageUtil.subList(tbodyList, basePageVo);
            tableObj.put("tbodyList", tbodyList);
            resultList.add(tableObj);
        }
        return resultList;
    }

    public static JSONObject getDocumentOnlineList(String moduleGroup, String function, BasePageVo basePageVo) {
        List<JSONObject> tbodyList = new ArrayList<>();
        List<ModuleGroupVo> activeModuleGroupVoList = TenantContext.get().getActiveModuleGroupList();
        List<String> activeModuleGroupList = activeModuleGroupVoList.stream().map(ModuleGroupVo::getGroup).collect(Collectors.toList());
        if (!activeModuleGroupList.contains(moduleGroup)) {
            return TableResultUtil.getResult(tbodyList, basePageVo);
        }
        Set<String> functionSet = moduleGroupToFunctionSetMap.get(moduleGroup);
        if (CollectionUtils.isNotEmpty(functionSet)) {
            for (String func : functionSet) {
                if (StringUtils.isNotBlank(function) && !Objects.equals(function, func)) {
                    continue;
                }
                Set<String> filePathSet = functionToFilePathSetMap.get(func);
                if (CollectionUtils.isNotEmpty(filePathSet)) {
                    for (String filePath : filePathSet) {
                        JSONObject tbodyObj = new JSONObject();
                        tbodyObj.put("moduleGroup", moduleGroup);
                        tbodyObj.put("function", func);
                        tbodyObj.put("filePath", filePath);
                        tbodyObj.put("fileName", getFileNameByFilePath(filePath));
                        tbodyList.add(tbodyObj);
                    }
                }
            }
        }
        Set<String> filePathSet = moduleGroupToFilePathSetMap.get(moduleGroup);
        if (CollectionUtils.isNotEmpty(filePathSet)) {
            for (String filePath : filePathSet) {
                JSONObject tbodyObj = new JSONObject();
                tbodyObj.put("moduleGroup", moduleGroup);
                tbodyObj.put("filePath", filePath);
                tbodyObj.put("fileName", getFileNameByFilePath(filePath));
                tbodyList.add(tbodyObj);
            }
        }
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
            // 3.创建分词器
            Analyzer analyzer = new IKAnalyzer(true);
            // 4.创建Directory目录对象，目录对象表示索引库的位置
            Directory dir = FSDirectory.open(Paths.get(Config.DOCUMENT_ONLINE_INDEX_DIR()));
            // 5.创建IndexWriterConfig对象，这个对象中指定切分词使用的分词器
            IndexWriterConfig config = new IndexWriterConfig(analyzer);
            // 6.创建IndexWriter输出流对象，指定输出的位置和使用的config初始化对象
            indexWriter = new IndexWriter(dir, config);
            // 删除索引
            indexWriter.deleteAll();

            String classpathRoot = "documentonline/";
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
                        if (configuredFilePathList.contains(filePath)) {
                            logger.warn(path + "文件中第" + i + "个元素中的filePath字段值为”" + filePath + "“，已在其他文件中配置，该配置将不生效");
                            continue;
                        }
                        mappingObj.put("source", path);
                        mappingConfigList.add(mappingObj);
                        configuredFilePathList.add(filePath);
                    }
                } catch (JSONException e) {
                    logger.error(e.getMessage(), e);
                }
            }
            // 1.采集数据
            Resource[] mdResources = resolver.getResources("classpath*:" + classpathRoot + "**/*.md");
            for (Resource resource : mdResources) {
                String moduleGroup = null;
                String function = null;
                String filename = resource.getFilename().substring(0, resource.getFilename().length() - 3);
                String path = resource.getURL().getPath();
                int classpathRootIndex = path.indexOf(classpathRoot);
                String filePath = path.substring(classpathRootIndex);
                JSONObject mappingConfig = getMappingConfigByFilePath(filePath);
                if (MapUtils.isNotEmpty(mappingConfig)) {
                    moduleGroup = mappingConfig.getString("moduleGroup");
                    if (StringUtils.isNotBlank(moduleGroup)) {
                        moduleGroupSet.add(moduleGroup);
                        function = mappingConfig.getString("function");
                        if (StringUtils.isNotBlank(function)) {
                            functionToFilePathSetMap.computeIfAbsent(function, k -> new LinkedHashSet<>()).add(filePath);
                            moduleGroupToFunctionSetMap.computeIfAbsent(moduleGroup, k -> new LinkedHashSet<>()).add(function);
                        } else {
                            moduleGroupToFilePathSetMap.computeIfAbsent(moduleGroup, k -> new LinkedHashSet<>()).add(filePath);
                        }
                    } else {
                        notConfiguredFilePathList.add(filePath);
                    }
                } else {
                    notConfiguredFilePathList.add(filePath);
                }
                StringWriter writer = new StringWriter();
                IOUtils.copy(resource.getInputStream(), writer, StandardCharsets.UTF_8);
                String str = writer.toString();
                Document document = new Document();
                // 创建域对象并且放入到文档集合中
                if (StringUtils.isNotBlank(moduleGroup)) {
                    document.add(new StringField("moduleGroup", moduleGroup, Field.Store.YES));
                }
                if (StringUtils.isNotBlank(function)) {
                    document.add(new StringField("function", function, Field.Store.YES));
                }
                document.add(new TextField("fileName", filename, Field.Store.YES));
                document.add(new StringField("filePath", filePath, Field.Store.YES));
                document.add(new TextField("content", str, Field.Store.YES));
                indexWriter.addDocument(document);
            }
//            String classpathRoot = "neatlogic/module/documentonline/document/";
//            // 1.采集数据
//            ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
//            Resource[] resources = resolver.getResources("classpath*:" + classpathRoot + "**/*.md");
//            for (Resource resource : resources) {
//                String path = resource.getURL().getPath();
//                int classpathRootIndex = path.indexOf(classpathRoot);
//                int endIndex = path.lastIndexOf("/");
//                String moduleNameAndFunctionName = path.substring(classpathRootIndex + classpathRoot.length(), endIndex);
//                if (StringUtils.isBlank(moduleNameAndFunctionName)) {
//                    continue;
//                }
//                String[] split = moduleNameAndFunctionName.split("/");
//                String moduleGroup = split[0];
//                String function = null;
//                if (split.length >= 2) {
//                    function = split[1];
//                }
//                String filename = resource.getFilename().substring(0, resource.getFilename().length() - 3);
//                StringWriter writer = new StringWriter();
//                IOUtils.copy(resource.getInputStream(), writer, StandardCharsets.UTF_8);
//                String str = writer.toString();
//                Document document = new Document();
//                // 创建域对象并且放入到文档集合中
//                document.add(new StringField("moduleGroup", moduleGroup, Field.Store.YES));
//                moduleGroupList.add(moduleGroup);
//                if (StringUtils.isNotBlank(function)) {
//                    document.add(new StringField("function", function, Field.Store.YES));
//                    moduleGroupToFunctionSetMap.computeIfAbsent(moduleGroup, k -> new HashSet<>()).add(function);
//                }
//                document.add(new TextField("fileName", filename, Field.Store.YES));
//                document.add(new TextField("content", str, Field.Store.YES));
//                indexWriter.addDocument(document);
//            }
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

    private JSONObject getMappingConfigByFilePath(String filePath) {
        for (JSONObject mappingConfig : mappingConfigList) {
            if (Objects.equals(filePath, mappingConfig.getString("filePath"))) {
                return mappingConfig;
            }
        }
        return null;
    }

    private static String getFileNameByFilePath(String filePath) {
        int index = filePath.lastIndexOf("/");
        return filePath.substring(index + 1, filePath.length() - 3);
    }

}
