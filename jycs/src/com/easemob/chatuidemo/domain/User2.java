package com.easemob.chatuidemo.domain;

import java.util.List;

import android.Manifest.permission;

import com.ab.db.orm.annotation.Column;
import com.ab.db.orm.annotation.Id;
import com.ab.db.orm.annotation.Relations;
import com.ab.db.orm.annotation.Table;
@Table(name = "user_local")
public class User2{
    @Id
    @Column(name="_id")
    private int _id;
    @Relations(name="imgfile",action="query_list",type="one2one",foreignKey="a_id")
	private UploadedFile avatar;//头像
	@Column(name="a_id")
	private String aId;
	private String notes;//备注
	@Column(name="code")
	private String code;//登录名
	@Column(name="name")
	private String name;//真实姓名
	@Column(name ="id")
	private String id;
	private String email;//邮箱
	@Column(name="mobile")
	private String mobile;//手机号
	@Column(name="gender")
	private String gender;//性别
	@Column(name="location")
	private String location;//省市区(下拉地址)
	@Column(name="category")
	private String category;//职务类别
	@Column(name="position")
	private String position;//职位
	@Column(name="address")
	private String address;//详细地址
	@Column(name="password")
	private String password;//数据库密码(加密后)
	@Column(name="plainPassword")
	private String plainPassword;//密码
	@Relations(name="im",action="query_list",foreignKey="im_id",type="one2one")
	private IM im;
	private List<Permission> permissions;
	private List<Unit> units;
	private String screenName;
	
    public String getScreenName() {
        return screenName;
    }
    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }
    public List<Unit> getUnits() {
        return units;
    }
    public void setUnits(List<Unit> units) {
        this.units = units;
    }
    public List<Permission> getPermissions() {
        return permissions;
    }
    public void setPermissions(List<Permission> permissions) {
        this.permissions = permissions;
    }
    public int get_id() {
        return _id;
    }
    public void set_id(int _id) {
        this._id = _id;
    }
    public UploadedFile getAvatar() {
        return avatar;
    }
    public void setAvatar(UploadedFile avatar) {
        this.avatar = avatar;
    }
    public String getaId() {
        return aId;
    }
    public void setaId(String aId) {
        this.aId = aId;
    }
    public String getNotes() {
        return notes;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getMobile() {
        return mobile;
    }
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
    public String getGender() {
        return gender;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }
    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public String getPosition() {
        return position;
    }
    public void setPosition(String position) {
        this.position = position;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getPlainPassword() {
        return plainPassword;
    }
    public void setPlainPassword(String plainPassword) {
        this.plainPassword = plainPassword;
    }
    public IM getIm() {
        return im;
    }
    public void setIm(IM im) {
        this.im = im;
    }
	
}
