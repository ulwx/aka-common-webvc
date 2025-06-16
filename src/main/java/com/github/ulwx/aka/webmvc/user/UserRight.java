package com.github.ulwx.aka.webmvc.user;

import java.time.LocalDateTime;

/*********************************************

***********************************************/
public class UserRight implements java.io.Serializable {

	private String Code;/*权限编码;len:60*/
	private String rightName;/*权限名称;len:90*/
	private String rightUrl;/*URL;len:240*/
	private String icon;/*icon;len:120*/
	private Integer enable;/*是否有效 0：无效  1：有效;len:10*/
	private Integer orderCode;/*排序码;len:10*/
	private LocalDateTime updateTime;/*更新时间;len:19*/
	private String updator;/*更新人;len:90*/

	public String getCode() {
		return Code;
	}

	public void setCode(String code) {
		Code = code;
	}

	public String getRightName() {
		return rightName;
	}

	public void setRightName(String rightName) {
		this.rightName = rightName;
	}

	public String getRightUrl() {
		return rightUrl;
	}

	public void setRightUrl(String rightUrl) {
		this.rightUrl = rightUrl;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public Integer getEnable() {
		return enable;
	}

	public void setEnable(Integer enable) {
		this.enable = enable;
	}

	public Integer getOrderCode() {
		return orderCode;
	}

	public void setOrderCode(Integer orderCode) {
		this.orderCode = orderCode;
	}

	public LocalDateTime getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(LocalDateTime updateTime) {
		this.updateTime = updateTime;
	}

	public String getUpdator() {
		return updator;
	}

	public void setUpdator(String updator) {
		this.updator = updator;
	}

	private static final long serialVersionUID =-22363175L;

}