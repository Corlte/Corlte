package com.lzyp.common.http;

public abstract class HttpClientCallback {


    public abstract void onSuccess(int code, String msg);

    public void onError(int code,String msg) {

    }


}
