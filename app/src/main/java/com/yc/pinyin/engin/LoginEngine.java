package com.yc.pinyin.engin;

import android.content.Context;

import com.alibaba.fastjson.TypeReference;

import com.yc.pinyin.domain.LoginDataInfo;


import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import yc.com.rthttplibrary.bean.ResultInfo;

/**
 * Created by zhangkai on 2017/2/20.
 */

public class LoginEngine extends BaseEngine {
    public LoginEngine(Context context) {
        super(context);
    }

//    @Override
//    public String getUrl() {
//        return Config.INIT_URL;
//    }

    public Observable<ResultInfo<LoginDataInfo>> rxGetInfo() {
//        return rxpost(new TypeReference<ResultInfo<LoginDataInfo>>() {
//        }.getType(), null, true, true, true);
        return request.getIndexInfo(appId).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }
}
