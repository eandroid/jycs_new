package com.easemob.chatuidemo.domain;



//Device:设备?
public class Device extends IdEntity {

	private String code;//
	
	private String machineId;//注册码?
	
	private User2 user;//用户信息
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMachineId() {
		return machineId;
	}

	public void setMachineId(String machineId) {
		this.machineId = machineId;
	}
	
	//@JsonIgnore:这个属性不进行json的转化，忽略这个属性的json转化。
	public User2 getUser() {
		return user;
	}

	public void setUser(User2 user) {
		this.user = user;
	}
}
