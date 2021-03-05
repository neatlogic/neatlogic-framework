package codedriver.framework.fulltextindex.dao.mapper;

public interface FullTextIndexSchemaMapper {
    void createFullTextIndexContentTable(String moduleId);

    void createFullTextIndexFieldTable(String moduleId);

    void createFullTextIndexOffsetTable(String moduleId);
}
