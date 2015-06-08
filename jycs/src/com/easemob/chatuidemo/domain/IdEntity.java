package com.easemob.chatuidemo.domain;


public abstract class IdEntity {

	private Long id;
	//@GeneratedValue:主键生成策略
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
}