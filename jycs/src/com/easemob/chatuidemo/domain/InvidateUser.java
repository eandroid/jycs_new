package com.easemob.chatuidemo.domain;

import com.ab.db.orm.annotation.Column;
import com.ab.db.orm.annotation.Id;
import com.ab.db.orm.annotation.Table;

@Table(name = "invidate_user")
public class InvidateUser {
    @Id
    @Column(name = "_id")
    private String _id;
    @Column(name="userId")
    private String userId;
    @Column(name="tag_gyx")
    private String isGYX = "0";//1 shi 0 bushi
    @Column(name="isYG")
    private String isYG ="0";
    @Column(name = "isLC")
    private String isLC = "0";
    @Column(name = "_url")
    private String _url;
    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    @Column(name = "name")
    private String name;
    @Column(name = "mobile")
    private String mobile;
    @Column(name = "category")
    private String category;
    @Column(name="screenname")
    private String screenName;//所在单位
    
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getIsGYX() {
        return isGYX;
    }

    public void setIsGYX(String isGYX) {
        this.isGYX = isGYX;
    }

    public String getIsYG() {
        return isYG;
    }

    public void setIsYG(String isYG) {
        this.isYG = isYG;
    }

    public String getIsLC() {
        return isLC;
    }

    public void setIsLC(String isLC) {
        this.isLC = isLC;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String get_url() {
        return _url;
    }

    public void set_url(String _url) {
        this._url = _url;
    }

}
