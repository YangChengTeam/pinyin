package com.yc.pinyin.ui.popupwindow;

import android.app.Activity;

import com.jakewharton.rxbinding.view.RxView;
import com.yc.pinyin.R;

import java.util.concurrent.TimeUnit;

import rx.functions.Action1;

/**
 * Created by suns  on 2020/4/4 10:48.
 */
public class IndexNoticeWindow extends BasePopupWindow {


    //19947659430
    public IndexNoticeWindow(Activity context) {
        super(context);


    }

    @Override
    public int getLayoutId() {
        return R.layout.popwindow_index_notice;
    }


    @Override
    public void init() {

        initListener();
    }

    private void initListener() {
        RxView.clicks(getView(R.id.iv_know)).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                dismiss();
            }
        });

    }


}
