/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.util.excel;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ExcelVo {
    private List<SheetVo> sheetList;

    public static class SheetVo {
        private String name;
        private List<HeaderVo> headerList;
        //private List<Map<String, Object>> dataList;
        private List<RowVo> rowList;

        static class HeaderVo {
            private String name;
            private int index;

            public HeaderVo(String name, int index) {
                this.name = name;
                this.index = index;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public int getIndex() {
                return index;
            }

            public void setIndex(int index) {
                this.index = index;
            }
        }

        static class RowVo {
            static class ColumnVo {
                private Object value;
                private int index;

                public ColumnVo(Object value, int index) {
                    this.value = value;
                    this.index = index;
                }

                public Object getValue() {
                    return value;
                }

                public void setValue(Object value) {
                    this.value = value;
                }

                public int getIndex() {
                    return index;
                }

                public void setIndex(int index) {
                    this.index = index;
                }
            }

            public ColumnVo getColumnByIndex(int index) {
                if (CollectionUtils.isNotEmpty(columnList)) {
                    Optional<ColumnVo> op = columnList.stream().filter(d -> d.getIndex() == index).findFirst();
                    if (op.isPresent()) {
                        return op.get();
                    }
                }
                return null;
            }

            private List<ColumnVo> columnList;

            public void addColumn(ColumnVo columnVo) {
                if (columnList == null) {
                    columnList = new ArrayList<>();
                }
                columnList.add(columnVo);
            }

            public List<ColumnVo> getColumnList() {
                return columnList;
            }

            public void setColumnList(List<ColumnVo> columnList) {
                this.columnList = columnList;
            }
        }

        public int getHeaderIndex(String header) {
            if (CollectionUtils.isNotEmpty(this.headerList)) {
                Optional<HeaderVo> op = this.headerList.stream().filter(d -> Objects.equals(d.getName(), header)).findFirst();
                if (op.isPresent()) {
                    return op.get().getIndex();
                }
            }
            return -1;
        }

        public SheetVo(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<HeaderVo> getHeaderList() {
            return headerList;
        }

        public void setHeaderList(List<HeaderVo> headerList) {
            this.headerList = headerList;
        }

        public List<RowVo> getRowList() {
            return rowList;
        }

        public void setRowList(List<RowVo> rowList) {
            this.rowList = rowList;
        }

        public void addHeader(HeaderVo header) {
            if (this.headerList == null) {
                this.headerList = new ArrayList<>();
            }
            this.headerList.add(header);
        }

        public void addRow(RowVo rowVo) {
            if (this.rowList == null) {
                this.rowList = new ArrayList<>();
            }
            this.rowList.add(rowVo);
        }
    }

    public SheetVo addSheet(SheetVo sheetVo) {
        if (this.sheetList == null) {
            this.sheetList = new ArrayList<>();
        }
        this.sheetList.add(sheetVo);
        return sheetVo;
    }

    public List<SheetVo> getSheetList() {
        return sheetList;
    }

    public void setSheetList(List<SheetVo> sheetList) {
        this.sheetList = sheetList;
    }

    public JSONObject toJson() {
        JSONObject returnObj = new JSONObject();
        if (CollectionUtils.isNotEmpty(sheetList)) {
            for (SheetVo sheetVo : sheetList) {
                JSONArray sheetObj = new JSONArray();
                if (CollectionUtils.isNotEmpty(sheetVo.getHeaderList()) && CollectionUtils.isNotEmpty(sheetVo.getRowList())) {
                    for (SheetVo.RowVo rowVo : sheetVo.getRowList()) {
                        JSONObject rowObj = new JSONObject();
                        for (SheetVo.HeaderVo headerVo : sheetVo.getHeaderList()) {
                            SheetVo.RowVo.ColumnVo column = rowVo.getColumnByIndex(sheetVo.getHeaderIndex(headerVo.getName()));
                            if (column != null) {
                                rowObj.put(headerVo.getName(), column.getValue());
                            }
                        }
                        sheetObj.add(rowObj);
                    }
                }
                returnObj.put(sheetVo.getName(), sheetObj);
            }
        }
        return returnObj;
    }
}
