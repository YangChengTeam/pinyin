package com.yc.pinyin.ui.popupwindow;

import android.app.Activity;
import android.os.Handler;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;
import com.kk.loading.LoadingDialog;

import com.yc.pinyin.R;
import com.yc.pinyin.domain.Config;
import com.yc.pinyin.domain.UserInfo;
import com.yc.pinyin.domain.UserInfoWrapper;
import com.yc.pinyin.domain.VipInfo;
import com.yc.pinyin.engin.PhoneLoginEngine;
import com.yc.pinyin.helper.SharePreferenceUtils;
import com.yc.pinyin.helper.UserInfoHelper;
import com.yc.pinyin.observer.BaseCommonObserver;
import com.yc.pinyin.utils.PhoneUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Subscriber;
import rx.functions.Action1;
import yc.com.rthttplibrary.util.ToastUtil;

/**
 * Created by suns  on 2020/4/4 10:48.
 */
public class RegisterPopupWindow extends BasePopupWindow {
    private PhoneLoginEngine loginEngine;
    private EditText etPhone;
    private EditText etCode;
    private ImageView ivGetCode;
    private ImageView ivLogin;
    private Handler mHandler;
    private TextView tvCode;
    private MyRunnable taskRunnable;
    private boolean isLogin = false;
    private LoadingDialog loadingDialog;
    private TextView tvUserPolicy;

    public RegisterPopupWindow(Activity context) {
        super(context);
        loginEngine = new PhoneLoginEngine(context);
//        Looper.prepare();
        mHandler = new Handler();
//        Looper.loop();
        loadingDialog = new LoadingDialog(context);

    }

    @Override
    public int getLayoutId() {
        return R.layout.popwindow_register_view;
    }

    public void setLogin(boolean login) {
        this.isLogin = login;
    }

    @Override
    public void init() {
        etPhone = (EditText) getView(R.id.et_phone);
        etCode = (EditText) getView(R.id.et_code);
        ivGetCode = (ImageView) getView(R.id.iv_get_code);
        ivLogin = (ImageView) getView(R.id.iv_login);
        tvCode = (TextView) getView(R.id.tv_code);
        tvUserPolicy = (TextView) getView(R.id.tv_user_policy);
        tvUserPolicy.setText(Html.fromHtml("注册即代表同意<font color='#b05739'>《用户协议》</font>"));
        String phone = SharePreferenceUtils.getInstance().getString(Config.USER_PHONE, "");
        if (!TextUtils.isEmpty(phone)) {
            etPhone.setText(phone);
        }
        initListener();
    }

    private void initListener() {
        RxView.clicks(getView(R.id.iv_pay_close)).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                dismiss();
            }
        });
        RxView.clicks(ivLogin).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                String phone = etPhone.getText().toString().trim();
                String code = etCode.getText().toString().trim();
                if (TextUtils.isEmpty(phone)) {
                    ToastUtil.toast(mContext, "手机号不能为空");
                    return;
                }
                if (!PhoneUtils.isPhone(phone)) {
                    ToastUtil.toast(mContext, "手机号格式不正确");
                    return;
                }

                if (TextUtils.isEmpty(code)) {
                    ToastUtil.toast(mContext, "验证码不能为空");
                    return;
                }


                codeLogin(phone, code);
