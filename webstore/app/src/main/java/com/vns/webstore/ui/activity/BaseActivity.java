package com.vns.webstore.ui.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import com.vns.webstore.middleware.entity.Article;
import com.vns.webstore.middleware.entity.NotifyInfo;
import com.vns.webstore.middleware.entity.WebPage;
import com.vns.webstore.middleware.network.ConnectionManager;
import com.vns.webstore.middleware.network.ErrorCode;
import com.vns.webstore.middleware.network.HttpRequestListener;
import com.vns.webstore.middleware.service.ActivityLogService;
import com.vns.webstore.middleware.service.AppConfigService;
import com.vns.webstore.middleware.service.ProfileService;
import com.vns.webstore.middleware.service.TemplateService;
import com.vns.webstore.middleware.utils.JSONHelper;
import com.vns.webstore.openapi.CoreOpenAPIs;
import com.vns.webstore.openapi.View;
import com.vns.webstore.middleware.storage.LocalStorageHelper;
import com.vns.webstore.ui.dialog.TranslateDialog;
import com.vns.webstore.ui.dialog.UpdateDialog;
import com.webstore.webstore.R;
import com.webstore.webstore.entity.UserActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



/**
 * Created by LAP10572-local on 8/29/2016.
 */
public class BaseActivity extends AppCompatActivity {
    public static final String FINDAROUND = "findaround";
    public static final String KNOWLEDGE = "knowledge";
    public static final String MUSIC_FILM = "music_film";
    public static final String ENTERTAINMENTS = "entertainment";
    public static final String NEWS = "news";
    public static final String UTIL = "util";
    public static final String WEBSTORE = "webstore";
    public static final String UPDATE = "update";
    static Map<String, List<WebPage>> webpaes = new HashMap<>();
    FloatingActionButton floatingBtn;



    static String buildFeed() {
        String notify = LocalStorageHelper.getFromFile(UPDATE);
        if (notify != null && !notify.isEmpty()) {
            List<NotifyInfo> notifyInfo = JSONHelper.toObjects(notify, NotifyInfo.class);
            String template = TemplateService.getNotifyTemplate();
            StringBuilder html = new StringBuilder();
            html.append("<link rel=\"stylesheet\" href=\"file:///android_asset/style.css\">");
            html.append("<html><body class='body'> <div class='notifybody'>");
            for (NotifyInfo n : notifyInfo) {
                Formatter f = new Formatter();
                html.append(f.format(template, n.getFrom(), n.getDate(), n.getUrl(), n.getThemeUrl(), n.getUrl(), n.getTitle(), n.getMessage()));
            }
            html.append("</div></body></html>");
            return html.toString();
        }
        return "No new upate";
    }

    private static Article toArticle(NotifyInfo notifyInfo) {
        Article art = new Article();
        art.setUrl(notifyInfo.getUrl());
        art.setImageUrl(notifyInfo.getThemeUrl());
        art.setFromWebSite(notifyInfo.getFrom());
        art.setTitle(notifyInfo.getTitle());
        art.setShotDesc(notifyInfo.getMessage());
        return art;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        JSONObject data = new JSONObject();
        try {
            data.put("Activity", getClass().getSimpleName());
            ActivityLogService.getInstance().logUserActivity(new UserActivity("UIActivity", "OpenActivity", data.toString()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    ProgressDialog pd = null;

    void configWebView(WebView wv) {
        if (wv != null) {
            wv.addJavascriptInterface(new View(this), "Android");
            wv.addJavascriptInterface(new CoreOpenAPIs(this), "AndroidWebstoreAPIs");
            wv.getSettings().setJavaScriptEnabled(true);
            wv.getSettings().setDatabaseEnabled(true);
            wv.getSettings().setDomStorageEnabled(true);
            wv.getSettings().setAppCacheEnabled(true);
            wv.setScrollBarStyle(android.view.View.SCROLLBARS_INSIDE_OVERLAY);
            wv.setWebChromeClient(new WebChromeClient() {
            });
            wv.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    if (url.startsWith("http://360hay.com")) {
                        view.loadUrl(url);
                    } else {
                        Intent intent = new Intent(getApplicationContext(), OpenArticleActivity.class);
                        intent.putExtra("url", url);
                        intent.putExtra("article", false);
                        startActivity(intent);
                    }
                    return true;
                }

                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {

                    timerDelayRemoveDialog(1000, pd);
                }

                @Override
                public void onPageFinished(WebView view, String url) {

                    super.onPageFinished(view, url);
                }

                @Override
                public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                    final String mimeType = "text/html";
                    final String encoding = "UTF-8";
                    String html = "<link rel=\"stylesheet\" href=\"file:///android_asset/style.css\">  <b class='red_color'>Không thể mở nội dung</b></br>Vui lòng kiểm tra lại kết nối internet.";
                    view.loadDataWithBaseURL("file:///android_asset/", html, mimeType, encoding, "");
                }
            });
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                WebView.setWebContentsDebuggingEnabled(true);
            }
        }
    }

    public void timerDelayRemoveDialog(long time, final Dialog d) {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (d != null)
                    d.dismiss();
            }
        }, time);
    }

    public void animate(final WebView view, int mode) {
        Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), mode);
        view.startAnimation(anim);
    }

    public void init() {
        ConnectionManager.isNetworkAvailable(getApplicationContext());
        LocalStorageHelper.init(getApplicationContext());
        try {
            AppConfigService.getWebsiteinfo();//Load webinfo
            ProfileService.getProfile(getApplicationContext());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void enableFloatingActionButton(android.view.View.OnClickListener listener, boolean visible) {
        floatingBtn = (FloatingActionButton) findViewById(R.id.floatingBtn);
        floatingBtn.setOnClickListener(listener);
        if (!visible)
            floatingBtn.hide();
        else
            floatingBtn.show();
    }
}
