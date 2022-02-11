package com.yc.pinyin.ui.popupwindow;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.jakewharton.rxbinding.view.RxView;
import com.yc.pinyin.R;
import com.yc.pinyin.domain.Config;
import com.yc.pinyin.domain.GoodInfo;
import com.yc.pinyin.domain.GoodListInfo;
import com.yc.pinyin.engin.GoodEngine;
import com.yc.pinyin.observer.BaseCommonObserver;
import com.yc.pinyin.pay.I1PayAbs;
import com.yc.pinyin.pay.IPayAbs;
import com.yc.pinyin.pay.IPayCallback;
import com.yc.pinyin.pay.OrderInfo;
import com.yc.pinyin.pay.OrderParamsInfo;
import com.yc.pinyin.ui.activitys.MainActivity;
import com.yc.pinyin.ui.adapter.PayWayInfoAdapter;

import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import rx.functions.Action1;
import yc.com.rthttplibrary.config.HttpConfig;
import yc.com.rthttplibrary.util.LogUtil;
import yc.com.rthttplibrary.util.PreferenceUtil;
import yc.com.rthttplibrary.util.ScreenUtil;
import yc.com.rthttplibrary.util.TaskUtil;
import yc.com.rthttplibrary.util.ToastUtil;

/**
 * Created by zhangkai on 2017/12/15.
 */

public class PayPopupWindow extends BasePopupWindow {

    private ImageView mIvWxPay;
    private ImageView mIvAliPay;
    private ImageButton mIvPayCharge;
    private PayWayInfoAdapter payWayInfoAdapter;
    private ImageView preImagView;
    private RecyclerView recyclerView;

    private IPayAbs iPayAbs;
    private final String WX_PAY = "wxpay";
    private final String ALI_PAY = "alipay";
    private String payway = WX_PAY;
    private GoodEngine goodEngin;

    private GoodInfo goodInfo;


    public PayPopupWindow(Activity context) {
        super(context);
    }

    @Override
    public int getLayoutId() {
        return R.layout.popwindow_pay_view;
    }

