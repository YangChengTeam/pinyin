package com.yc.pinyin.pay;

import android.app.Activity;


import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import yc.com.rthttplibrary.util.ToastUtil;

/**
 * Created by zhangkai on 2017/4/19.
 */

public class IWXPay1Impl extends IPayImpl {

    private IWXAPI msgApi;

    public IWXPay1Impl(Activity context) {
        super(context);
        isGen=true;
        msgApi = WXAPIFactory.createWXAPI(context, null);
        msgApi.registerApp("wx6b6c6d943013289c");
    }

    @Override
    public void pay(final OrderInfo orderInfo, IPayCallback iPayCallback) {
        if (orderInfo == null || orderInfo.getPayInfo() == null) {
            ToastUtil.toast(mContext, "支付失败");
            return;
        }
        PayInfo payInfo = orderInfo.getPayInfo();
        if ("SUCCESS".equals(payInfo.getResult_code())) {
            uOrderInfo = orderInfo;
            uiPayCallback = iPayCallback;
            wxpay(payInfo);
        } else {
            ToastUtil.toast(mContext, "预下订单请求失败");
        }
    }

    private void wxpay(PayInfo payInfo) {
        PayReq request = new PayReq();
        request.appId = payInfo.getAppid();
        request.partnerId = payInfo.getMch_id();
        request.prepayId = payInfo.getPrepay_id();
        request.packageValue = "Sign=WXPay";
        request.nonceStr = payInfo.getNonce_str();
        request.timeStamp = payInfo.getTimestamp();
        request.sign = payInfo.getSign();
        IPayImpl.appid = payInfo.getAppid();
        msgApi.registerApp(payInfo.getAppid());
        msgApi.sendReq(request);
    }

}