//                if (isLogin) {
//
//                    return;
//                }
//
//
//                dismiss();

            }
        });
        RxView.clicks(ivGetCode).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                String phone = etPhone.getText().toString().trim();

                if (TextUtils.isEmpty(phone)) {
                    ToastUtil.toast(mContext, "手机号不能为空");
                    return;
                }
                if (!PhoneUtils.isPhone(phone)) {
                    ToastUtil.toast(mContext, "手机号格式不正确");
                    return;
                }
                sendCode(phone);
            }
        });
        RxView.clicks(tvUserPolicy).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                UserPolicyWindow userPolicyWindow = new UserPolicyWindow(mContext);
                userPolicyWindow.show();
            }
        });
    }

    private void codeLogin(String phone, String code) {
        loadingDialog.show("登录中...");

        loginEngine.codeLogin(phone, code).subscribe(new BaseCommonObserver<UserInfoWrapper>(mContext) {
            @Override
            public void onSuccess(UserInfoWrapper data, String message) {
                loadingDialog.dismiss();

                if (data != null) {
                    UserInfo userInfo = data.getUserInfo();
                    List<VipInfo> vipList = data.getVipList();
                    if (null != userInfo) {
                        userInfo.setPwd("");
                        SharePreferenceUtils.getInstance().putString(Config.USER_PHONE, userInfo.getMobile());
                    }
                    UserInfoHelper.saveUser(userInfo);

                    if (!isLogin) {
                        PasswordSetPopupWindow passwordSetPopupWindow = new PasswordSetPopupWindow(mContext);
                        passwordSetPopupWindow.show();
                    } else {
                        UserInfoHelper.saveVipList(vipList);
                    }
                    dismiss();
                } else {
                    ToastUtil.toast(mContext, message);
                }

            }

            @Override
            public void onFailure(int code, String errorMsg) {
                loadingDialog.dismiss();
                ToastUtil.toast(mContext, errorMsg);
            }

            @Override
            public void onRequestComplete() {

            }
        });
//        loginEngine.codeLogin(phone, code).subscribe(new Subscriber<ResultInfo<UserInfoWrapper>>() {
//            @Override
//            public void onCompleted() {
//                loadingDialog.dismiss();
//            }
//
//            @Override
//            public void onError(Throwable e) {
//
//            }
//
//            @Override
//            public void onNext(ResultInfo<UserInfoWrapper> userInfoWrapperResultInfo) {
//                if (userInfoWrapperResultInfo != null) {
//                    if (userInfoWrapperResultInfo.code == HttpConfig.STATUS_OK && userInfoWrapperResultInfo.data != null) {
//                        UserInfo userInfo = userInfoWrapperResultInfo.data.getUserInfo();
//                        List<VipInfo> vipList = userInfoWrapperResultInfo.data.getVipList();
//                        if (null != userInfo) {
//                            userInfo.setPwd("");
//                            SharePreferenceUtils.getInstance().putString(Config.USER_PHONE, userInfo.getMobile());
//                        }
//                        UserInfoHelper.saveUser(userInfo);
//
//                        if (!isLogin) {
//                            PasswordSetPopupWindow passwordSetPopupWindow = new PasswordSetPopupWindow(mContext);
//                            passwordSetPopupWindow.show();
//                        } else {
//                            UserInfoHelper.saveVipList(vipList);
//                        }
//                        dismiss();
//                    } else {
//                        ToastUtil.toast(mContext, userInfoWrapperResultInfo.message);
//                    }
//                }
//            }
//        });
    }

    private void sendCode(String phone) {

     loginEngine.sendCode(phone).subscribe(new BaseCommonObserver<String>(mContext) {
            @Override
            public void onSuccess(String data, String message) {

                showGetCodeDisplay(tvCode);
                ToastUtil.toast(mContext, message);

            }

            @Override
            public void onFailure(int code, String errorMsg) {
//                Log.e("TAG", "onFailure: "+errorMsg );
                ToastUtil.toast(mContext, errorMsg);
            }

            @Override
            public void onRequestComplete() {

            }
        });

//        loginEngine.sendCode(phone).subscribe(new Subscriber<ResultInfo<String>>() {
//            @Override
//            public void onCompleted() {
//
//            }
//
//            @Override
//            public void onError(Throwable e) {
//
//            }
//
//            @Override
//            public void onNext(ResultInfo<String> stringResultInfo) {
//                if (stringResultInfo != null) {
//                    if (stringResultInfo.code == HttpConfig.STATUS_OK) {
//                        showGetCodeDisplay(tvCode);
//                        ToastUtil.toast(mContext, stringResultInfo.message);
//                    } else {
//                        ToastUtil.toast(mContext, stringResultInfo.message);
//                    }
//                }
//
//
//            }
//        });
    }


    /**
     * 改变获取验证码按钮状态
     */
    private void showGetCodeDisplay(TextView textView) {
        taskRunnable = new MyRunnable(textView);
        if (null != mHandler) {
            ivGetCode.setVisibility(View.GONE);
            textView.setVisibility(View.VISIBLE);
            mHandler.removeCallbacks(taskRunnable);
            mHandler.removeMessages(0);
            totalTime = 60;
            textView.setClickable(false);
//            textView.setTextColor(ContextCompat.getColor(R.color.coment_color));
//            textView.setBackgroundResource(R.drawable.bg_btn_get_code);
            if (null != mHandler) mHandler.postDelayed(taskRunnable, 0);
        }
    }

    /**
     * 定时任务，模拟倒计时广告
     */
    private int totalTime = 60;


    private class MyRunnable implements Runnable {
        TextView mTv;

        public MyRunnable(TextView textView) {
            this.mTv = textView;
        }

        @Override
        public void run() {
            mTv.setText(totalTime + " s");
            totalTime--;
            if (totalTime < 0) {
                //还原
                initGetCodeBtn();
                return;
            }
            if (null != mHandler) mHandler.postDelayed(this, 1000);
        }
    }


    /**
     * 还原获取验证码按钮状态
     */
    private void initGetCodeBtn() {
        totalTime = 0;
        if (null != taskRunnable && null != mHandler) {
            mHandler.removeCallbacks(taskRunnable);
            mHandler.removeMessages(0);
        }
        ivGetCode.setVisibility(View.VISIBLE);
        tvCode.setVisibility(View.GONE);

    }

}
