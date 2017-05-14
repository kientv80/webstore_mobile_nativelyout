package com.vns.webstore.ui.activity;

/**
 * Created by root on 20/04/2017.
 */
public class ViewPagerInfo {
    public String getDataUrl() {
        return dataUrl;
    }

    public void setDataUrl(String dataUrl) {
        this.dataUrl = dataUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ViewType getViewType() {
        return viewType;
    }

    public void setViewType(ViewType viewType) {
        this.viewType = viewType;
    }

    enum ViewType{ArticleLayout,ListLayout}
    private String dataUrl;
    private String title;
    private ViewType viewType;
    public ViewPagerInfo(ViewType viewType,String dataUrl,String title){
        this.setViewType(viewType);
        this.setDataUrl(dataUrl);
        this.setTitle(title);
    }
}