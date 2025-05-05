package com.lzyp.common.http;

import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.lzyp.common.CommonAppConfig;
import com.lzyp.common.http.fuel.FuelHttpClient;
import com.lzyp.common.utils.LanguageUtil;

import java.io.IOException;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by http://www.yunbaokj.com on 2023/3/15.
 */
public class HttpClient {

    private static HttpClient sInstance;
    private final IHttpClient mHttpClientImpl;

    private HttpClient() {
        mHttpClientImpl = new FuelHttpClient();
    }

    public static HttpClient getInstance() {
        if (sInstance == null) {
            synchronized (HttpClient.class) {
                if (sInstance == null) {
                    sInstance = new HttpClient();
                }
            }
        }
        return sInstance;
    }

    public IHttpRequest get(String serviceName, String tag) {
        return mHttpClientImpl.get(serviceName, tag).params(CommonHttpConsts.LANGUAGE, LanguageUtil.getInstance().getLanguage());
    }

    public IHttpRequest get(String serviceName, String tag, Map<String, String> params) {
        IHttpRequest req = this.get(serviceName, tag);
        if (params != null && params.size() > 0) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                req.params(entry.getKey(), entry.getValue());
            }
        }
        req.params(CommonHttpConsts.LANGUAGE, LanguageUtil.getInstance().getLanguage());
        return req;
    }

    public IHttpRequest post(String serviceName, String tag) {
        return mHttpClientImpl.post(serviceName, tag).params(CommonHttpConsts.LANGUAGE, LanguageUtil.getInstance().getLanguage());
    }

    public IHttpRequest post(String serviceName, String tag, Map<String, String> paramMap) {
        IHttpRequest req = this.post(serviceName, tag);
        if (paramMap != null && paramMap.size() > 0) {
            for (Map.Entry<String, String> entry : paramMap.entrySet()) {
                req.params(entry.getKey(), entry.getValue());
            }
        }
        req.params(CommonHttpConsts.LANGUAGE, LanguageUtil.getInstance().getLanguage());
        return req;
    }

    public IHttpRequest getString(String url, String tag) {
        return mHttpClientImpl.getString(url, tag);
    }

    public IHttpRequest getFile(String url, String tag) {
        return mHttpClientImpl.getFile(url, tag);
    }

    public void cancel(String tag) {
        mHttpClientImpl.cancel(tag);
    }

    public void postForString(String path, Map<String, String> orderParams, HttpClientCallback callback) {


        // 创建 OkHttpClient 实例
        OkHttpClient client = new OkHttpClient();

        // 构建请求体
        String jsonBody = JSONObject.toJSONString(orderParams);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonBody);

        // 构建请求
        Request request = new Request.Builder()
                .url(CommonAppConfig.HOST+path)
                .post(requestBody)
                .addHeader("Content-Type", "application/json")
                .build();

        // 发送请求
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code: " + response);
            // 获取响应体
            Response response1 = response;
            String responseBody = response1.body().string();
            int code = response1.code();
            Log.i("postForString","path:"+path);
            Log.i("postForString","code:"+code);
            Log.i("postForString","result:"+responseBody);
            if(code==200){
                callback.onSuccess(code,responseBody);
            }else{
                callback.onSuccess(code,responseBody);
            }
        } catch (Exception e) {
            Log.e("dopay","request failed",e);
            callback.onSuccess(500,e.getMessage());
        }




    }

}
