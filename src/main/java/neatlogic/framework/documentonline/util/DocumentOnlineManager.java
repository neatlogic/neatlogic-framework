/*
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */

package neatlogic.framework.documentonline.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.common.config.Config;
import neatlogic.framework.common.util.FileUtil;
import neatlogic.framework.crossover.CrossoverServiceFactory;
import neatlogic.framework.documentonline.crossover.IDocumentOnlineCrossoverMapper;
import neatlogic.framework.documentonline.dto.DocumentOnlineConfigVo;
import neatlogic.framework.documentonline.dto.DocumentOnlineDirectoryVo;
import neatlogic.framework.documentonline.dto.DocumentOnlineVo;
import neatlogic.framework.documentonline.exception.DocumentOnlineJarNameIllegalException;
import neatlogic.framework.exception.core.ApiRuntimeException;
import neatlogic.framework.util.$;
import neatlogic.framework.util.HtmlUtil;
import org.apache.commons.collections4.CollectionUtils;
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
import org.apache.lucene.store.MMapDirectory;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.web.multipart.MultipartFile;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class DocumentOnlineManager {

    private final static Logger logger = LoggerFactory.getLogger(DocumentOnlineManager.class);

    private static final String COMMERCIAL_JAR_NAME_PREFIX = "neatlogic-document-online-commercial";

    private static final String COMMUNITY_JAR_NAME_PREFIX = "neatlogic-document-online";

    private static final String SUFFIX = ".jar";

    /**
     * War包外部在线帮助文档Jar包目录
     */
    public static final String OUTSIDE_WAR_DOCUMENTS_ONLINE_JARS = "documentOnlineJars";
    /**
     * 在线帮助文档根目录
     */
    public static final String DIRECTORY_ROOT = "documentonline";
    /**
     * 在线帮助文档索引库位置
     */
    public static final String INDEX_DIRECTORY = System.getProperty("java.io.tmpdir") + File.separator + DIRECTORY_ROOT;

    private static DocumentOnlineDirectoryVo DOCUMENT_ONLINE_DIRECTORY_ROOT = new DocumentOnlineDirectoryVo("root", false);

    public static DocumentOnlineDirectoryVo getDocumentOnlineDirectoryRoot() {
        return DOCUMENT_ONLINE_DIRECTORY_ROOT;
    }

    public static synchronized JSONObject initializeIndex(
            List<DocumentOnlineConfigVo> documentOnlineConfigList,
            List<String> mappingJsonLocationPatternList,
            List<String> mdFileLocationPatternList
    ) {
        JSONObject resultObj = new JSONObject();
        resultObj.put("mappingJsonLocationPatternList", mappingJsonLocationPatternList);
        resultObj.put("mdFileLocationPatternList", mdFileLocationPatternList);
        // 1.采集数据
        List<Resource> mdResourceList = new ArrayList<>();
        /**
         * 用于存储documentonline-mapping.json配置文件中的数据，不同模块jar包中都可能存在documentonline-mapping.json配置文件，可能有多个
         */
        List<DocumentOnlineConfigVo> mappingConfigList = new ArrayList<>();
        try {
            for (DocumentOnlineConfigVo documentOnlineConfigVo : documentOnlineConfigList) {
                documentOnlineConfigVo.setSource("database");
                mappingConfigList.add(documentOnlineConfigVo);
            }
            ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            // 在documentonline-mapping.json配置文件中已设置在线文档文件路径集合，用于防止同个在线文档重复配置
            List<Resource> jsonResourceList = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(mappingJsonLocationPatternList)) {
                for (String locationPattern : mappingJsonLocationPatternList) {
                    Resource[] resources = resolver.getResources(locationPattern);
                    Collections.addAll(jsonResourceList, resources);
                }
            }
            List<String> mappingJsonResourceURLList = new ArrayList<>();
            for (Resource resource : jsonResourceList) {
                String path = resource.getURL().toString();
                mappingJsonResourceURLList.add(path);
                String content = null;
                try (StringWriter writer = new StringWriter(); InputStream inputStream = resource.getInputStream()) {
                    // 读取文件内容
                    IOUtils.copy(inputStream, writer, StandardCharsets.UTF_8);
                    content = writer.toString();
                }
                if (StringUtils.isBlank(content)) {
                    continue;
                }
                try {
                    JSONArray mappingArray = JSON.parseArray(content);
                    for (int i = 0; i < mappingArray.size(); i++) {
                        JSONObject mappingObj = mappingArray.getJSONObject(i);
                        DocumentOnlineConfigVo documentOnlineConfigVo = mappingObj.toJavaObject(DocumentOnlineConfigVo.class);
                        if (mappingConfigList.contains(documentOnlineConfigVo)) {
                            continue;
                        }
                        String filePath = documentOnlineConfigVo.getFilePath();
                        if (StringUtils.isBlank(filePath)) {
                            logger.warn($.t("nmfs.documentonlineinitializeindexhandler.executeforalltenant.warn_a", path, i));
                            continue;
                        }
                        String moduleGroup = documentOnlineConfigVo.getModuleGroup();
                        if (StringUtils.isBlank(moduleGroup)) {
                            logger.warn($.t("nmfs.documentonlineinitializeindexhandler.executeforalltenant.warn_b", path, i));
                            continue;
                        }
                        documentOnlineConfigVo.setSource(path);
                        mappingConfigList.add(documentOnlineConfigVo);
                        // 初始化的时候以documentonline-mapping.json配置文件数据为主，根据删除主键数据库中数据
//                        documentOnlineMapper.deleteDocumentOnlineConfig(documentOnlineConfigVo);
                    }
                } catch (JSONException e) {
                    logger.error(e.getMessage(), e);
                }
            }
            resultObj.put("mappingJsonResourceURLList", mappingJsonResourceURLList);
            if (CollectionUtils.isNotEmpty(mdFileLocationPatternList)) {
                for (String locationPattern : mdFileLocationPatternList) {
                    Resource[] resources = resolver.getResources(locationPattern);
                    Collections.addAll(mdResourceList, resources);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        if (CollectionUtils.isNotEmpty(mdResourceList)) {
            DocumentOnlineDirectoryVo documentOnlineDirectoryVo = new DocumentOnlineDirectoryVo("root", false);
            // 1.创建分词器
            Analyzer analyzer = new IKAnalyzer(true);
            // 2.创建dir目录对象，目录对象表示索引库的位置
            // 3.创建IndexWriterConfig对象，这个对象中指定切分词使用的分词器
            IndexWriterConfig config = new IndexWriterConfig(analyzer);
            // 4.创建IndexWriter输出流对象，指定输出的位置和使用的config初始化对象
            try (Directory dir = MMapDirectory.open(Paths.get(INDEX_DIRECTORY));
                 IndexWriter indexWriter = new IndexWriter(dir, config)) {
                // 5.删除之前索引数据，然后在重新生成
                indexWriter.deleteAll();
                System.out.println($.t("nmfs.documentonlineinitializeindexhandler.executeforalltenant.system_out_println", INDEX_DIRECTORY));
                // 存储所有在线文档的路径集合，用于判断不同jar包中有相同路径的文档
                List<String> existingFilePathList = new ArrayList<>();
                List<String> mdResourceURLList = new ArrayList<>();
                for (Resource resource : mdResourceList) {
                    String filename = resource.getFilename().substring(0, resource.getFilename().length() - 3);
                    int index = filename.indexOf(".");
                    if (index != -1) {
                        String sort = filename.substring(0, index);
                        if (StringUtils.isNumeric(sort)) {
                            filename = filename.substring(index + 1);
                        }
                    }
                    String path = resource.getURL().toString();
                    mdResourceURLList.add(path);
                    String filePath = DocumentOnlineManager.getWithinJarAbsoluteFilePathByURL(resource.getURL());
                    if (existingFilePathList.contains(filePath)) {
                        logger.error($.t("nmfs.documentonlineinitializeindexhandler.executeforalltenant.error", filePath));
                        System.exit(1);
                    }
                    existingFilePathList.add(filePath);
                    List<DocumentOnlineConfigVo> configList = new ArrayList<>();
                    // 根据文件路径找到配置映射信息，分析出文件所属的模块组、菜单，定位的锚点
                    List<DocumentOnlineConfigVo> mappingConfigs = getMappingConfigByFilePath(filePath, mappingConfigList);
                    if (CollectionUtils.isNotEmpty(mappingConfigs)) {
                        for (DocumentOnlineConfigVo documentOnlineConfigVo : mappingConfigs) {
                            if (StringUtils.isNotBlank(documentOnlineConfigVo.getModuleGroup())) {
                                DocumentOnlineConfigVo configVo = new DocumentOnlineConfigVo(documentOnlineConfigVo);
                                configVo.setFilePath(filePath);
                                configList.add(configVo);
                            }
                        }
                    }
                    List<String> upwardNameList = new ArrayList<>();
                    DocumentOnlineDirectoryVo directory = buildDirectory(documentOnlineDirectoryVo, filePath, path, configList);
                    if (directory != null) {
                        upwardNameList = directory.getUpwardNameList();
                    }
                    String content = "";
                    try (StringWriter writer = new StringWriter(); InputStream inputStream = resource.getInputStream()) {
                        // 读取文件内容
                        IOUtils.copy(inputStream, writer, StandardCharsets.UTF_8);
                        content = writer.toString();
                    }
                    Document document = new Document();
                    // 创建域对象并且放入到文档中
                    // 文件路径字段不分词
                    document.add(new StringField("filePath", filePath, Field.Store.YES));
                    // 上层名称列表字段不分词
                    document.add(new StringField("upwardNameList", JSON.toJSONString(upwardNameList), Field.Store.YES));
                    // 文件名称字段分词
                    document.add(new TextField("fileName", filename, Field.Store.YES));
                    // 文件内容字段分词
                    document.add(new TextField("content", content, Field.Store.YES));
                    indexWriter.addDocument(document);
                }
                resultObj.put("mdResourceURLList", mdResourceURLList);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            } finally {
                // 禁止添加子节点
                documentOnlineDirectoryVo.noAllowedAddChild();
            }
            DOCUMENT_ONLINE_DIRECTORY_ROOT = documentOnlineDirectoryVo;
            resultObj.put("DOCUMENT_ONLINE_DIRECTORY_ROOT", documentOnlineDirectoryVo);
        }
        for (DocumentOnlineConfigVo documentOnlineConfigVo : mappingConfigList) {
            if (!documentOnlineConfigVo.isUsed()) {
                logger.warn($.t("nmfs.documentonlineinitializeindexhandler.executeforalltenant.warn_c") + JSON.toJSONString(documentOnlineConfigVo));
            }
        }
        resultObj.put("mappingConfigList", mappingConfigList);
        return resultObj;
    }

    /**
     * 根据文件路径找到documentonline-mapping.json配置文件设置的映射信息
     *
     * @param filePath 文件路径
     * @return 映射信息
     */
    private static List<DocumentOnlineConfigVo> getMappingConfigByFilePath(String filePath, List<DocumentOnlineConfigVo> mappingConfigList) {
        List<DocumentOnlineConfigVo> resultList = new ArrayList<>();
        for (DocumentOnlineConfigVo documentOnlineConfigVo : mappingConfigList) {
            if (filePath.startsWith(documentOnlineConfigVo.getFilePath())) {
                if (!resultList.contains(documentOnlineConfigVo)) {
                    resultList.add(documentOnlineConfigVo);
                    documentOnlineConfigVo.setUsed(true);
                }
            }
        }
        return resultList;
    }

    /**
     * 构建在线帮助文档目录
     *
     * @param root
     * @param filePath
     */
    private static DocumentOnlineDirectoryVo buildDirectory(DocumentOnlineDirectoryVo root, String filePath, String path, List<DocumentOnlineConfigVo> configList) {
        int directoryRootIndex = filePath.indexOf(DIRECTORY_ROOT);
        String substr = filePath.substring(directoryRootIndex + DIRECTORY_ROOT.length() + 1);
        List<String> nameList = new ArrayList<>();
        DocumentOnlineDirectoryVo parent = root;
        String[] split = substr.split("/");
        for (String name : split) {
            boolean isFile = false;
            if (name.endsWith(".md")) {
                isFile = true;
                name = name.substring(0, name.length() - 3);
            }
            Integer prefix = null;
            int index = name.indexOf(".");
            if (index != -1) {
                String sort = name.substring(0, index);
                if (StringUtils.isNumeric(sort)) {
                    prefix = Integer.valueOf(sort);
                    name = name.substring(index + 1);
                }
            }
            nameList.add(name);
            if (isFile) {
                List<String> upwardNameList = new LinkedList<>(nameList);
                upwardNameList.remove(0);
                DocumentOnlineDirectoryVo child = new DocumentOnlineDirectoryVo(prefix, name, true, upwardNameList, path, configList);
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
                List<String> upwardNameList = new LinkedList<>(nameList);
                upwardNameList.remove(0);
                child = new DocumentOnlineDirectoryVo(prefix, name, false, upwardNameList);
                parent.addChild(child);
            }
            parent = child;
        }
        return null;
    }


    public static String interceptsSpecifiedNumberOfCharacters(InputStream inputStream, int skip, int number) throws IOException {
        Parser parser = Parser.builder().build();
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        StringBuilder stringBuilder = new StringBuilder();
        try (InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
            bufferedReader.skip(skip);
            while (stringBuilder.length() < number) {
                String lineContent = bufferedReader.readLine();
                if (lineContent == null) {
                    break;
                }
                if (StringUtils.isBlank(lineContent)) {
                    continue;
                }
                // 1.先把这行内容中HTML标签去掉，因为第2步中将markdown语法转换成HTML标签时，会把原有的HTML标签中的尖括号转成实体字符
                // 例如：<img src="https://img.shields.io/badge/License-Apache%202.0-blue.svg" /></a>
                // 转换成 <pre><code>    &lt;img src=&quot;https://img.shields.io/badge/License-Apache%202.0-blue.svg&quot; /&gt;&lt;/a&gt;</code></pre>
                lineContent = HtmlUtil.removeHtml(lineContent);
                if (StringUtils.isBlank(lineContent)) {
                    continue;
                }
                // 2.把这行内容中markdown语法转换成HTML标签
                Node document = parser.parse(lineContent);
                String html = renderer.render(document);
                // 3.再次把这行内容中HTML标签去掉
                lineContent = HtmlUtil.removeHtml(html);
                if (StringUtils.isBlank(lineContent)) {
                    continue;
                }
                stringBuilder.append(" ");
                if (lineContent.length() > number - stringBuilder.length()) {
                    stringBuilder.append(lineContent.substring(0, number - stringBuilder.length()));
                } else {
                    stringBuilder.append(lineContent);
                }
            }
        }
        return stringBuilder.toString();
    }


    public static List<DocumentOnlineVo> getAllFileList(DocumentOnlineDirectoryVo directory, String moduleGroup, String menu) {
        return getAllFileListAndSort(directory, moduleGroup, menu);
    }

    /**
     * 通过递归，获取某个目录下的指定模块、指定菜单下的文件，并且排序
     * @param directory 目录
     * @param moduleGroup 指定模块
     * @param menu 指定菜单
     * @return 返回文件列表
     */
    private static List<DocumentOnlineVo> getAllFileListAndSort(DocumentOnlineDirectoryVo directory, String moduleGroup, String menu) {
        List<DocumentOnlineVo> list = getAllFileListUseRecurve(directory, moduleGroup, menu);
        list.sort((o1, o2) -> {
            if (o1.getPrefix() != null && o2.getPrefix() != null) {
                return Integer.compare(o1.getPrefix(), o2.getPrefix());
            } else if (o1.getPrefix() != null && o2.getPrefix() == null) {
                return -1;
            } else if (o1.getPrefix() == null && o2.getPrefix() != null) {
                return 1;
            } else {
                return String.CASE_INSENSITIVE_ORDER.compare(o1.getFileName(), o2.getFileName());
            }
        });
        return list;
    }

    /**
     * 通过递归，获取某个目录下的指定模块、指定菜单下的文件
     * @param directory 目录
     * @param moduleGroup 指定模块
     * @param menu 指定菜单
     * @return 返回文件列表
     */
    private static List<DocumentOnlineVo> getAllFileListUseRecurve(DocumentOnlineDirectoryVo directory, String moduleGroup, String menu) {
        List<DocumentOnlineVo> list = new ArrayList<>();
        for (DocumentOnlineDirectoryVo child : directory.getChildren()) {
            if (child.getIsFile()) {
                JSONObject returnObj = new JSONObject();
                if (StringUtils.isBlank(moduleGroup) || child.belongToOwner(moduleGroup, menu, returnObj)) {
                    DocumentOnlineVo documentOnlineVo = new DocumentOnlineVo();
                    documentOnlineVo.setUpwardNameList(child.getUpwardNameList());
                    documentOnlineVo.setPrefix(child.getPrefix());
                    documentOnlineVo.setFileName(child.getName());
                    documentOnlineVo.setFilePath(child.getFilePath());
                    String anchorPoint = returnObj.getString("anchorPoint");
                    documentOnlineVo.setAnchorPoint(anchorPoint);
                    documentOnlineVo.setConfigList(child.getConfigList());
                    list.add(documentOnlineVo);
                }
            } else {
                list.addAll(getAllFileListUseRecurve(child, moduleGroup, menu));
            }
        }
        return list;
    }

    public static List<DocumentOnlineVo> getAllFileList(DocumentOnlineDirectoryVo directory) {
        return getAllFileListAndSort(directory, null, null);
    }

    public static DocumentOnlineDirectoryVo getDocumentOnlineDirectoryByFilePath(String filePath) {
        if (StringUtils.isBlank(filePath)) {
            return null;
        }
        int directoryRootIndex = filePath.indexOf(DocumentOnlineManager.DIRECTORY_ROOT);
        filePath = filePath.substring(directoryRootIndex);
        DocumentOnlineDirectoryVo directory = DocumentOnlineManager.getDocumentOnlineDirectoryRoot();
        String[] directoryNameList = filePath.split("/");
        for (int i = 1; i < directoryNameList.length; i++) {
            String directoryName = directoryNameList[i];
            // 标记是否找到对应的目录
            boolean flag = false;
            for (DocumentOnlineDirectoryVo child : directory.getChildren()) {
                String childName = child.getName();
                if (child.getIsFile()) {
                    childName += ".md";
                }
                if (child.getPrefix() != null) {
                    childName = child.getPrefix() + "." + childName;
                }
                if (Objects.equals(childName, directoryName)) {
                    directory = child;
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                return null;
            }
        }
        return directory;
    }

    public static String getResourceLocationPatternByFilePath(String filePath) {
        String locationPattern = null;
        if (filePath.startsWith("jar:file:")) {
            locationPattern = filePath;
        } else {
            locationPattern = "classpath:" + filePath;
        }
        return locationPattern;
    }

    public static String getWithinJarAbsoluteFilePathByURL(URL url) {
        String path = url.toString();
        int separatorIndex = path.lastIndexOf("/neatlogic/resources/");
        return path.substring(separatorIndex + 1);
    }

    /**
     * 从war包外部加载在线文档
     * @return
     */
    public static JSONObject LoadDocumentsOutsideWar() {
        String documentOnlineHomeDirPath = Config.DATA_HOME() + OUTSIDE_WAR_DOCUMENTS_ONLINE_JARS;
        File documentOnlineHome = new File(documentOnlineHomeDirPath);
        if (!documentOnlineHome.exists()) {
            if (!documentOnlineHome.mkdirs()) {
                throw new RuntimeException("没有创建文件(" + documentOnlineHomeDirPath + ")权限");
            }
        }
        {
            File[] listFiles = documentOnlineHome.listFiles();
            if (listFiles != null) {
                List<String> filePathList = new ArrayList<>();
                filePathList.add(new File(documentOnlineHomeDirPath + "/" + COMMERCIAL_JAR_NAME_PREFIX + SUFFIX).getPath());
                filePathList.add(new File(documentOnlineHomeDirPath + "/" + COMMUNITY_JAR_NAME_PREFIX + SUFFIX).getPath());
                for (File file : listFiles) {
                    if (!filePathList.contains(file.getPath())) {
                        boolean delete = file.delete();
                    }
                }
            }
        }
        try {
            List<String> filePathList = new ArrayList<>();
            filePathList.add(documentOnlineHomeDirPath + "/" + COMMERCIAL_JAR_NAME_PREFIX + SUFFIX);
            filePathList.add(documentOnlineHomeDirPath + "/" + COMMUNITY_JAR_NAME_PREFIX + SUFFIX);
            for (String filePath : filePathList) {
                boolean exists = FileUtil.exists(Config.FILE_HANDLER() + ":" + filePath);
                if (exists) {
                    Path targetPath = Paths.get(filePath);
                    File file = targetPath.toFile();
                    if (file.exists()) {
                        long dataLength = FileUtil.getDataLength(Config.FILE_HANDLER() + ":" + filePath);
                        if (dataLength == file.length()) {
                            continue;
                        } else {
                            boolean delete = file.delete();
                        }
                    }
                    try (InputStream inputStream = FileUtil.getData(Config.FILE_HANDLER() + ":" + filePath)) {
                        long length = Files.copy(inputStream, targetPath);
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        List<String> mappingJsonLocationPatternList = new ArrayList<>();
        List<String> mdFileLocationPatternList = new ArrayList<>();
        File[] listFiles = documentOnlineHome.listFiles();
        if (listFiles != null) {
            for (File file : listFiles) {
                mappingJsonLocationPatternList.add("jar:file:" + documentOnlineHomeDirPath + "/" + file.getName() + "!/neatlogic/**/documentonline-mapping.json");
                mdFileLocationPatternList.add("jar:file:" + documentOnlineHomeDirPath + "/" +  file.getName() + "!/neatlogic/**/*.md");
            }
            // 先查询出数据库中数据
            IDocumentOnlineCrossoverMapper documentOnlineCrossoverMapper = CrossoverServiceFactory.getApi(IDocumentOnlineCrossoverMapper.class);
            List<DocumentOnlineConfigVo> documentOnlineConfigList = documentOnlineCrossoverMapper.getAllDocumentOnlineConfigList();
            return DocumentOnlineManager.initializeIndex(documentOnlineConfigList, mappingJsonLocationPatternList, mdFileLocationPatternList);
        }
        return new JSONObject();
    }

    /**
     * 从war包内部加载在线文档
     * @return
     */
    public static JSONObject LoadDocumentsWithinWar() {
        String classpathRoot = "neatlogic/resources/**/" + DIRECTORY_ROOT + "/";
        String mappingJsonLocationPattern = "classpath*:" + classpathRoot + "**/documentonline-mapping.json";
        String mdFileLocationPattern = "classpath*:" + classpathRoot + "**/*.md";
        // 先查询出数据库中数据
        IDocumentOnlineCrossoverMapper documentOnlineCrossoverMapper = CrossoverServiceFactory.getApi(IDocumentOnlineCrossoverMapper.class);
        List<DocumentOnlineConfigVo> documentOnlineConfigList = documentOnlineCrossoverMapper.getAllDocumentOnlineConfigList();
        return DocumentOnlineManager.initializeIndex(documentOnlineConfigList, List.of(mappingJsonLocationPattern), List.of(mdFileLocationPattern));
    }

    public static JSONObject importJar(MultipartFile multipartFile) throws Exception {
        String documentOnlineHomeDirPath = Config.DATA_HOME() + OUTSIDE_WAR_DOCUMENTS_ONLINE_JARS;
        // neatlogic-document-online-0.4.0.0-SNAPSHOT.jar neatlogic-document-online-commercial-0.4.0.0-SNAPSHOT.jar
        String oldFileName = multipartFile.getOriginalFilename();
        if (StringUtils.isNotBlank(oldFileName) && oldFileName.endsWith(SUFFIX)) {
            if (oldFileName.startsWith(COMMERCIAL_JAR_NAME_PREFIX)) {
                JSONObject jsonObj = new JSONObject();
                String filePath = documentOnlineHomeDirPath + "/" + COMMERCIAL_JAR_NAME_PREFIX + SUFFIX;
                boolean exists = FileUtil.exists(Config.FILE_HANDLER() + ":" + filePath);
                if (exists) {
                    FileUtil.deleteData(Config.FILE_HANDLER() + ":" + filePath);
                    jsonObj.put("exists", true);
                }
                try (InputStream inputStream = multipartFile.getInputStream()) {
                    String path = FileUtil.saveData(inputStream, "application/zip", filePath);
                    jsonObj.put("path", path);
                }
                return jsonObj;
            } else if (oldFileName.startsWith(COMMUNITY_JAR_NAME_PREFIX)) {
                JSONObject jsonObj = new JSONObject();
                String filePath = documentOnlineHomeDirPath + "/" + COMMUNITY_JAR_NAME_PREFIX + SUFFIX;
                boolean exists = FileUtil.exists(Config.FILE_HANDLER() + ":" + filePath);
                if (exists) {
                    FileUtil.deleteData(Config.FILE_HANDLER() + ":" + filePath);
                    jsonObj.put("exists", true);
                }
                try (InputStream inputStream = multipartFile.getInputStream()) {
                    String path = FileUtil.saveData(inputStream, "application/zip", filePath);
                    jsonObj.put("path", path);
                }
                return jsonObj;
            } else {
                throw new DocumentOnlineJarNameIllegalException(oldFileName);
            }
        } else {
            throw new DocumentOnlineJarNameIllegalException(oldFileName);
        }
    }
}
