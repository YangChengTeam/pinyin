package com.yc.pinyin.engin;

import android.content.Context;

import com.yc.pinyin.domain.MyOrderInfoWrapper;
import com.yc.pinyin.helper.UserInfoHelper;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import yc.com.rthttplibrary.bean.ResultInfo;


/**
 * Created by suns  on 2020/4/8 11:16.
 */
public class OrderListEngine extends BaseEngine {
    public OrderListEngine(Context context) {
        super(context);
    }

    public Observable<ResultInfo<MyOrderInfoWrapper>> getOrderList(int page, int page_size) {

        return request.getOrderList(UserInfoHelper.getUid(), page, page_size, appId)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }
}
