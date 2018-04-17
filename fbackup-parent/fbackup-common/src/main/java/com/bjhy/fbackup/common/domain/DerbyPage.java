package com.bjhy.fbackup.common.domain;

import com.bjhy.fbackup.common.util.ConstantUtil;

/**
 * derby分页实体
 * @author wubo
 */
public class DerbyPage {
	
	/**
	 * 数据的总数
	 */
	private int dataTotal;
	
	/**
	 * 每页的数量
	 */
	private int perPageNumber = ConstantUtil.DERBY_PAGE_PER_PAGE_NUMBER;
	
	/**
	 * 总的分页数
	 */
	private int totalPageNumber;

	public int getDataTotal() {
		return dataTotal;
	}

	public void setDataTotal(int dataTotal) {
		this.dataTotal = dataTotal;
	}

	public int getPerPageNumber() {
		return perPageNumber;
	}

	public void setPerPageNumber(int perPageNumber) {
		this.perPageNumber = perPageNumber;
	}

	public int getTotalPageNumber() {
		return totalPageNumber;
	}

	public void setTotalPageNumber(int totalPageNumber) {
		this.totalPageNumber = totalPageNumber;
	}
}
