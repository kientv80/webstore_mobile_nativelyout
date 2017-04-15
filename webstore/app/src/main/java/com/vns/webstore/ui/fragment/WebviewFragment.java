package com.vns.webstore.ui.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.vns.webstore.middleware.network.ConnectionManager;
import com.webstore.webstore.R;

/**
 * Created by LAP10572-local on 8/26/2016.
 */
public class WebviewFragment extends WebviewBaseFragment {

    private String url;
    private String htmlContent;
    WebView webView = null;
    public void setUrl(String url){
        this.url = url;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        System.out.println("crate view " + url);
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.webview,container,false);
        webView = (WebView)viewGroup.findViewById(R.id.webview);
        configWebView(webView);
        if(ConnectionManager.isNetworkAvailable()) {
            if (htmlContent != null)
                webView.loadDataWithBaseURL("file:///android_asset/style.css", getHtmlContent(), MYTYPE, UTF_8, "");
            else if (url != null)
                webView.loadUrl(url);
        }else{
            webView.loadDataWithBaseURL("file:///android_asset/style.css", "Không có kết nối internet. Vui lòng kết nối Internet và thử lại sau.", MYTYPE, UTF_8, "");
        }
        return viewGroup;
    }
    @Override
    public void onDestroy() {
        System.out.println(">>>>>>>>>>>>>>>> onDestroy" + getUrl());
        super.onDestroy();
        if(webView == null)
            return;
        if(pd != null)
            pd.dismiss();

        webView.loadUrl("about:blank");
        webView.stopLoading();
        webView.setWebChromeClient(null);
        webView.setWebViewClient(null);
        webView.destroy();
        webView = null;
    }


    @Override
    public void onResume() {
        System.out.println(">>>>>>>>>>>>>>>> onResume" + getUrl());
        if(pd != null)
            pd.dismiss();
        super.onResume();
        webView.onResume();
    }

    @Override
    public void onPause() {
        System.out.println(">>>>>>>>>>>>>>>> onPause" + getUrl());
        if(pd != null)
            pd.dismiss();
        super.onPause();
        webView.onPause();
    }

    public String getUrl() {
        return url;
    }

    public String getHtmlContent() {
        return htmlContent;
    }

    public void setHtmlContent(String htmlContent) {
        this.htmlContent = htmlContent;
    }
}
