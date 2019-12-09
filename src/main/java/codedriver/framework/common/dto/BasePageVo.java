package codedriver.framework.common.dto;

public class BasePageVo {
	private transient Boolean needPage = true;
	private transient Integer pageSize = 10;
	private transient Integer currentPage = 1;
	private transient Integer pageCount = 0;
	private transient Integer startNum;
	private transient Integer rowNum = 0;

	public BasePageVo() {
	}

	public Integer getRowNum() {
		return rowNum;
	}

	public void setRowNum(Integer rowNum) {
		this.rowNum = rowNum;
	}

	public Boolean getNeedPage() {
		return needPage;
	}

	public void setNeedPage(Boolean needPage) {
		this.needPage = needPage;
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

	public void setStartNum(Integer startNum) {
		this.startNum = startNum;
	}

	public Integer getPageCount() {
		return pageCount;
	}

	public void setPageCount(Integer pageCount) {
		this.pageCount = pageCount;
	}
}
