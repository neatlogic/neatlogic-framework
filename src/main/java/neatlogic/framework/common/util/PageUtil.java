package neatlogic.framework.common.util;

import neatlogic.framework.common.dto.BasePageVo;

import java.util.ArrayList;
import java.util.List;

public class PageUtil {
	public static int getPageCount(int rowNum, int pageSize) {
		int pageCount = rowNum / pageSize + (rowNum % pageSize > 0 ? 1 : 0);
		return pageCount;
	}

	/**
	 * 获取当前页数据
	 * @param list
	 * @param searchVo
	 * @return
	 * @param <E>
	 */
	public static <E> List<E> subList(List<E> list, BasePageVo searchVo) {
		List<E> resultList = new ArrayList<>();
		int rowNum = list.size();
		if (rowNum == 0) {
			return resultList;
		}
		searchVo.setRowNum(rowNum);
		int fromIndex = searchVo.getStartNum();
		if (fromIndex < rowNum) {
			int toIndex = fromIndex + searchVo.getPageSize();
			toIndex = toIndex > rowNum ? rowNum : toIndex;
			resultList = list.subList(fromIndex, toIndex);
		}
		return resultList;
	}
}
