package com.bjhy.fbackup.common.util;

/**
 * 页数是从第一页开始,没有0页这个说法
 * derby数据库分页的工具类
 * @author wubo
 */
public class DerbyPageUtil {
	
	/**
	 * 得到跳过的数量
	 * @param currentPage 当前页
	 * @param perPageTotal 每页的总数
	 * @return
	 */
	public static int getSkipNumber(int currentPage,int perPageTotal){
		int skipNumber = (currentPage-1) * perPageTotal;
		return skipNumber;
	}
	
	/**
	 * 得到页数
	 * @param dataTotal 所有数据的总数
	 * @param perPageTotal 分页后每页的总数
	 * @return
	 */
	public static int getPageNumber(int dataTotal,int perPageTotal){
		if(perPageTotal == 0){
			return 0;
		}
		
		//余数
		int complementNumber = dataTotal%perPageTotal;
		
		if(complementNumber == 0){
			return (dataTotal/perPageTotal);
		}else{
			return (dataTotal/perPageTotal)+1;
		}
	}
}
