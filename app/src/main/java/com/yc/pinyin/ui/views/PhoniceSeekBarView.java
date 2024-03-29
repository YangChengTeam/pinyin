package com.yc.pinyin.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;
import com.yc.pinyin.R;
import com.yc.pinyin.helper.SeekBarHelper;

import java.util.concurrent.TimeUnit;

import rx.functions.Action1;

/**
 * Created by zhangkai on 2017/12/18.
 */

public class PhoniceSeekBarView extends BaseView {

    private ImageView mLeftImageView;
    private ImageView mRightImageView;
    private TextView mIndexTextView;

    public PhoniceSeekBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PhoniceSeekBarView(Context context) {
        super(context);
    }

    @Override
    public int getLayoutId() {
        return R.layout.view_phonice_seekbar;
    }

    private SeekBarHelper seekBarHelper;

    @Override
    public void init() {
        mLeftImageView = (ImageView) getView(R.id.iv_left);
        mRightImageView = (ImageView) getView(R.id.iv_right);
        mIndexTextView = (TextView) getView(R.id.tv_index);

        seekBarHelper = new SeekBarHelper();
        seekBarHelper.init(mLeftImageView, mRightImageView, mIndexTextView, null);
    }

    public void showIndex(int count) {
        seekBarHelper.showIndex(count);
    }

    public void setIndex(int index) {
        seekBarHelper.setIndex(index);
    }

    public int getIndex() {
        return seekBarHelper.getIndex();
    }

    public void setIndexListener(SeekBarHelper.IndexListener indexListener) {
        seekBarHelper.setIndexListener(indexListener);
    }
}