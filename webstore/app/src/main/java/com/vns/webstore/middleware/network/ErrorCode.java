package com.vns.webstore.middleware.network;

/**
 * Created by root on 01/03/2017.
 */
public class ErrorCode {
    public ERROR_CODE getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ERROR_CODE errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public enum ERROR_CODE {NO_CONNECTION,CONNECTION_TIMEOUT,EXCEPTION,SUCCESSED}
    private ERROR_CODE errorCode;
    private String errorMsg;
    public ErrorCode(ERROR_CODE errorCode, String errorMsg){
        this.setErrorCode(errorCode);
        this.setErrorMsg(errorMsg);
    }
}
