package com.easemob.chatuidemo.domain.dao;

import java.io.Serializable;
import java.util.Date;

import com.ab.db.orm.annotation.Column;
import com.ab.db.orm.annotation.Id;
import com.ab.db.orm.annotation.Relations;
import com.ab.db.orm.annotation.Table;
import com.easemob.chatuidemo.domain.UploadedFile;

/** 门店实体类 */
@Table(name = "store")
public class Store implements Serializable  {
    /**
     * 
     */
    private static final long serialVersionUID = -4009355581570423605L;
    // ID @Id主键,int类型,数据库建表时此字段会设为自增长
    @Id
    @Column(name = "_id")
    private int _id;
    @Column(name = "id")
    private long id;
    @Column(name = "url")
    private String url;
    @Column(name = "storeName")
    private String storeName;// 门店名(非空唯一)
    @Column(name = "summary")
    private String summary;// 副标题
    @Column(name = "address")
    private String address;// 门店地址(非空)
    @Column(name = "province")
    private String province;// 省份
    @Column(name = "phone")
    private String phone;// 联系方式(非空)
    @Column(name = "introduction")
    private String introduction;// 门店简介
    @Relations(action = "query_insert", foreignKey = "u_id", name = "imgfile", type = "one2one")
    private UploadedFile photo;// 大图url
    @Column(name="u_id")
    private String uId;
    @Column(name = "createTime")
    private Date createTime;// 创建时间
    @Column(name = "updateTime")
    private Date updateTime;// 修改时间
    @Column(name = "deleteTime")
    private Date deleteTime;// 删除时间
    @Column(name = "type")
    private String type;// 类型:slideshow or listshow

    public String getuId() {
        return uId;
    }

    @Override
    public String toString() {
        return "Store [_id=" + _id + ", id=" + id + ", url=" + url
                + ", storeName=" + storeName + ", summary=" + summary
                + ", address=" + address + ", province=" + province
                + ", phone=" + phone + ", introduction=" + introduction
                + ", photo=" + photo + ", uId=" + uId + ", createTime="
                + createTime + ", updateTime=" + updateTime + ", deleteTime="
                + deleteTime + ", type=" + type + "]";
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public UploadedFile getPhoto() {
        return photo;
    }

    public void setPhoto(UploadedFile photo) {
        this.photo = photo;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

}
