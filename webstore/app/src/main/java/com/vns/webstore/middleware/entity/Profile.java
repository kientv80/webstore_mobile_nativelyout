package com.vns.webstore.middleware.entity;

import java.io.Serializable;

/**
 * Created by LAP10572-local on 8/29/2016.
 */
public class Profile implements Serializable{

    private String id;
    private String name;
    private String avatar;
    private String phoneNum;

    public Profile(String id, String name, String avatar,String phoneNum){
        this.id = id;
        this.name = name;
        this.avatar = avatar;
        this.phoneNum = phoneNum;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }
}
