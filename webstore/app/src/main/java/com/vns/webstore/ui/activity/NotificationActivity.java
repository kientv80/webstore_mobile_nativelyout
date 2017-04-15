package com.vns.webstore.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.vns.webstore.middleware.entity.Article;
import com.vns.webstore.middleware.entity.NotifyInfo;
import com.vns.webstore.middleware.network.ConnectionManager;
import com.vns.webstore.middleware.service.WebstoreService;
import com.vns.webstore.middleware.storage.LocalStorageHelper;
import com.vns.webstore.middleware.utils.JSONHelper;
import com.vns.webstore.ui.adapter.ArticleAdapter;
import com.vns.webstore.ui.notification.Notification;
import com.webstore.webstore.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 19/03/2017.
 */

public class NotificationActivity extends AppCompatActivity {
    RecyclerView articlesListingView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LocalStorageHelper.init(getApplicationContext());
        ConnectionManager.isNetworkAvailable(getApplicationContext());
        setContentView(R.layout.view_notification_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ImageButton btnBack = (ImageButton) findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        articlesListingView = (RecyclerView)findViewById(R.id.recyclerview);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        articlesListingView.setLayoutManager(layoutManager);
        articlesListingView.setHasFixedSize(false);
        loadNotifications();

    }

    private void loadNotifications() {
        String notifications = LocalStorageHelper.getFromFile(WebstoreService.NOTIFICATIONS);
        if(notifications != null && !notifications.isEmpty()){
            try {
                JSONObject jsonObject = new JSONObject(notifications);
                if(jsonObject.has("data")){
                    List<NotifyInfo> notificationsList = JSONHelper.toObjects(jsonObject.getString("data"),NotifyInfo.class);
                    List<Article> articles = convertToArticles(notificationsList);
                    ArticleAdapter adapter = new ArticleAdapter(getApplicationContext(),articles);
                    adapter.setSupportWideLayout(false);
                    articlesListingView.setAdapter(adapter);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private List<Article> convertToArticles(List<NotifyInfo> notificationsList) {
        List<Article> articles = new ArrayList<>();
        for(NotifyInfo ni : notificationsList){
            Article art = new Article();
            art.setId(ni.getId());
            art.setTitle(ni.getTitle());
            art.setShotDesc(ni.getMessage());
            art.setUrl(ni.getUrl());
            art.setImageUrl(ni.getThemeUrl());
            art.setFromWebSite(ni.getFrom());
            art.setStrDate(ni.getDate());
            articles.add(art);
        }
        return articles;
    }
}
