package com.vns.webstore.middleware.entity;

/**
 * Created by root on 23/02/2017.
 */

public class Websiteinfo {
    private String icon;
    private String name;
    private boolean isGoodImage;
    public boolean getIsGoodImage(){
        return isGoodImage;
    }
    public void setIsGoodImage(boolean isGoodImage){
        this.isGoodImage = isGoodImage;
    }
    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
