package com.vns.webstore.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;
import com.vns.webstore.middleware.entity.Article;
import com.vns.webstore.middleware.entity.Websiteinfo;
import com.vns.webstore.middleware.service.ActivityLogService;
import com.vns.webstore.middleware.service.AppConfigService;
import com.vns.webstore.middleware.service.TemplateService;
import com.webstore.webstore.R;
import com.webstore.webstore.entity.UserActivity;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by root on 18/01/2017.
 */

public class ArticleAdapter extends RecyclerView.Adapter<ArticleViewHolder> {
    private List<Article> articles;
    private Context context;
    Map<String,Websiteinfo> websiteinfoMap = null;
    private boolean supportWideLayout=true;
    public ArticleAdapter(Context context,List<Article> articles) {
        this.context = context;
        this.setArticles(articles);
        try {
            websiteinfoMap = AppConfigService.getWebsiteinfo();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    String currentCate;
    Random randomGenerator = new Random();
    @Override
    public ArticleViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        System.out.println(">>>>>>>>>>>>>>>>>>>>>onCreateViewHolder " + i);
        ArticleViewHolder holder = null;
        /*
        if(randomGenerator.nextInt(10)%2 == 0 && supportWideLayout==true){
            View view  = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.article_wide_layout, viewGroup, false);
            holder = new ArticleViewHolder(view,true);
        }else{
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.article_layout, viewGroup, false);
            holder = new ArticleViewHolder(view,false);
        }*/
        View view  = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.article_container, viewGroup, false);
        holder = new ArticleViewHolder(view,true);
        return holder;
    }

    @Override
    public void onBindViewHolder(ArticleViewHolder articleViewHolder, int i) {

        if(websiteinfoMap == null || websiteinfoMap.isEmpty())
            try {
                websiteinfoMap = AppConfigService.getWebsiteinfo();
            } catch (JSONException e) {
                e.printStackTrace();
                ActivityLogService.getInstance().logUserActivity(new UserActivity("AppConfigService.getWebsiteinfo","ERROR",e.getMessage()));
            }
        Article article = getArticles().get(i);
        ViewGroup viewGroup =  (ViewGroup)articleViewHolder.view.findViewById(R.id.articleContainer);
        View view = null;
        viewGroup.removeAllViews();

        if(AppConfigService.isGoodImageWebsite(article.getFromWebSite()) && ( (article.getShotDesc()!=null && !article.getShotDesc().isEmpty()) || article.getTitle().length() > 100)) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.article_wide_layout, viewGroup, true);
            articleViewHolder.wideImage=true;
        }else{
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.article_layout, viewGroup, true);
            articleViewHolder.wideImage=false;
        }

        articleViewHolder.initUI(view);
        if(websiteinfoMap != null && websiteinfoMap.containsKey(article.getFromWebSite()) && !websiteinfoMap.get(article.getFromWebSite()).getIcon().isEmpty()){
            Picasso.with(context).load(websiteinfoMap.get(article.getFromWebSite()).getIcon()).placeholder(R.drawable.photos_icon).resize(80, 60).into(articleViewHolder.ownerAvatar);
        }else{
            Picasso.with(context).load(R.drawable.photos_icon).into(articleViewHolder.ownerAvatar);
        }
        articleViewHolder.ownerName.setText(article.getFromWebSite()+ "\n(" + article.getStrDate() + ")");
        //articleViewHolder.getType().setText(article.getType());
        Picasso.with(context).load(article.getImageUrl()).placeholder(R.drawable.photos_icon).into(articleViewHolder.articleImage);
        articleViewHolder.articleTitle.setText(article.getTitle());
        articleViewHolder.setUrl(article.getUrl());
        articleViewHolder.setArticleHtml(renderArticle(article));
        articleViewHolder.setArticle(article);
        if(articleViewHolder.articleDesc != null && article.getShotDesc() != null){
            articleViewHolder.articleDesc.setText(article.getShotDesc());
        }

    }

    @Override
    public int getItemCount() {
        return getArticles().size();
    }
    private String renderArticle(Article articleObj){
        String template = TemplateService.getArticleTemplate();
        Formatter f = new Formatter();
        if(articleObj.getShotDesc() == null)
            articleObj.setShotDesc("");

        String articleHtml = f.format(template,articleObj.getImageUrl(),articleObj.getTitle(),articleObj.getFromWebSite(),articleObj.getStrDate(),articleObj.getShotDesc(),this.context.getResources().getString(R.string.msg_web_loading)).toString();
        return articleHtml;
    }

    public List<Article> getArticles() {
        return articles;
    }

    public void setArticles(List<Article> articles) {
        this.articles = articles;
    }

    public boolean isSupportWideLayout() {
        return supportWideLayout;
    }

    public void setSupportWideLayout(boolean supportWideLayout) {
        this.supportWideLayout = supportWideLayout;
    }
}
