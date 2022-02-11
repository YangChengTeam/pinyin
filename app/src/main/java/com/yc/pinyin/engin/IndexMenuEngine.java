package com.yc.pinyin.engin;

import android.content.Context;

import com.alibaba.fastjson.TypeReference;

import com.yc.pinyin.domain.IndexMenuInfoWrapper;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import yc.com.rthttplibrary.bean.ResultInfo;


/**
 * Created by wanglin  on 2019/2/26 17:54.
 */
public class IndexMenuEngine extends BaseEngine {
    public IndexMenuEngine(Context context) {
        super(context);
    }

//    @Override
//    public String getUrl() {
//        return Config.INDEX_MENU_URL;
//    }

    public Observable<ResultInfo<IndexMenuInfoWrapper>> getIndexMenuInfo() {
//        return rxpost(new TypeReference<ResultInfo<IndexMenuInfoWrapper>>() {
//        }.getType(), null, true, true, true);

        return request.getIndexMenuInfo(appId).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }
}
