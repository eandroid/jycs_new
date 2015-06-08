package com.easemob.chatuidemo.domain;

import java.util.List;

import com.ab.db.orm.annotation.Column;
import com.ab.db.orm.annotation.Relations;
import com.ab.db.orm.annotation.Table;

/** 广场实体类 */
@Table(name = "square")
public class Square {
    @Column(name = "id")
    private String id;
    @Column(name = "type")
    private String type;// 广场类型:员工,联采,供应
    @Column(name = "information")
    private String information;// 信息
    @Column(name = "createTime")
    private String createTime;// 创建时间
    @Column(name = "updateTime")
    private String updateTime;// 修改时间
    @Column(name = "deleteTime")
    private String deleteTime;// 删除时间
    @Relations(action = "query_list", foreignKey = "img_id",name="imgfile",type="one2many")
    private List<UploadedFile> photos;// 对应的图片(多张)
    private Unit unit;
    private User2 user;
    @Column(name = "img_id")
    private String imgId;
    
    private User user2;
    
    public User getUser2() {
        return user2;
    }

    public void setUser2(User user2) {
        this.user2 = user2;
    }

    public Square() {
        super();
    }

    public Square(String type, String information, Unit unit, User2 user) {
        super();
        this.type = type;
        this.information = information;
        this.unit = unit;
        this.user = user;
    }

    
    public String getImgId() {
        return imgId;
    }

    public void setImgId(String imgId) {
        this.imgId = imgId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getDeleteTime() {
        return deleteTime;
    }

    public void setDeleteTime(String deleteTime) {
        this.deleteTime = deleteTime;
    }

    public List<UploadedFile> getPhotos() {
        return photos;
    }

    public void setPhotos(List<UploadedFile> photos) {
        this.photos = photos;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public User2 getUser() {
        return user;
    }

    public void setUser(User2 user) {
        this.user = user;
    }

}
