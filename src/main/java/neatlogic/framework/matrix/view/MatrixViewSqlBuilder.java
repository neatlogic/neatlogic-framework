/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.framework.matrix.view;

import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.exception.core.ApiRuntimeException;
import neatlogic.framework.matrix.dto.MatrixViewAttributeVo;
import neatlogic.framework.matrix.exception.*;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.*;

public class MatrixViewSqlBuilder {

    private final String sql;
    private String viewName;
    private final Map<String, String> attrMap = new LinkedHashMap<>();

    public MatrixViewSqlBuilder(String xml) {
        try {
            Document document = DocumentHelper.parseText(xml);
            Element root = document.getRootElement();
            Element attrsElement = root.element("attrs");
            if (attrsElement == null) {
                throw new MatrixViewSettingFileIrregularException("attrs");
            }
            Element sqlElement = root.element("sql");
            if (sqlElement == null) {
                throw new MatrixViewSettingFileIrregularException("sql");
            }
            List<Element> attrElementList = attrsElement.elements("attr");
            if (CollectionUtils.isNotEmpty(attrElementList)) {
                for (Element attrE : attrElementList) {
                    if (StringUtils.isBlank(attrE.attributeValue("name"))) {
                        throw new MatrixViewSettingFileIrregularException("attrs->attr", "name");
                    }
                    if (StringUtils.isBlank(attrE.attributeValue("label"))) {
                        throw new MatrixViewSettingFileIrregularException("attrs->attr", "label");
                    }
                    attrMap.put(attrE.attributeValue("name"), attrE.attributeValue("label"));
                }
            } else {
                throw new MatrixViewSettingFileIrregularException("attrs->attr");
            }
            sql = sqlElement.getTextTrim();
        } catch (Exception ex) {
            throw new MatrixViewSettingFileIrregularException(ex);
        }
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public boolean valid() {
        try {
            Statement stmt = CCJSqlParserUtil.parse(sql);
            Select selectStatement = (Select) stmt;
            PlainSelect select = (PlainSelect) selectStatement.getSelectBody();
            final boolean[] hasId = new boolean[1];
            final boolean[] hasUuid = new boolean[1];
            Set<String> columnList = new HashSet<>();
            for (SelectItem selectItem : select.getSelectItems()) {
                selectItem.accept(new SelectItemVisitor() {
                    @Override
                    public void visit(AllColumns allColumns) {
                    }

                    @Override
                    public void visit(AllTableColumns allTableColumns) {
                    }

                    @Override
                    public void visit(SelectExpressionItem selectExpressionItem) {
                        String columnName;
                        if (selectExpressionItem.getAlias() != null) {
                            columnName = selectExpressionItem.getAlias().getName();
                        } else {
                            columnName = selectExpressionItem.toString();
                        }
                        if (columnName.equalsIgnoreCase("id")) {
                            hasId[0] = true;
                        } else if (columnName.equalsIgnoreCase("uuid")) {
                            hasUuid[0] = true;
                        }
                        columnList.add(columnName);
                    }
                });
            }
            if (!hasId[0]) {
                throw new MatrixViewSqlFieldNotExistsException("id");
            }
            if (!hasUuid[0]) {
                throw new MatrixViewSqlFieldNotExistsException("uuid");
            }
            if (CollectionUtils.isNotEmpty(columnList)) {
                for (String attrName : attrMap.keySet()) {
                    if (!columnList.contains(attrName)) {
                        throw new MatrixViewSqlFieldNotExistsException(attrName);
                    }
                }
            } else {
                throw new MatrixViewHasNoAttrException();
            }
        } catch (ApiRuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new MatrixViewSqlIrregularException(ex);
        }
        return true;
    }

    /**
     * 获取SQL测试语句
     *
     * @return 获取测试SQL语句，只会返回1行数据，用于检查表和字段名是否有异常
     */
    public String getTestSql() {
        try {
            this.valid();
            Statement stmt = CCJSqlParserUtil.parse(sql);
            Select selectStatement = (Select) stmt;
            PlainSelect select = (PlainSelect) selectStatement.getSelectBody();
            Limit limit = new Limit();
            limit.setRowCount(new LongValue(1L));
            select.setLimit(limit);
            return stmt.toString();
        } catch (ApiRuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new MatrixViewSqlIrregularException(ex);
        }
    }

    /**
     * 获取所有列
     *
     * @return 列名列表
     */
    public List<MatrixViewAttributeVo> getAttrList() {
        List<MatrixViewAttributeVo> attrList = new ArrayList<>();
        for (String attrName : attrMap.keySet()) {
            MatrixViewAttributeVo attrVo = new MatrixViewAttributeVo();
            attrVo.setType("text");
            attrVo.setName(attrName);
            attrVo.setLabel(attrMap.get(attrName));
            attrList.add(attrVo);
        }
        return attrList;
    }

    /**
     * 获取创建视图语句
     *
     * @return 创建视图语句
     */
    public String getCreateViewSql() {
        try {
            if (StringUtils.isBlank(viewName)) {
                throw new MatrixViewNameEmptyException();
            }
            return "CREATE OR REPLACE VIEW " + TenantContext.get().getDataDbName() + "." + viewName + " AS " + sql;

        } catch (ApiRuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new MatrixViewSqlIrregularException(ex);
        }
    }

}
