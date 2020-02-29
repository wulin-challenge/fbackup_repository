package com.bjhy.fbackup.client.core.domain;

/**
 * 罪犯的基本信息和媒体信息
 * select baseInfo.id as baseInfoId ,baseInfo.Bh as baseInfoBh,baseInfo.Dah baseInfoDah,baseInfo.Xm as baseInfoXm,
mediaInfo.Mturl as mediaInfoMturl,mediaInfo.Mtlbid as mediaInfoMtlbId,mediaInfo.MtlbName as mediaInfoMtlbName
from criminal_base_info baseInfo left join criminal_media_info mediaInfo on baseInfo.id = mediaInfo.Criminalbaseinfoid
where mediaInfo.Mturl is not null;
 * @author wubo
 */
public class BaseInfoMedaiInfo {
	
	/**
	 * 罪犯的Id
	 */
	private String baseInfoId;
	
	/**
	 * 罪犯的编号
	 */
	private String baseInfoBh;
	
	/**
	 * 罪犯的档案号
	 */
	private String baseInfoDah;
	
	/**
	 * 罪犯的姓名
	 */
	private String baseInfoXm;
	
	/**
	 * 罪犯的媒体url
	 */
	private String mediaInfoMturl;
	
	/**
	 * 罪犯媒体类型Id
	 */
	private String mediaInfoMtlbId;
	
	/**
	 * 罪犯的媒体类型名称
	 */
	private String mediaInfoMtlbName;

	public String getBaseInfoId() {
		return baseInfoId;
	}

	public void setBaseInfoId(String baseInfoId) {
		this.baseInfoId = baseInfoId;
	}

	public String getBaseInfoBh() {
		return baseInfoBh;
	}

	public void setBaseInfoBh(String baseInfoBh) {
		this.baseInfoBh = baseInfoBh;
	}

	public String getBaseInfoDah() {
		return baseInfoDah;
	}

	public void setBaseInfoDah(String baseInfoDah) {
		this.baseInfoDah = baseInfoDah;
	}

	public String getBaseInfoXm() {
		return baseInfoXm;
	}

	public void setBaseInfoXm(String baseInfoXm) {
		this.baseInfoXm = baseInfoXm;
	}

	public String getMediaInfoMturl() {
		return mediaInfoMturl;
	}

	public void setMediaInfoMturl(String mediaInfoMturl) {
		this.mediaInfoMturl = mediaInfoMturl;
	}

	public String getMediaInfoMtlbId() {
		return mediaInfoMtlbId;
	}

	public void setMediaInfoMtlbId(String mediaInfoMtlbId) {
		this.mediaInfoMtlbId = mediaInfoMtlbId;
	}

	public String getMediaInfoMtlbName() {
		return mediaInfoMtlbName;
	}

	public void setMediaInfoMtlbName(String mediaInfoMtlbName) {
		this.mediaInfoMtlbName = mediaInfoMtlbName;
	}
}
