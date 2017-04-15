package com.vns.webstore.ui.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.vns.webstore.middleware.network.ConnectionManager;
import com.vns.webstore.middleware.service.ProfileService;
import com.webstore.webstore.R;

import com.vns.webstore.ui.activity.OpenArticleActivity;
import com.vns.webstore.openapi.View;
import com.vns.webstore.openapi.CoreOpenAPIs;

/**
 * Created by LAP10572-local on 8/29/2016.
 */
public class WebviewBaseFragment extends Fragment {
    public static final String MYTYPE = "text/html";
    public static final String UTF_8 = "UTF-8";
    public boolean isFirst;
    ProgressDialog pd = null;
    void configWebView(WebView wv) {
        if(wv != null) {
            wv.addJavascriptInterface(new View(getContext()),"Android");
            wv.addJavascriptInterface(new CoreOpenAPIs(getContext()),"AndroidWebstoreAPIs");
            wv.getSettings().setJavaScriptEnabled(true);
            wv.getSettings().setDatabaseEnabled(true);
            wv.getSettings().setDomStorageEnabled(true);
            wv.getSettings().setAppCacheEnabled(true);
            wv.setScrollBarStyle(android.view.View.SCROLLBARS_INSIDE_OVERLAY);
            //wv.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
            wv.setWebChromeClient(new WebChromeClient(){});
            wv.setWebViewClient(new WebViewClient(){
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    if(ConnectionManager.isNetworkAvailable()){
                        if(url.startsWith("http://360hay.com")){
                            if(url.contains("?")){
                                view.loadUrl(url + "&uid=" + ProfileService.getProfile().getId());
                            }else{
                                view.loadUrl(url+ "?uid=" + ProfileService.getProfile().getId());
                            }
                        }else{
                            Intent intent = new Intent(getContext(), OpenArticleActivity.class);
                            intent.putExtra("url",url);
                            intent.putExtra("article",false);
                            startActivity(intent);
                        }
                    }else{
                        view.loadDataWithBaseURL("file:///android_asset/style.css", "Không có kết nối internet. Vui lòng kết nối Internet và thử lại sau.", MYTYPE, UTF_8, "");
                    }

                    return true;
                }
                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon)
                {
                    if(isFirst == true) {
                        System.out.println("==================== isFirst true");
                        if(pd == null)
                            pd = ProgressDialog.show(getContext(), "", getResources().getString(R.string.msg_web_loading), true);
                        pd.show();
                        timerDelayRemoveDialog(4000, pd);
                    }
                }

                @Override
                public void onPageFinished(WebView view, String url)
                {
                    if(pd != null)
                        pd.dismiss();
                    super.onPageFinished(view, url);
                }
                @Override
                public void onReceivedError(WebView webView, WebResourceRequest request, WebResourceError error) {
                    final String mimeType = "text/html";
                    final String encoding = "UTF-8";
                    String html = "<link rel=\"stylesheet\" href=\"file:///android_asset/style.css\">  <b class='red_color'>Không thể mở nội dung</b></br>Vui lòng kiểm tra lại kết nối internet.";
                    webView.loadDataWithBaseURL("file:///android_asset/", html, mimeType, encoding, "");
                    super.onReceivedError(webView,request,error);
                }

                @Override
                public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                    super.onReceivedSslError(view, handler, error);
                    System.out.println("onReceivedSslError " + view.getUrl());
                }

                @Override
                public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                    super.onReceivedHttpError(view, request, errorResponse);
                    System.out.println("onReceivedHttpError " + view.getUrl());
                }
            });
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                WebView.setWebContentsDebuggingEnabled(true);
            }
        }
    }
    public void timerDelayRemoveDialog(long time, final Dialog d){
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if(d != null)
                    d.dismiss();
            }
        }, time);
    }
}
