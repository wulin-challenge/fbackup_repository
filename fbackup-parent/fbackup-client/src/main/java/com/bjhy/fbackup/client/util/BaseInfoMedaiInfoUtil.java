package com.bjhy.fbackup.client.util;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.bjhy.fbackup.client.domain.BaseInfoMedaiInfo;
import com.bjhy.fbackup.client.statics.YzJdbcTemplate;
import com.bjhy.fbackup.client.statics.YzNamedParameterJdbcTemplate;
import com.bjhy.fbackup.common.extension.ExtensionLoader;
import com.bjhy.fbackup.common.util.RepositorySqlUtil;

/**
 * 得到罪犯的 BaseInfoMedaiInfo 工具类
 * @author 吴波
 */
@SuppressWarnings({"unchecked","deprecation"})
public class BaseInfoMedaiInfoUtil {
	
	private static final YzJdbcTemplate yzTemplate = ExtensionLoader.getInstance(YzJdbcTemplate.class);
	
	private static final YzNamedParameterJdbcTemplate yzNamed = ExtensionLoader.getInstance(YzNamedParameterJdbcTemplate.class);
	/**
	 * 当前页的数量
	 */
	public static final int currentPageNumber = 50;
	
	/**
	 * 基本sql
	 */
	private static String baseSql = "select baseInfo.id as baseInfoId ,baseInfo.Bh as baseInfoBh,baseInfo.Dah baseInfoDah,baseInfo.Xm as baseInfoXm,"
					+" mediaInfo.Mturl as mediaInfoMturl,mediaInfo.Mtlbid as mediaInfoMtlbId,mediaInfo.MtlbName as mediaInfoMtlbName"
					+" from criminal_base_info baseInfo left join criminal_media_info mediaInfo on baseInfo.id = mediaInfo.Criminalbaseinfoid"
					+" where mediaInfo.Mturl is not null";
	
	/**
	 * 得到分页的数据
	 * @return
	 */
	public static List<BaseInfoMedaiInfo> getPageListData(int currentPage){
		int startValue = getStartValue(currentPage, currentPageNumber);
		int endValue = getEndValue(currentPage, currentPageNumber);
		
		String pageSql = "SELECT * FROM ("
			+" SELECT rownum r,a.*"
			+" FROM ("+baseSql+") a"
			+" ) b"
			+" WHERE b.r>="+startValue+" AND b.r<="+endValue;
		List<Map<String, Object>> queryForList = yzNamed.queryForList(pageSql,Collections.EMPTY_MAP);
		List<BaseInfoMedaiInfo> listMaptoListEntity = RepositorySqlUtil.listMaptoListEntity(BaseInfoMedaiInfo.class, queryForList);
		return listMaptoListEntity;
	}
	
	/**
	 * 得到开始值
	 * @param currentPage
	 * @param perPageTotal
	 * @return
	 */
	private static int getStartValue(int currentPage,int perPageTotal){
		int startValue = perPageTotal*(currentPage-1)+1;
		return startValue;
	}
	
	private static int getEndValue(int currentPage,int perPageTotal){
		int endValue = perPageTotal*currentPage;
		return endValue;
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
	
	/**
	 * 得到数据的总数量
	 * @return
	 */
	public static int getDataTotalNumber(){
		String countSql = "select count(1) from ("+baseSql+")";
		
//		int queryForInt = yzTemplate.queryForInt(countSql);
		Integer queryForObject = yzTemplate.queryForObject(countSql, int.class);
		return queryForObject;
	}
	
	

}
