/*Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

package neatlogic.module.framework.startup;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.documentonline.dto.DocumentOnlineConfigVo;
import neatlogic.framework.documentonline.dto.DocumentOnlineDirectoryVo;
import neatlogic.framework.startup.StartupBase;
import neatlogic.framework.util.$;
import neatlogic.module.framework.dao.mapper.doumentonline.DocumentOnlineMapper;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@Component
public class DocumentOnlineInitializeIndexHandler extends StartupBase {

    private final Logger logger = LoggerFactory.getLogger(DocumentOnlineInitializeIndexHandler.class);

    @javax.annotation.Resource
    private DocumentOnlineMapper documentOnlineMapper;
    /**
     * 在线帮助文档根目录
     */
    public final static String DIRECTORY_ROOT = "documentonline";
    /**
     * 在线帮助文档索引库位置
     */
    public final static String INDEX_DIRECTORY = System.getProperty("java.io.tmpdir") + File.separator + DIRECTORY_ROOT;
    /**
     * 在线帮助文档类路径根目录
     */
    private static String classpathRoot = "neatlogic/resources/**/" + DIRECTORY_ROOT + "/";
    /**
     * 用于存储documentonline-mapping.json配置文件中的数据，不同模块jar包中都可能存在documentonline-mapping.json配置文件，可能有多个
     */
    private static List<DocumentOnlineConfigVo> mappingConfigList = new ArrayList();

    public final static DocumentOnlineDirectoryVo DOCUMENT_ONLINE_DIRECTORY_ROOT = new DocumentOnlineDirectoryVo("root", false);

    @Override
    public String getName() {
        return "nmfs.documentonlineinitializeindexhandler.getname";
    }

    @Override
    public int executeForAllTenant() {
        IndexWriter indexWriter = null;
        try {
            // 先查询出数据库中数据
            List<DocumentOnlineConfigVo> documentOnlineConfigList = documentOnlineMapper.getAllDocumentOnlineConfigList();
            for (DocumentOnlineConfigVo documentOnlineConfigVo : documentOnlineConfigList) {
                documentOnlineConfigVo.setSource("database");
                mappingConfigList.add(documentOnlineConfigVo);
            }
            // 在documentonline-mapping.json配置文件中已设置在线文档文件路径集合，用于防止同个在线文档重复配置
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

            // 存储所有在线文档的路径集合，用于判断不同jar包中有相同路径的文档
            List<String> existingFilePathList = new ArrayList<>();
            // 1.创建分词器
            Analyzer analyzer = new IKAnalyzer(true);
            System.out.println($.t("nmfs.documentonlineinitializeindexhandler.executeforalltenant.system_out_println", INDEX_DIRECTORY));
            // 2.创建dir目录对象，目录对象表示索引库的位置
            Directory dir = MMapDirectory.open(Paths.get(INDEX_DIRECTORY));
            // 3.创建IndexWriterConfig对象，这个对象中指定切分词使用的分词器
            IndexWriterConfig config = new IndexWriterConfig(analyzer);
            // 4.创建IndexWriter输出流对象，指定输出的位置和使用的config初始化对象
            indexWriter = new IndexWriter(dir, config);
            // 5.删除之前索引数据，然后在重新生成
            indexWriter.deleteAll();
            // 1.采集数据
            Resource[] mdResources = resolver.getResources("classpath*:" + classpathRoot + "**/*.md");
            for (Resource resource : mdResources) {
                String filename = resource.getFilename().substring(0, resource.getFilename().length() - 3);
                int index = filename.indexOf(".");
                if (index != -1) {
                    String sort = filename.substring(0, index);
                    if (StringUtils.isNumeric(sort)) {
                        filename = filename.substring(index + 1);
                    }
                }
                String path = resource.getURL().getPath();
                int separatorIndex = path.indexOf("!/");
                String filePath = path.substring(separatorIndex + 2);
                if (existingFilePathList.contains(filePath)) {
                    logger.error($.t("nmfs.documentonlineinitializeindexhandler.executeforalltenant.error", filePath));
                    System.exit(1);
                }
                existingFilePathList.add(filePath);
                List<DocumentOnlineConfigVo> configList = new ArrayList<>();
                // 根据文件路径找到配置映射信息，分析出文件所属的模块组、菜单，定位的锚点
                List<DocumentOnlineConfigVo> mappingConfigs = getMappingConfigByFilePath(filePath);
                if (CollectionUtils.isNotEmpty(mappingConfigs)) {
                    for (DocumentOnlineConfigVo documentOnlineConfigVo : mappingConfigs) {
                        if (StringUtils.isNotBlank(documentOnlineConfigVo.getModuleGroup())) {
                            DocumentOnlineConfigVo configVo = new DocumentOnlineConfigVo(documentOnlineConfigVo);
                            configVo.setFilePath(filePath);
                            configList.add(configVo);
                        }
                    }
                }
                DocumentOnlineDirectoryVo directory = buildDirectory(DOCUMENT_ONLINE_DIRECTORY_ROOT, filePath, configList);
                // 读取文件内容
                StringWriter writer = new StringWriter();
                IOUtils.copy(resource.getInputStream(), writer, StandardCharsets.UTF_8);
                String str = writer.toString();

                Document document = new Document();
                // 创建域对象并且放入到文档中
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
        } finally {
            // 8.释放资源
            if (indexWriter != null) {
                try {
                    indexWriter.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
            // 禁止添加子节点
            DOCUMENT_ONLINE_DIRECTORY_ROOT.noAllowedAddChild();
        }
        return 1;
    }

    @Override
    public int sort() {
        return 10;
    }

    /**
     * 根据文件路径找到documentonline-mapping.json配置文件设置的映射信息
     *
     * @param filePath 文件路径
     * @return 映射信息
     */
    private List<DocumentOnlineConfigVo> getMappingConfigByFilePath(String filePath) {
        List<DocumentOnlineConfigVo> resultList = new ArrayList<>();
        List<String> filePathList = new ArrayList<>();
        List<String> directoryList = new ArrayList<>();
        String[] split = filePath.split("/");
        for (String directory : split) {
            directoryList.add(directory);
            if (directoryList.size() > 2) {
                filePathList.add(String.join("/", directoryList));
            }
        }
        if (CollectionUtils.isNotEmpty(filePathList)) {
            List<DocumentOnlineConfigVo> documentOnlineConfigList = documentOnlineMapper.getDocumentOnlineConfigListByFilePathList(filePathList);
            for (DocumentOnlineConfigVo documentOnlineConfigVo : documentOnlineConfigList) {
                documentOnlineConfigVo.setSource("database");
                resultList.add(documentOnlineConfigVo);
            }
        }
        for (DocumentOnlineConfigVo documentOnlineConfigVo : mappingConfigList) {
            if (filePath.startsWith(documentOnlineConfigVo.getFilePath())) {
                if (!resultList.contains(documentOnlineConfigVo)) {
                    resultList.add(documentOnlineConfigVo);
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
    private DocumentOnlineDirectoryVo buildDirectory(DocumentOnlineDirectoryVo root, String filePath, List<DocumentOnlineConfigVo> configList) {
        int directoryRootIndex = filePath.indexOf(DIRECTORY_ROOT);
        String path = filePath.substring(directoryRootIndex + DIRECTORY_ROOT.length() + 1);
        List<String> nameList = new ArrayList<>();
        DocumentOnlineDirectoryVo parent = root;
        String[] split = path.split("/");
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
                DocumentOnlineDirectoryVo child = new DocumentOnlineDirectoryVo(prefix, name, true, upwardNameList, filePath, configList);
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
}
