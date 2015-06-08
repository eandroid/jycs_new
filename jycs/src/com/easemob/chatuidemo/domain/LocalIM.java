package com.easemob.chatuidemo.domain;

import java.io.Serializable;

import com.ab.db.orm.annotation.Column;
import com.ab.db.orm.annotation.Id;
import com.ab.db.orm.annotation.Table;

@Table(name="localim")
public class LocalIM implements Serializable{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name="id")
    private int id;
    @Column(name="url")
    private String url;
    @Column(name="imName")
    private String imName;
    @Column(name="username")
    private String username;
    @Column(name="screenname")
    private String screenName;
    @Column(name="phonenumber")
    private String phonenumber;
    @Column(name="category")
    private String category;
    
    public String getScreenName() {
        return screenName;
    }
    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }
    public String getPhonenumber() {
        return phonenumber;
    }
    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getImName() {
        return imName;
    }
    public void setImName(String imName) {
        this.imName = imName;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    @Override
    public String toString() {
        return "LocalIM [id=" + id + ", url=" + url + ", imName=" + imName
                + ", username=" + username + "]";
    }
    
}