    @Override
    public void init() {
        goodEngin = new GoodEngine(mContext);
        setAnimationStyle(R.style.popwindow_style);
        initData();
        mIvPayCharge = (ImageButton) getView(R.id.iv_pay_charge);
        mIvWxPay = (ImageView) getView(R.id.iv_wx_pay);
        mIvAliPay = (ImageView) getView(R.id.iv_ali_pay);
        recyclerView = (RecyclerView) getView(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        payWayInfoAdapter = new PayWayInfoAdapter(null);
        recyclerView.setAdapter(payWayInfoAdapter);
        recyclerView.addItemDecoration(new MyItemDecoration());

        mIvWxPay.setImageResource(R.mipmap.pay_wx_press);

        initListener();

        iPayAbs = new I1PayAbs(mContext);


    }

    private void initData() {

        TaskUtil.getImpl().runTask(new Runnable() {
            @Override
            public void run() {
                String str = PreferenceUtil.getImpl(mContext).getString(Config.VIP_LIST_URL, "");
                if (!TextUtils.isEmpty(str)) {
                    try {
                        final GoodListInfo goodListInfo = JSON.parseObject(str, GoodListInfo.class);
                        if (goodListInfo != null) {
                            mContext.runOnUiThread(new Runnable() {

                                @Override

                                public void run() {
                                    if (goodListInfo.getGoodInfoList() != null) {
                                        payWayInfoAdapter.setNewData(goodListInfo.getGoodInfoList());
                                        goodInfo = getGoodInfo(goodListInfo.getGoodInfoList());
                                    }

                                }
                            });
                        }
                    } catch (Exception e) {
                        LogUtil.msg("vip info read failed " + e.getMessage());
                    }

                }
            }
        });
        goodEngin.getGoodList().subscribe(new BaseCommonObserver<GoodListInfo>(mContext) {
            @Override
            public void onSuccess(final GoodListInfo data, String message) {
                if (data != null && data.getGoodInfoList() != null) {
                    payWayInfoAdapter.setNewData(data.getGoodInfoList());
                    goodInfo = getGoodInfo(data.getGoodInfoList());
                }
                TaskUtil.getImpl().runTask(new Runnable() {
                    @Override
                    public void run() {
                        if (data != null)
                            PreferenceUtil.getImpl(mContext).putString(Config.VIP_LIST_URL, JSON.toJSONString(data));
                    }
                });
            }

            @Override
            public void onFailure(int code, String errorMsg) {

            }

            @Override
            public void onRequestComplete() {

            }
        });
//        goodEngin.getGoodList().subscribe(new Action1<ResultInfo<GoodListInfo>>() {
//            @Override
//            public void call(final ResultInfo<GoodListInfo> goodListInfoResultInfo) {
//
//            }
//        });
    }


    private void initListener() {


        RxView.clicks(getView(R.id.ll_ali_pay)).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                resetPayWay();
                mIvAliPay.setImageResource(R.mipmap.pay_ali_press);
                payway = ALI_PAY;
            }
        });
        RxView.clicks(getView(R.id.ll_wx_pay)).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                resetPayWay();
                mIvWxPay.setImageResource(R.mipmap.pay_wx_press);
                payway = WX_PAY;
            }
        });
        RxView.clicks(getView(R.id.iv_pay_close)).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                dismiss();
            }
        });
        RxView.clicks(mIvPayCharge).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                if (MainActivity.getMainActivity().isSuperVip()) {
                    createRewardDialog();
                    return;
                }
                if (goodInfo != null) {
                    OrderParamsInfo orderParamsInfo = new OrderParamsInfo(String.valueOf(goodInfo.getId()), "0", Float.parseFloat(goodInfo.getReal_price()), goodInfo.getTitle());
                    orderParamsInfo.setPayway_name(payway);

                    iPayAbs.pay(orderParamsInfo, new IPayCallback() {
                        @Override
                        public void onSuccess(OrderInfo orderInfo) {
                            MainActivity.getMainActivity().saveVip(goodInfo.getId() + "");
                            dismiss();
                        }

                        @Override
                        public void onFailure(OrderInfo orderInfo) {

                        }
                    });
                } else {
                    ToastUtil.toast(mContext, HttpConfig.NET_ERROR);
                }
            }
        });

        payWayInfoAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ImageView mIvSelect = payWayInfoAdapter.getIv(position);
                boolean isBuy = (boolean) mIvSelect.getTag();
                if (isBuy) {
                    return;
                }

                if (preImagView == null)
                    preImagView = payWayInfoAdapter.getIv(0);

                if (preImagView != mIvSelect && !((boolean) preImagView.getTag())) {
                    preImagView.setImageResource(R.mipmap.pay_select_normal);
                }
                mIvSelect.setImageResource(R.mipmap.pay_select_press);
                preImagView = mIvSelect;
                goodInfo = payWayInfoAdapter.getItem(position);
            }
        });

    }

    private void createRewardDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("提示");
        builder.setMessage("你已经购买了所有项目");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });
        builder.show();
    }


    private void resetPayWay() {
        mIvAliPay.setImageResource(R.mipmap.pay_ali_normal);
        mIvWxPay.setImageResource(R.mipmap.pay_wx_normal);
    }

    private class MyItemDecoration extends RecyclerView.ItemDecoration {
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.set(0, 0, 0, ScreenUtil.dip2px(mContext, 9));
        }
    }

    private GoodInfo getGoodInfo(List<GoodInfo> goodInfoList) {
        GoodInfo goodInfo = null;
        if (goodInfoList != null && goodInfoList.size() > 0) {
            goodInfo = goodInfoList.get(0);
            if (MainActivity.getMainActivity().isSuperVip()) {
                goodInfo = null;
            }

//            if ((MainActivity.getMainActivity().isPhonicsVip() && MainActivity.getMainActivity().isPhonogramVip()) || MainActivity.getMainActivity().isPhonogramOrPhonicsVip()) {
//                goodInfo = goodInfoList.get(0);
//            }
//            if (MainActivity.getMainActivity().isPhonogramVip()) {
//                goodInfo = goodInfoList.get(1);
//            }
//            if (MainActivity.getMainActivity().isPhonicsVip()) {
//                goodInfo = goodInfoList.get(0);
//            }


        }
        return goodInfo;
    }
}