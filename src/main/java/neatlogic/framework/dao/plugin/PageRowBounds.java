/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.framework.dao.plugin;

import org.apache.ibatis.session.RowBounds;

public class PageRowBounds extends RowBounds {
    private int rowNum;

    public PageRowBounds() {

    }

    public PageRowBounds(int offset, int limit) {
        super(offset, limit);
    }

    public int getRowNum() {
        return rowNum;
    }

    public void setRowNum(int rowNum) {
        this.rowNum = rowNum;
    }
}
