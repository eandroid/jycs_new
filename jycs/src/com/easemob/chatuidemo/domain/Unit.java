package com.easemob.chatuidemo.domain;

import java.util.HashSet;
import java.util.Set;

import com.ab.db.orm.annotation.Column;
import com.ab.db.orm.annotation.Table;

//组织节点
@Table(name="unit")
public class Unit{
    
    private Set<Unit> children = new HashSet<Unit>();
    
    private Unit parent;
    
    private Long left = 0L;

    private Long right = 0L;
    @Column(name="name")
    private String name;
    @Column(name="id")
    private long id;
    @Column(name="u_id")
    private String uId;
    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    private String uriName;
    
    private String Category;// 分类

    private Set<User2> users = new HashSet<User2>();

    private boolean enable;

    public Unit() {
        this.enable = true;
    }

    public Unit(String name) {
        this.name = name;
        this.uriName = name;
        this.enable = true;
    }

    // cascade:级联操作设置
    // @JsonIgnore
    public Set<Unit> getChildren() {
        return children;
    }

    protected void setChildren(Set<Unit> children) {
        this.children = children;
    }

    public Unit getParent() {
        return parent;
    }

    public void setParent(Unit parent) {
        this.parent = parent;
    }

    public Long getLeft() {
        return left;
    }

    public void setLeft(Long left) {
        this.left = left;
    }

    public Long getRight() {
        return right;
    }

    public void setRight(Long right) {
        this.right = right;
    }

    public String getName() {
        return name;
    }

    /* 设置上级组织Name改名 */
    public void setName(String name) {
        this.name = name;

        if (null == parent)
            setUriName(name);
        else
            setUriName(parent.getUriName() + "-" + name);
    }

    public String getUriName() {
        return uriName;
    }

    private void setUriName(String uriName) {
        this.uriName = uriName;

        for (Unit u : this.children) {
            u.setUriName(uriName + "-" + u.getName());
        }
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public Set<User2> getUsers() {
        return users;
    }

    public void setUsers(Set<User2> users) {
        this.users = users;
    }

    public boolean getEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    /*
     * add child
     */
    public void addChild(Unit unit) {
        unit.setUriName(this.getUriName() + "-" + unit.getName());
        this.children.add(unit);
        unit.setParent(this);
    }

    /*
     * remove child
     */
    public void removeChild(Unit unit) {
        this.children.remove(unit);
        unit.setParent(null);
    }

}
