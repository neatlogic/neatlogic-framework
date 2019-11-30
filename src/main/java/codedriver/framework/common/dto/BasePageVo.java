package codedriver.framework.common.dto;

import java.util.ArrayList;
import java.util.List;

public class BasePageVo {
	private Boolean needPage = true;
	private Boolean needPageCount = true;
	private Integer pageSize = 10;
	private Integer currentPage = 1;
	private Integer startNum;
	private Integer pageSizePlus;
	private Integer pageCount = 0;
	private Integer limit = 20;
	private Integer rowNum = 0;

	public BasePageVo() {
	}


	public Integer getRowNum() {
		return rowNum;
	}

	public void setRowNum(Integer rowNum) {
		this.rowNum = rowNum;
	}

	public Boolean getNeedPageCount() {
		return needPageCount;
	}

	public void setNeedPageCount(Boolean needPageCount) {
		this.needPageCount = needPageCount;
	}

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	// easyui插件新增属性
	private Integer rows = 10;
	private Integer page;

	private List<Long> taskIdListFromIndex; // 全文检索id集合

	public Boolean getNeedPage() {
		return needPage;
	}

	public void setNeedPage(Boolean needPage) {
		this.needPage = needPage;
	}

	public Integer getPageSizePlus() {
		if (pageSize != null) {
			return pageSize + 1;
		}
		return pageSizePlus;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;

	}

	public Integer getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(Integer currentPage) {
		if (currentPage == null) {
			this.currentPage = 1;
		} else {
			this.currentPage = currentPage;
		}
	}

	public Integer getStartNum() {
		if (startNum == null) {
			startNum = Math.max((currentPage - 1) * pageSize, 0);
		}
		return startNum;
	}

	public List<Long> getTaskIdListFromIndex() {
		return taskIdListFromIndex;
	}

	public void setTaskIdListFromIndex(List<Long> taskIdListFromIndex) {
		this.taskIdListFromIndex = taskIdListFromIndex;
	}

	public void setStartNum(Integer startNum) {
		this.startNum = startNum;
	}

	public void addTaskIdFromIndex(Long taskId) {
		if (this.taskIdListFromIndex == null) {
			this.taskIdListFromIndex = new ArrayList<Long>();
		}
		this.taskIdListFromIndex.add(taskId);
	}

	public Integer getRows() {
		return rows;
	}

	public void setRows(Integer rows) {
		this.rows = rows;
		this.pageSize = rows;
	}

	public Integer getPage() {
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
		this.currentPage = page;
	}

	public Integer getPageCount() {
		return pageCount;
	}

	public void setPageCount(Integer pageCount) {
		this.pageCount = pageCount;
	}
}
