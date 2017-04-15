package com.vns.webstore.middleware.entity;

/**
 * Created by LAP10572-local on 8/26/2016.
 */
public class WebPage {
    public WebPage(String title, String url){
        this.title = title;
        this.url = url;
    }
    private String title;
    private String url;
    private String htmlContent;
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHtmlContent() {
        return htmlContent;
    }

    public void setHtmlContent(String htmlContent) {
        this.htmlContent = htmlContent;
    }
}
