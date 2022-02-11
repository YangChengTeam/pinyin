package com.yc.pinyin.pay;

import android.app.Activity;

import com.kk.loading.LoadingDialog;

import com.yc.pinyin.observer.BaseCommonObserver;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import yc.com.rthttplibrary.config.GoagalInfo;
import yc.com.rthttplibrary.util.LogUtil;
import yc.com.rthttplibrary.util.ToastUtil;


/**
 * Created by zhangkai on 2017/3/17.
 */

public class I1PayAbs extends IPayAbs {
    private PayEngine payEngin;
    private LoadingDialog loadingDialog;
    private IPayImpl iPayImpl;

    public I1PayAbs(Activity context) {
        super(context);
        payEngin = new PayEngine(context);
        loadingDialog = new LoadingDialog(mContext);
    }

    @Override
    public void pay(final OrderParamsInfo orderParamsInfo, final IPayCallback callback) {
        iPayImpl = PayImplFactory.createPayImpl(mContext, orderParamsInfo.getPayway_name());

        if (iPayImpl == null) {
            ToastUtil.toast(mContext, "通道已关闭");
            return;
        }

        loadingDialog.show("请稍后...");
        String md5signstr = iPayImpl.befor(debug(orderParamsInfo.getPrice()) + "", orderParamsInfo.getName()) + "";
        orderParamsInfo.setMd5signstr(md5signstr);
        payEngin.pay1(orderParamsInfo).subscribe(new BaseCommonObserver<OrderInfo>(mContext) {
            @Override
            public void onSuccess(OrderInfo data, String message) {
                loadingDialog.dismiss();
                OrderInfo orderInfo = null;
                String order_sn = System.currentTimeMillis() + "_" + GoagalInfo.get().uuid;

                if (data != null && data.getOrder_sn() != null) {
                    orderInfo = data;
                    if (orderInfo.getPayInfo() != null && orderInfo.getPayInfo().getIsH5Pay() == 1) {
                        iPayImpl = new IDunXingH5PayImpl(mContext, "0");
                    }
                }

                if (orderInfo == null) {
                    orderInfo = new OrderInfo(Integer.parseInt(orderParamsInfo.getGoods_id()), orderParamsInfo.getPrice(),
                            orderParamsInfo.getName(), order_sn, orderParamsInfo.getPayway_name(), null);
                } else {
                    orderInfo.setPayway(orderParamsInfo.getPayway_name());
                    orderInfo.setViptype(Integer.parseInt(orderParamsInfo.getGoods_id()));
                    orderInfo.setName(orderParamsInfo.getName());
                }

//                if (resultInfo != null) {
                orderInfo.setMessage(message + "");
//                }

                orderInfo.setMoney(debug(orderInfo.getMoney()));
                iPayImpl.after();
                iPayImpl.pay(orderInfo, callback);
            }

            @Override
            public void onFailure(int code, String errorMsg) {
                loadingDialog.dismiss();
                LogUtil.msg(code + "---" + errorMsg);
            }

            @Override
            public void onRequestComplete() {

            }
        });

//        payEngin.pay(mContext, orderParamsInfo).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<ResultInfo<OrderInfo>>() {
//            @Override
//            public void call(ResultInfo<OrderInfo> resultInfo) {
//                loadingDialog.dismiss();
//
//                OrderInfo orderInfo = null;
//                String order_sn = System.currentTimeMillis() + "_" + GoagalInfo.get().uuid;
//                if (resultInfo != null && resultInfo.code == HttpConfig.STATUS_OK) {
//                    if (resultInfo.data != null && resultInfo.data.getOrder_sn() != null) {
//                        orderInfo = resultInfo.data;
//                        if (orderInfo.getPayInfo() != null && orderInfo.getPayInfo().getIsH5Pay() == 1) {
//                            iPayImpl = new IDunXingH5PayImpl(mContext, "0");
//                        }
//                    }
//                }
//                if (orderInfo == null) {
//                    orderInfo = new OrderInfo(Integer.parseInt(orderParamsInfo.getGoods_id()), orderParamsInfo.getPrice(),
//                            orderParamsInfo.getName(), order_sn, orderParamsInfo.getPayway_name(), null);
//                } else {
//                    orderInfo.setPayway(orderParamsInfo.getPayway_name());
//                    orderInfo.setViptype(Integer.parseInt(orderParamsInfo.getGoods_id()));
//                    orderInfo.setName(orderParamsInfo.getName());
//                }
//
//                if (resultInfo != null) {
//                    orderInfo.setMessage(resultInfo.message + "");
//                }
//
//                orderInfo.setMoney(debug(orderInfo.getMoney()));
//                iPayImpl.after();
//                iPayImpl.pay(orderInfo, callback);
//            }
//        });
    }
}
