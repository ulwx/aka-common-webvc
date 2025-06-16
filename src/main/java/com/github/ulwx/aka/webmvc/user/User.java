package com.github.ulwx.aka.webmvc.user;

import com.ulwx.tool.ObjectUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class User implements java.io.Serializable {

	private String id;/*用户id，流水号;len:10*/
	private String account;/*用户登录名称;len:40*/
	private String password;/*用户密码;len:80*/
	private String name;/*用户名称;len:20*/
	private String tel;/*电话;len:30*/
	private String sex;/*性别 男 ，女 ，未知;len:4*/
	private String phone;/*手机号码;len:20*/
	private LocalDate birthDay;/*生日;len:10*/
	private String nikeName;/*昵称;len:20*/
	private String email;/*邮件地址;len:30*/
	private String nation;/*民族;len:16*/
	private LocalDateTime addTime;/*添加时间;len:19*/
	private String picUrl;/*用户图片URL;len:128*/
	private String sign;/*个性签名;len:40*/
	private LocalDateTime updateTime;/*更新时间;len:19*/
	private String updator;/*添加人姓名;len:15*/
	private Integer enable;/*0：无效 1：有效;len:3*/

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setAccount(String account){
		this.account = account;
	}
	public String getAccount(){
		return account;
	}
	public void setPassword(String password){
		this.password = password;
	}
	public String getPassword(){
		return password;
	}
	public void setName(String name){
		this.name = name;
	}
	public String getName(){
		return name;
	}
	public void setTel(String tel){
		this.tel = tel;
	}
	public String getTel(){
		return tel;
	}
	public void setSex(String sex){
		this.sex = sex;
	}
	public String getSex(){
		return sex;
	}
	public void setPhone(String phone){
		this.phone = phone;
	}
	public String getPhone(){
		return phone;
	}
	public void setBirthDay(LocalDate birthDay){
		this.birthDay = birthDay;
	}
	public LocalDate getBirthDay(){
		return birthDay;
	}
	public void setNikeName(String nikeName){
		this.nikeName = nikeName;
	}
	public String getNikeName(){
		return nikeName;
	}
	public void setEmail(String email){
		this.email = email;
	}
	public String getEmail(){
		return email;
	}
	public void setNation(String nation){
		this.nation = nation;
	}
	public String getNation(){
		return nation;
	}
	public void setAddTime(LocalDateTime addTime){
		this.addTime = addTime;
	}
	public LocalDateTime getAddTime(){
		return addTime;
	}
	public void setPicUrl(String picUrl){
		this.picUrl = picUrl;
	}
	public String getPicUrl(){
		return picUrl;
	}
	public void setSign(String sign){
		this.sign = sign;
	}
	public String getSign(){
		return sign;
	}
	public void setUpdateTime(LocalDateTime updateTime){
		this.updateTime = updateTime;
	}
	public LocalDateTime getUpdateTime(){
		return updateTime;
	}
	public void setUpdator(String updator){
		this.updator = updator;
	}
	public String getUpdator(){
		return updator;
	}
	public void setEnable(Integer enable){
		this.enable = enable;
	}
	public Integer getEnable(){
		return enable;
	}

	public String toString(){
		return  ObjectUtils.toString(this);
	}

	private static final long serialVersionUID =417077740L;

}