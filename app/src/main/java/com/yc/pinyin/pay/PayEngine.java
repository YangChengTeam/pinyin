package com.yc.pinyin.pay;


import android.content.Context;

import com.yc.pinyin.engin.BaseEngine;
import com.yc.pinyin.helper.UserInfoHelper;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import yc.com.rthttplibrary.bean.ResultInfo;


/**
 * Created by zhangkai on 2017/2/20.
 */

public class PayEngine extends BaseEngine {


    public PayEngine(Context context) {
        super(context);
    }

    public Observable<ResultInfo<OrderInfo>> pay(OrderParamsInfo orderParamsInfo) {


        Map<String, Object> params = new HashMap<>();
        params.put("goods_id", orderParamsInfo.getGoods_id());
        params.put("goods_num", 1);
        params.put("is_payway_split", "1");
        params.put("payway_name", orderParamsInfo.getPayway_name());
        params.put("money", orderParamsInfo.getPrice());
        params.put("ds_money", orderParamsInfo.getDsMoney());
        params.put("type", orderParamsInfo.getType());
        params.put("user_id", UserInfoHelper.getUid());


        return request.pay(params, appId).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }


    public Observable<ResultInfo<OrderInfo>> pay1(OrderParamsInfo orderParamsInfo) {
//        return HttpCoreEngin.get(context).rxpost(orderParamsInfo.getPay_url(), new TypeReference<ResultInfo<OrderInfo>>(){}
//                        .getType(),
//                orderParamsInfo.getParams(),
//                true,
//                true, true);
        return request.pay1(orderParamsInfo.getGoods_id(), 1, "1", orderParamsInfo.getPayway_name(),
                orderParamsInfo.getPrice(), orderParamsInfo.getDsMoney(), orderParamsInfo.getType(), UserInfoHelper.getUid(), appId)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }
}
