package com.yc.pinyin.engin;

import android.content.Context;

import com.yc.pinyin.domain.GoodListInfo;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import yc.com.rthttplibrary.bean.ResultInfo;


/**
 * Created by zhangkai on 2017/3/1.
 */

public class GoodEngine extends BaseEngine {
    public GoodEngine(Context context) {
        super(context);
    }

//    @Override
//    public String getUrl() {
//        return Config.VIP_LIST_URL;
//    }

    public Observable<ResultInfo<GoodListInfo>> getGoodList() {
//        return rxpost(new TypeReference<ResultInfo<GoodListInfo>>(){}.getType(), null, true, true, true);
        return request.getGoodList(appId).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

}
