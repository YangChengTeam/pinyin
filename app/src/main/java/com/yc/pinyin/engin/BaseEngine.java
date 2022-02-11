package com.yc.pinyin.engin;

import android.content.Context;

import com.yc.pinyin.httpinterface.HttpRequestInterface;

import yc.com.rthttplibrary.request.RetrofitHttpRequest;

/**
 * Created by suns  on 2020/4/7 09:20.
 */
public class BaseEngine {
    protected Context mContext;
    protected HttpRequestInterface request;
    protected String appId = "6";

    public BaseEngine(Context context) {
        this.mContext = context;
        request = RetrofitHttpRequest.get(context).create(HttpRequestInterface.class);
    }
}
