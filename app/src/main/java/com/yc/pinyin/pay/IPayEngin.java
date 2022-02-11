package com.yc.pinyin.pay;

import android.content.Context;

import io.reactivex.Observable;
import yc.com.rthttplibrary.bean.ResultInfo;


/**
 * Created by zhangkai on 2017/4/14.
 */

public interface IPayEngin {
    Observable<ResultInfo<OrderInfo>> pay(Context context, OrderParamsInfo orderParamsInfo);
}
