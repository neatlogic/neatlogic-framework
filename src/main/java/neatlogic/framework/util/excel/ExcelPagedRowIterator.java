/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
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
