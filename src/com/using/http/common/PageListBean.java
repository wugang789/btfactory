package com.using.http.common;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("rawtypes")
public class PageListBean {
	private int currentPage = 1; // 当前页
	private int rowsCountPerPage = 5; // 每页的数据条数
	private int totalPage = 0; // 共有多少页
	private int totalCount = 0; // 共有多少行数据
	private List dataList = null; // 待分页的数据
	private List tempDataList = null; // 每页的数据

	@SuppressWarnings("unchecked")
	public List getPaper(List dataList/* 待分页的数据 */, int rowsCount/* 每页显示的行数 */) {
		initPageList(dataList, rowsCount);
		tempDataList = new ArrayList();
		// currentPage * rowsCountPerPage 定位到当前页的数据数
		// currentPage * rowsCountPerPage - rowsCountPerPage 定位到当前页的第一条数据
		for (int i = currentPage * rowsCountPerPage - rowsCountPerPage; i < currentPage * rowsCountPerPage; i++) {
			if (i >= totalCount)
				break;
			tempDataList.add(dataList.get(i));
		}
		return tempDataList;
	}

	// 待分页的数据,设置记录数,每页记录数,总页数
	private void initPageList(List dataList, int rowsCount) {
		this.dataList = dataList;
		totalCount = dataList.size();
		rowsCountPerPage = rowsCount;
		if (totalCount % rowsCountPerPage == 0) {
			totalPage = totalCount / rowsCountPerPage;
		} else {
			totalPage = totalCount / rowsCountPerPage + 1;
		}
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public int getRowsCountPerPage() {
		return rowsCountPerPage;
	}

	public void setRowsCountPerPage(int rowsCountPerPage) {
		this.rowsCountPerPage = rowsCountPerPage;
	}

	public int getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public List getDataList() {
		return dataList;
	}

	public void setDataList(List dataList) {
		this.dataList = dataList;
	}

	public static void main(String[] args) {

		List da = new ArrayList();
		for (int i = 0; i < 25; i++) {
			da.add(i + 1);
		}

		da.remove(0);
		da.remove(1);
		da.remove(2);

		PageListBean bean = new PageListBean();

		bean.setCurrentPage(1);
		List pagerList = bean.getPaper(da, 5);

		for (int b = 0; b < pagerList.size(); b++) {
			System.out.println(pagerList.get(b));

		}
	}

}
