package com.yc.pinyin.ui.popupwindow;

import android.app.Activity;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;
import com.yc.pinyin.App;
import com.yc.pinyin.R;

import java.util.concurrent.TimeUnit;

import rx.functions.Action1;

/**
 * Created by suns  on 2020/4/4 10:48.
 */
public class PrivatePolicyWindow extends BasePopupWindow {


    public PrivatePolicyWindow(Activity context) {
        super(context);
//        this.phone = phone;
//        this.code = code;

    }

    @Override
    public int getLayoutId() {
        return R.layout.popwindow_private_policy_view;
    }

    @Override
    public void init() {
        ((TextView) getView(R.id.webView)).setText(App.privacyPolicy);

//        String str = PreferenceUtil.getImpl(mContext).getString(Config.ADV_INFO, "");
//        final AdvInfo advInfo = JSON.parseObject(str, AdvInfo.class);
//
//        if (null != advInfo) {
//            url = advInfo.getUrl();
//
//        }
//
//        wvMain = (CommonWebView) getView(R.id.webView);
//        initWebview();
        initListener();
    }


    private void initListener() {
        RxView.clicks(getView(R.id.iv_pay_close)).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                dismiss();
            }
        });
    }


}
