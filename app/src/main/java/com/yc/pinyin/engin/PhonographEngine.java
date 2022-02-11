package com.yc.pinyin.engin;

import android.content.Context;

import com.yc.pinyin.domain.PhonogramListInfo;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import yc.com.rthttplibrary.bean.ResultInfo;


/**
 * Created by zhangkai on 2017/12/19.
 */

public class PhonographEngine extends BaseEngine {
    public PhonographEngine(Context context) {
        super(context);
    }

//    @Override
//    public String getUrl() {
//        return Config.PHONOGRAM_LIST_URL;
//    }

    public Observable<ResultInfo<PhonogramListInfo>> getPhonogramList() {
//        return rxpost(new TypeReference<ResultInfo<PhonogramListInfo>>() {
//        }.getType(), null, true, true, true);

        return request.getPhonogramList(appId).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }
}
