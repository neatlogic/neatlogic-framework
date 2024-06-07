/*
 * Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package neatlogic.framework.util.excel;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class ExcelPagedRowIterator {
    private final Sheet sheet;
    private final int pageSize;
    private final int batch;
    private final int totalRows;


    public ExcelPagedRowIterator(Sheet sheet, int batch) {
        totalRows = sheet.getPhysicalNumberOfRows();
        this.batch = batch;
        this.sheet = sheet;
        this.pageSize = totalRows / batch + (totalRows % batch == 0 ? 0 : 1);
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getTotalRows() {
        return totalRows;
    }

    public List<Iterator<Row>> getPageIterators() {
        List<Iterator<Row>> pageIterators = new ArrayList<>(batch);
        for (int i = 0; i < batch; i++) {
            pageIterators.add(new PageIterator(i));
        }
        return pageIterators;
    }

    private class PageIterator implements Iterator<Row> {
        private final int targetPage;
        private final int startRow;
        private final int endRow;
        private int currentRow;

        public PageIterator(int targetPage) {
            this.targetPage = targetPage;
            this.startRow = targetPage * pageSize;
            this.endRow = Math.min(startRow + pageSize, totalRows);
            this.currentRow = startRow;
        }

        @Override
        public boolean hasNext() {
            return currentRow < endRow;
        }

        @Override
        public Row next() {
            if (!hasNext()) {
                throw new NoSuchElementException("End of current page");
            }
            Row row = sheet.getRow(currentRow);
            currentRow++;
            if(row.getRowNum() == 0){
                row = sheet.getRow(currentRow);
                currentRow++;
            }
            return row;
        }
    }

    public static void main(String[] args) {
        try (InputStream is = new FileInputStream("/Users/cocokong/Downloads/处理人地域分组.xls")) {
            Workbook wb = WorkbookFactory.create(is);
            Sheet sheet = wb.getSheetAt(0);
            int pageSize = 10; // Number of rows per page

            ExcelPagedRowIterator pagedRowIterator = new ExcelPagedRowIterator(sheet, pageSize);
            List<Iterator<Row>> pageIterators = pagedRowIterator.getPageIterators();

            for (int i = 0; i < pageIterators.size(); i++) {
                Iterator<Row> pageIterator = pageIterators.get(i);
                System.out.println("Page " + (i + 1) + ":");
                while (pageIterator.hasNext()) {
                    Row row = pageIterator.next();
                    System.out.println(row.getCell(4));  // Example: Print first cell of each row
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
