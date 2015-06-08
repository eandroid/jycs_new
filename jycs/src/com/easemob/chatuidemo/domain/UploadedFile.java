package com.easemob.chatuidemo.domain;

import java.io.Serializable;

import com.ab.db.orm.annotation.Column;
import com.ab.db.orm.annotation.Id;
import com.ab.db.orm.annotation.Table;

@Table(name = "imgfile")
public class UploadedFile implements Serializable  {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "_id")
    private int _id;
    @Column(name = "id")
    private Long id;
    @Column(name = "tName")
    private String tName;
    @Column(name = "photoDescription")
    private String photoDescription;
    @Column(name = "url")
    private String url;
    @Column(name = "thumbnailUrl")
    private String thumbnailUrl;
    @Column(name = "u_id")
    private String uId;
    @Column(name = "a_id")
    private String imgId;

    public String getImgId() {
        return imgId;
    }

    public void setImgId(String imgId) {
        this.imgId = imgId;
    }

    
    @Override
    public String toString() {
        return "UploadedFile [_id=" + _id + ", id=" + id + ", tName=" + tName
                + ", photoDescription=" + photoDescription + ", url=" + url
                + ", thumbnailUrl=" + thumbnailUrl + ", uId=" + uId
                + ", imgId=" + imgId + "]";
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String gettName() {
        return tName;
    }

    public void settName(String tName) {
        this.tName = tName;
    }

    public String getPhotoDescription() {
        return photoDescription;
    }

    public void setPhotoDescription(String photoDescription) {
        this.photoDescription = photoDescription;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

}
