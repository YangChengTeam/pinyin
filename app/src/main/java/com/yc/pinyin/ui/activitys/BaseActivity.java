package com.yc.pinyin.ui.activitys;

import android.content.Intent;
import android.os.Bundle;

import com.yc.pinyin.ui.IView;
import com.yc.pinyin.utils.UIUtils;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by zhangkai on 2017/10/31.
 */

public abstract class BaseActivity extends AppCompatActivity implements IView {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            // Activity was brought to front and not created,
            // Thus finishing this will get us to the last viewed activity
            finish();
            return;
        }
        UIUtils.invoke(getWindow().getDecorView().getRootView());
        setContentView(getLayoutId());
        init();
        loadData();
    }

    public void loadData() {
    }
}
