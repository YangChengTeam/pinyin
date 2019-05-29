package com.yc.pinyin.ui.activitys;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qq.e.ads.nativ.NativeExpressADView;
import com.yc.pinyin.R;
import com.yc.pinyin.domain.Config;
import com.yc.pinyin.utils.Mp3Utils;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import yc.com.tencent_adv.AdvDispatchManager;
import yc.com.tencent_adv.AdvType;
import yc.com.tencent_adv.OnAdvStateListener;

/**
 * Created by zhangkai on 2017/12/15.
 */

public class SplashActivity extends BaseActivity implements OnAdvStateListener {
    private Subscription subscription = null;
    private MediaPlayer mediaPlayer;
    public static SplashActivity INSTANCE;
    Handler handler = new Handler();
    private TextView skipView;
    private FrameLayout splashContainer;
    private RelativeLayout reTryLayout;
    private long minSplashTimeWhenNoAD = 2000;

    @Override
    public int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    public void init() {
        INSTANCE = this;


        final ImageView logoImageView = findViewById(R.id.iv_logo);
        skipView = findViewById(R.id.skip_view);
        splashContainer = findViewById(R.id.splash_container);
        reTryLayout = findViewById(R.id.retry_layout);

        AdvDispatchManager.getManager().init(this, AdvType.SPLASH, splashContainer, skipView, Config.AV_APPID, Config.AV_SPLASH_ID,this);
        final Integer[] bgIDs = new Integer[]{R.mipmap.splash_bg1, R.mipmap.splash_bg2, R.mipmap.splash_bg3, R.mipmap
                .splash_bg4};
        subscription = Observable.interval(300, TimeUnit.MILLISECONDS).observeOn
                (AndroidSchedulers
                        .mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        logoImageView.setImageDrawable(ContextCompat.getDrawable(SplashActivity.this, bgIDs[aLong.intValue() % 4]));
                    }
                });
        mediaPlayer = Mp3Utils.playMp3(this, "splash.mp3", new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

                if (subscription != null && !subscription.isUnsubscribed()) {
                    subscription.unsubscribe();
                    subscription = null;
                }
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
//                    }
//                }, minSplashTimeWhenNoAD);

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        AdvDispatchManager.getManager().onPause();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        AdvDispatchManager.getManager().onResume();
//        if (mediaPlayer != null) {
//            mediaPlayer.start();
//        }
    }

    @Override
    public void finish() {
        super.finish();
        INSTANCE = null;
    }

    public static SplashActivity getInstance() {
        return INSTANCE;
    }


    //防止用户返回键退出 APP
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                || keyCode == KeyEvent.KEYCODE_HOME) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onShow() {
        reTryLayout.setVisibility(View.GONE);
        skipView.setVisibility(View.VISIBLE);
    }

    private void switchMain(long delay) {
        long delayTime = 0;
        if (delay < minSplashTimeWhenNoAD) {
            delayTime = minSplashTimeWhenNoAD - delay;
        }

        // 计算出还需要延时多久
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                gotoMain();
            }
        }, delayTime);

    }

    private void gotoMain() {
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        finish();
    }

    @Override
    public void onDismiss(long delayTime) {
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
        switchMain(delayTime);
    }

    @Override
    public void onError() {

    }

    @Override
    public void onNativeExpressDismiss(NativeExpressADView view) {

    }

    @Override
    public void onNativeExpressShow(Map<NativeExpressADView, Integer> mDatas) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        AdvDispatchManager.getManager().onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
