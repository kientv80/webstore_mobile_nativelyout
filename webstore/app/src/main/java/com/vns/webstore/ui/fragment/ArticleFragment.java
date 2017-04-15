package com.vns.webstore.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vns.webstore.middleware.entity.Article;
import com.vns.webstore.middleware.entity.CateItem;
import com.vns.webstore.middleware.network.ErrorCode;
import com.vns.webstore.middleware.network.HttpClientHelper;
import com.vns.webstore.middleware.network.HttpRequestListener;
import com.vns.webstore.middleware.service.ActivityLogService;
import com.vns.webstore.middleware.storage.LocalStorageHelper;
import com.vns.webstore.middleware.utils.JSONHelper;
import com.vns.webstore.middleware.worker.WebstoreBackgroundService;
import com.vns.webstore.ui.adapter.ArticleAdapter;
import com.webstore.webstore.R;
import com.webstore.webstore.entity.UserActivity;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by root on 30/01/2017.
 */

public class ArticleFragment extends Fragment implements HttpRequestListener {
    private List<Article> articleList;
    RecyclerView articlesListingView;
    private String url;
    private String title;
    boolean isLoading = false;
    boolean loadMore;
    int visibleThreshold = 5;
    private String name;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.recyclerview_listing,container,false);
        articlesListingView = (RecyclerView)viewGroup.findViewById(R.id.recyclerview);
        articlesListingView.setHasFixedSize(false);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        articlesListingView.setLayoutManager(layoutManager);
        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) articlesListingView.getLayoutManager();
        articlesListingView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int totalItemCount = linearLayoutManager.getItemCount();
                int lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                if (totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    loadMores();
                }
            }
        });
        loadArticles(container);
        return viewGroup;
    }
;
    public void reloadArticle(){
        loadArticles(null);
    }

    int currentIndex = 0;
    int totalArticles =0;
    private void loadMores(){
        ArticleAdapter adapter = (ArticleAdapter)articlesListingView.getAdapter();
        if(adapter == null && totalArticles == 0 || totalArticles != adapter.getItemCount()){
            totalArticles = adapter.getItemCount();
        }else{
            return;
        }
        currentIndex+=10;
        HttpClientHelper.executeHttpGetRequest(url + "?from=" + currentIndex , this, null);
    }
    public void loadArticles(ViewGroup container){
            currentIndex = 0;
            renderArticlesFromCache();
            String articles = LocalStorageHelper.getFromFile(getName());
            if(articles != null && !articles.isEmpty()){
                try {
                    JSONObject json = new JSONObject(articles.toString());
                    if(((System.currentTimeMillis() - json.getLong("cachedTime")) > (30*60*1000))){
                        HttpClientHelper.executeHttpGetRequest(url + "?from=" + currentIndex, this, name);
                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }else {
                System.out.println("Loading articles");
                HttpClientHelper.executeHttpGetRequest(url + "?from=" + currentIndex , this, name);
            }
    }
    private void renderArticlesFromCache(){
        String articles = LocalStorageHelper.getFromFile(getName());
        List<Article> listArticles = new ArrayList<>();
        if("tintuc".equals(getName())) {
            String updates = LocalStorageHelper.getFromFile(WebstoreBackgroundService.UPDATE);
            if (updates != null && !updates.isEmpty()) {
                try {
                    JSONObject json = new JSONObject(updates.toString());
                    if(json.has("data"))
                        listArticles = toArticles(json.getString("data"));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        if(articles != null && !articles.isEmpty()){
            try {
                JSONObject json = new JSONObject(articles.toString());
                if(json.has("data")) {
                    listArticles.addAll(toArticles(json.getString("data")));
                    renderArticle(listArticles);
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }
    @Override
    public void onResume() {
        System.out.println("ArticleFragment onResume");
        if((System.currentTimeMillis() - pausedTime) > 30*60*1000){
            System.out.println("ArticleFragment onResume reload article");
            super.onResume();
            currentIndex = 0;
            HttpClientHelper.executeHttpGetRequest(url + "?from=" + currentIndex , this, name);
        }else{
            super.onResume();
        }
    }
    long pausedTime;
    @Override
    public void onPause() {
        super.onPause();
        pausedTime = System.currentTimeMillis();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        currentIndex = 0;
    }

    public List<Article> getArticleList() {
        return articleList;
    }

    public void setArticleList(List<Article> articleList) {
        this.articleList = articleList;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public void onRecievedData(Object data,final ErrorCode errorCode) {
        if (data != null && !data.toString().isEmpty()) {
            List<Article> articles = toArticles(data);
            renderArticle(articles);
        }
        if(errorCode != null && errorCode.getErrorCode() != ErrorCode.ERROR_CODE.SUCCESSED){
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast notify = Toast.makeText(getContext(),errorCode.getErrorMsg(),Toast.LENGTH_LONG);
                    notify.show();
                }
            });
            renderArticlesFromCache();
        }
    }

    private void renderArticle(final List<Article> articles ) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    Set<Article> uniqueArts = new HashSet<>(articles);
                    List<Article>  sortUniqueArticles = new ArrayList<Article>(uniqueArts);
                    Collections.sort(sortUniqueArticles);
                    ArticleAdapter adapter = (ArticleAdapter)articlesListingView.getAdapter();
                    if(adapter == null){
                        adapter = new ArticleAdapter(getContext(),sortUniqueArticles);
                        articlesListingView.setAdapter(adapter);
                    }else {
                        int itemCount = adapter.getItemCount();
                        if(currentIndex == 0){
                            adapter.getArticles().clear();
                            articlesListingView.getAdapter().notifyItemRangeRemoved(0,itemCount);
                            adapter.getArticles().addAll(sortUniqueArticles);
                            articlesListingView.getAdapter().notifyItemRangeInserted(0,sortUniqueArticles.size());
                        }else{
                            adapter.getArticles().addAll(sortUniqueArticles);
                            articlesListingView.getAdapter().notifyItemRangeInserted(itemCount,sortUniqueArticles.size()+itemCount);
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    ActivityLogService.getInstance().logUserActivity(new UserActivity(getClass().getSimpleName()+".renderArticle","ERROR",ex.getMessage()));
                }
            }
        });
    }
    private List<Article> toArticles(Object data){
        List<Article> articles = new ArrayList<>();
        try {
            JsonParser parser = new JsonParser();
            JsonObject jsonData = parser.parse(data.toString()).getAsJsonObject();
            if(jsonData.has("categories")) {
                JsonArray categories = jsonData.getAsJsonArray("categories");
                for (int i = 0; i < categories.size(); i++) {
                    JsonObject category = categories.get(i).getAsJsonObject();
                    if(category.has("news")) {
                        JsonArray news = category.getAsJsonArray("news");
                        for (int j = 0; j < news.size(); j++) {
                            articles.add((Article) JSONHelper.toObject(news.get(j).toString(), Article.class));
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            ActivityLogService.getInstance().logUserActivity(new UserActivity(getClass().getSimpleName()+".toArticles","ERROR",e.getMessage()));
        }
        return articles;
    }
    private List<CateItem> groupByCategory(List<Article> articles) {
        Map<String,CateItem> categories = new HashMap<>();
        for(Article art : articles){
            if(!categories.containsKey(art.getParentCateName())){
                categories.put(art.getParentCateName(),new CateItem());
                art.setMainArticle(true);
            }
            categories.get(art.getParentCateName()).getArticles().add(art);

        }
        return new ArrayList<>(categories.values());
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
