package com.vns.webstore.middleware.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LAP10572-local on 8/25/2016.
 */
public class CateItem {
    private String name;
    private String label;
    private String icon;
    private String url;
    private boolean openLink;
    private List<Article> articles = new ArrayList<>();
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public List<Article> getArticles() {
        return articles;
    }

    public void setArticles(List<Article> articles) {
        this.articles = articles;
    }

    public boolean isOpenLink() {
        return openLink;
    }

    public void setOpenLink(boolean openLink) {
        this.openLink = openLink;
    }
}
