package com.vns.webstore.middleware.entity;

/**
 * Created by LAP10572-local on 8/18/2016.
 */
public class Article implements Comparable{
    private String id;
    private String title;
    private String shotDesc;
    private String imageUrl;
    private String url;
    private String strDate;
    private String fromWebSite;
    private String websiteAvatar;
    private String type;
    private String parentCateName;
    private boolean mainArticle = false;
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getShotDesc() {
        return shotDesc;
    }

    public void setShotDesc(String shotDesc) {
        this.shotDesc = shotDesc;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getStrDate() {
        return strDate;
    }

    public void setStrDate(String strDate) {
        this.strDate = strDate;
    }

    public String getFromWebSite() {
        return fromWebSite;
    }

    public void setFromWebSite(String fromWebSite) {
        this.fromWebSite = fromWebSite;
    }

    public String getWebsiteAvatar() {
        return websiteAvatar;
    }

    public void setWebsiteAvatar(String websiteAvatar) {
        this.websiteAvatar = websiteAvatar;
    }



    public String getParentCateName() {
        return parentCateName;
    }

    public void setParentCateName(String parentCateName) {
        this.parentCateName = parentCateName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isMainArticle() {
        return mainArticle;
    }

    public void setMainArticle(boolean mainArticle) {
        this.mainArticle = mainArticle;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Article article = (Article) o;

        return id != null ? id.equals(article.id) : article.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public int compareTo(Object o) {
        long thisId = Long.parseLong(id);
        long thatId = Long.parseLong(((Article)o).getId());
        if(thisId > thatId)
            return -1;
        else if(thisId == thatId)
            return 0;
        else
            return 1;

    }
}
