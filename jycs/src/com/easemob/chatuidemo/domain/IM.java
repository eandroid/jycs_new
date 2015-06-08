package com.easemob.chatuidemo.domain;

import com.ab.db.orm.annotation.Column;
import com.ab.db.orm.annotation.Id;
import com.ab.db.orm.annotation.Table;

/**
 * 环信实体类
 * @author 让火收了雨
 *
 */
@Table(name="im")
public class IM {
    @Id
    @Column(name="_id")
    private long _id;
    @Column(name="id")
	private Long id;
    @Column(name="username")
	private String username;//用户名
    @Column(name="password")
	private String password;//密码
    @Column(name="im_id")
    private String im_id;
    
	

    public String getIm_id() {
        return im_id;
    }

    public void setIm_id(String im_id) {
        this.im_id = im_id;
    }

    public IM() {
		super();
	}
	
	public IM(String username, String password) {
		super();
		this.username = username;
		this.password = password;
	}
	
	public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
}
