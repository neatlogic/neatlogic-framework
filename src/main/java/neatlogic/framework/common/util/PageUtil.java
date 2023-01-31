package neatlogic.framework.common.util;

public class PageUtil {
	public static int getPageCount(int rowNum, int pageSize) {
		int pageCount = rowNum / pageSize + (rowNum % pageSize > 0 ? 1 : 0);
		return pageCount;
	}
}
