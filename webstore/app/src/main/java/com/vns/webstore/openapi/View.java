package com.vns.webstore.openapi;

import android.content.Context;
import android.content.Intent;
import android.webkit.JavascriptInterface;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vns.webstore.middleware.entity.Profile;
import com.vns.webstore.middleware.service.ActivityLogService;
import com.vns.webstore.middleware.service.ProfileService;
import com.vns.webstore.ui.activity.OpenArticleActivity;
import com.webstore.webstore.R;
import com.vns.webstore.middleware.service.TemplateService;
import com.vns.webstore.middleware.entity.Article;
import com.webstore.webstore.entity.UserActivity;

import java.util.Formatter;

/**
 * Created by kientv on 8/17/2016.
 */
public class View {
    Context mContext;
    public View(Context context){
        mContext = context;
    }
    @JavascriptInterface
    public String openArticle(String article){
        Gson g = new Gson();
        Article articleObj = g.fromJson(article, Article.class);
        Intent intent = new Intent(mContext, OpenArticleActivity.class);
        String articleHtml = renderArticle(articleObj);
        intent.putExtra("articleHtml",articleHtml);
        intent.putExtra("url",articleObj.getUrl());
        intent.putExtra("shortDesc",articleObj.getShotDesc());
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        mContext.startActivity(intent);
        Profile p = ProfileService.getProfile(mContext);
        String id = "NA";
        if(p != null)
            id = p.getId();
        ActivityLogService.getInstance().logUserActivity(new UserActivity("open_article","ARTICLE",article));
        return articleHtml;
    }

    @JavascriptInterface
    public void renderArticles(String jsonArticles){
        JsonParser parser = new JsonParser();
        JsonElement jsArticles = parser.parse("jsonArticles");
        JsonArray articles  = jsArticles.getAsJsonArray();
        String template = TemplateService.getArticleTemplate();
        Formatter f = new Formatter();
        StringBuilder html = new StringBuilder();
        for(int i = 0; i< articles.size();i++){
            JsonObject article = articles.get(i).getAsJsonObject();
            html.append(f.format(template,article.get("imageUrl").toString(),article.get("title").toString(),article.get("type").toString(),article.get("strDate").toString(),article.get("fromWebSite").toString()));
        }
        Intent intent = new Intent(mContext, OpenArticleActivity.class);
        intent.putExtra("html",html.toString());
        mContext.startActivity(intent);
    }

    @JavascriptInterface
    public void renderProductItems(String jsonProductItems){

    }
    @JavascriptInterface
    public String getAppInfo(){
        return "WSAndroidApp_v1.0";
    }

    private String renderArticle(Article articleObj){
        String template = TemplateService.getArticleTemplate();
        Formatter f = new Formatter();
        String articleHtml = f.format(template,articleObj.getTitle(),articleObj.getStrDate(),articleObj.getFromWebSite(),articleObj.getShotDesc(),articleObj.getImageUrl(),mContext.getResources().getString(R.string.msg_web_loading)).toString();
        return articleHtml;
    }
}
