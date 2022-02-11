package com.yc.pinyin.ui.popupwindow;

import android.app.Activity;
import android.text.Html;
import android.text.TextUtils;
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
public class LoginPopupWindow extends BasePopupWindow {
    private PhoneLoginEngine loginEngine;
    private LoadingDialog loadingDialog;
    private EditText etPhone;
    private EditText etPwd;

    private ImageView ivLogin;
    private TextView tvCodeRegister;
    private TextView tvUserPolicy;
    private TextView tvRegister;

    public LoginPopupWindow(Activity context) {
        super(context);
        loginEngine = new PhoneLoginEngine(context);
        loadingDialog = new LoadingDialog(context);
    }

    @Override
    public int getLayoutId() {
        return R.layout.popwindow_login_view;
    }

    @Override
    public void init() {
        etPhone = (EditText) getView(R.id.et_phone);
        etPwd = (EditText) getView(R.id.et_pwd);

        ivLogin = (ImageView) getView(R.id.iv_login);
        tvCodeRegister = (TextView) getView(R.id.tv_code_register);
        tvRegister = (TextView) getView(R.id.tv_register);
        tvCodeRegister.setText(Html.fromHtml("忘记了？<font color='#b05739'> 验证码登录</font>"));
        tvUserPolicy = (TextView) getView(R.id.tv_user_policy);
        tvUserPolicy.setText(Html.fromHtml("登录即代表同意<font color='#b05739'>《用户协议》</font>"));
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
                String pwd = etPwd.getText().toString().trim();
                if (TextUtils.isEmpty(phone)) {
                    ToastUtil.toast(mContext, "手机号不能为空");
                    return;
                }
                if (!PhoneUtils.isPhone(phone)) {
                    ToastUtil.toast(mContext, "手机号格式不正确");
                    return;
                }

                if (TextUtils.isEmpty(pwd)) {
                    ToastUtil.toast(mContext, "密码不能为空");
                    return;
                }
                login(phone, pwd);
            }
        });
        RxView.clicks(tvCodeRegister).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                RegisterPopupWindow registerPopupWindow = new RegisterPopupWindow(mContext);
                registerPopupWindow.setLogin(true);
                registerPopupWindow.show();
                dismiss();
            }
        });
        RxView.clicks(tvRegister).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                RegisterPopupWindow registerPopupWindow = new RegisterPopupWindow(mContext);
                registerPopupWindow.show();
                dismiss();
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

    private void login(String phone, final String pwd) {
        loadingDialog.show("登录中...");
        loginEngine.login(phone, pwd).subscribe(new BaseCommonObserver<UserInfoWrapper>(mContext) {
            @Override
            public void onSuccess(UserInfoWrapper data, String message) {
                loadingDialog.dismiss();

                if (data != null) {
                    UserInfo userInfo = data.getUserInfo();
                    List<VipInfo> vipList = data.getVipList();
                    if (null != userInfo)
                        userInfo.setPwd(pwd);
                    UserInfoHelper.saveUser(userInfo);
                    UserInfoHelper.saveVipList(vipList);
                    dismiss();


                }
            }

            @Override
            public void onFailure(int code, String errorMsg) {
                loadingDialog.dismiss();
                if (code == 2 || errorMsg.contains("账号不存在")) {

                    RegisterPopupWindow registerPopupWindow = new RegisterPopupWindow(mContext);
                    registerPopupWindow.show();
                    dismiss();
                } else {
                    ToastUtil.toast(mContext, errorMsg);
                }
            }

            @Override
            public void onRequestComplete() {

            }
        });

    }


}
