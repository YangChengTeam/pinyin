package com.yc.pinyin.pay;

import android.app.Activity;
import android.content.DialogInterface;
import android.view.KeyEvent;

import com.alibaba.fastjson.TypeReference;
import com.kk.loading.LoadingDialog;
import com.yc.pinyin.httpinterface.HttpRequestInterface;
import com.yc.pinyin.observer.BaseCommonObserver;


import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

import rx.functions.Action1;
import yc.com.rthttplibrary.request.RetrofitHttpRequest;

/**
 * Created by zhangkai on 2017/3/17.
 */

public abstract class IPayImpl {

    public static OrderInfo uOrderInfo;
    public static IPayCallback uiPayCallback;
    public static String appid;
    protected static Activity mContext;
    protected static boolean isGen = false;
    protected static LoadingDialog loadingDialog;


    public static boolean isGen() {
        return isGen;
    }

    public static void setGen(boolean gen) {
        isGen = gen;
    }

    public IPayImpl(Activity context) {
        this.mContext = context;
    }

    public abstract void pay(OrderInfo orderInfo, IPayCallback iPayCallback);

    public Object befor(Object... obj) {
        return null;
    }

    public Object after(Object... obj) {
        return null;
    }

    protected String get(String cStr, String dStr) {
        return cStr == null || cStr.isEmpty() ? dStr : cStr;
    }

    protected static int n = 0;
    private static int times = 5;

    public static void checkOrder(final OrderInfo orderInfo, final IPayCallback iPayCallback, final String url) {
        if (!isGen) return;
        if (n == 0) {
            if (loadingDialog == null) {
                loadingDialog = new LoadingDialog(mContext);
            }
            loadingDialog.setCancelable(false);
            loadingDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {

                    }
                    return false;
                }
            });
            loadingDialog.show("正在查询...");
        }
        Map<String, String> params = new HashMap<>();
        params.put("order_sn", orderInfo.getOrder_sn());

        Retrofit retrofit = RetrofitHttpRequest.get(mContext);
        retrofit.create(HttpRequestInterface.class)
                .checkOrder(url, params)
                .delay(2, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseCommonObserver<String>(mContext) {
                    @Override
                    public void onSuccess(String data, String message) {

                        loadingDialog.dismiss();
                        orderInfo.setMessage("支付成功");
                        iPayCallback.onSuccess(orderInfo);
                        IPayImpl.uiPayCallback = null;
                        IPayImpl.uOrderInfo = null;
                        n = 0;
                        isGen = false;


                    }

                    @Override
                    public void onFailure(int code, String errorMsg) {
                        n++;
                        if (n < times) {
                            checkOrder(orderInfo, iPayCallback, url);
                        } else {
                            loadingDialog.dismiss();
                            orderInfo.setMessage("支付失败");
                            iPayCallback.onFailure(orderInfo);
                            IPayImpl.uiPayCallback = null;
                            IPayImpl.uOrderInfo = null;
                            n = 0;
                            isGen = false;
                        }
                    }

                    @Override
                    public void onRequestComplete() {

                    }
                });

//        HttpCoreEngin.get(mContext).rxpost(url, new TypeReference<ResultInfo<String>>() {
//                }.getType(), params, true,
//                true, true).delay(2, TimeUnit.SECONDS).observeOn
//                (AndroidSchedulers.mainThread()).subscribe(new Action1<ResultInfo<String>>() {
//            @Override
//            public void call(ResultInfo<String> resultInfo) {
//                if (resultInfo != null && resultInfo.code == HttpConfig.STATUS_OK) {
//                    loadingDialog.dismiss();
//                    orderInfo.setMessage("支付成功");
//                    iPayCallback.onSuccess(orderInfo);
//                    IPayImpl.uiPayCallback = null;
//                    IPayImpl.uOrderInfo = null;
//                    n = 0;
//                    isGen = false;
//                    return;
//                }
//                n++;
//                if (n < times) {
//                    checkOrder(orderInfo, iPayCallback, url);
//                } else {
//                    loadingDialog.dismiss();
//                    orderInfo.setMessage("支付失败");
//                    iPayCallback.onFailure(orderInfo);
//                    IPayImpl.uiPayCallback = null;
//                    IPayImpl.uOrderInfo = null;
//                    n = 0;
//                    isGen = false;
//                }
//            }
//        });
    }
}
