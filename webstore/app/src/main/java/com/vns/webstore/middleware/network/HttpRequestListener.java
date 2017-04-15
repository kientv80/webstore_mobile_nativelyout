package com.vns.webstore.middleware.network;

/**
 * Created by LAP10572-local on 7/14/2016.
 */
public interface HttpRequestListener {
    public void onRecievedData(Object data, ErrorCode errorCode);
}
