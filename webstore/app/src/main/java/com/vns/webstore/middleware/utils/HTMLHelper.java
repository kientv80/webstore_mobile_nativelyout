package com.vns.webstore.middleware.utils;

import android.webkit.CookieManager;

/**
 * Created by LAP10572-local on 6/16/2016.
 */
public class HTMLHelper {
    public static void setCookie(String domain,String value){
        CookieManager.getInstance().setCookie(domain,value);
    }

}
