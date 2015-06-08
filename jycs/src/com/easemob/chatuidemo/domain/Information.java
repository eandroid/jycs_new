package com.easemob.chatuidemo.domain;

import java.util.Date;



/** 资讯 */

public class Information extends IdEntity {
	private String summary;// 副标题
	private String title;// 标题(不为空)
	private String address;// 地址
	private Date createTime;// 创建时间
	private Date updateTime;// 修改时间
	private Date deleteTime;// 删除时间
	private String content;// 正文
	private long userId;// 该咨询的发表用户的id
	private String type;// 类型,(slideshow or listshow)(非空)
	private UploadedFile photo;// 大图url
	private String url;
	
	public Information() {
		super();
	}

	public Information(String summary, String title, String address,
			String content, long userId, String type) {
		super();
		this.summary = summary;
		this.title = title;
		this.address = address;
		this.content = content;
		this.userId = userId;
		this.type = type;
	}

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Date getDeleteTime() {
        return deleteTime;
    }

    public void setDeleteTime(Date deleteTime) {
        this.deleteTime = deleteTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public UploadedFile getPhoto() {
        return photo;
    }

    public void setPhoto(UploadedFile photo) {
        this.photo = photo;
    }

	
}
