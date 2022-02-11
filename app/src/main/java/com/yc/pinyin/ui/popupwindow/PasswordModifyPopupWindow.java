package com.yc.pinyin.ui.popupwindow;

import android.app.Activity;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageView;

import com.jakewharton.rxbinding.view.RxView;
import com.kk.loading.LoadingDialog;
import com.yc.pinyin.R;
import com.yc.pinyin.domain.UserInfo;
import com.yc.pinyin.engin.PhoneLoginEngine;
import com.yc.pinyin.helper.UserInfoHelper;
import com.yc.pinyin.observer.BaseCommonObserver;

import java.util.concurrent.TimeUnit;

import rx.functions.Action1;
import yc.com.rthttplibrary.util.ToastUtil;

/**
 * Created by suns  on 2020/4/4 10:48.
 */
public class PasswordModifyPopupWindow extends BasePopupWindow {
    private PhoneLoginEngine loginEngine;
    private EditText etPwd, etNewPwd;
    private ImageView ivModify;
    private LoadingDialog loadingDialog;

    public PasswordModifyPopupWindow(Activity context) {
        super(context);
        loginEngine = new PhoneLoginEngine(context);
        loadingDialog = new LoadingDialog(context);
    }

    @Override
    public int getLayoutId() {
        return R.layout.popwindow_modify_psd_view;
    }

    @Override
    public void init() {
        etPwd = (EditText) getView(R.id.et_pwd);
        etNewPwd = (EditText) getView(R.id.et_newpwd);
        ivModify = (ImageView) getView(R.id.iv_modify);

        initListener();
    }

    private void initListener() {
        RxView.clicks(getView(R.id.iv_pay_close)).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                dismiss();
            }
        });
        RxView.clicks(ivModify).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                String pwd = etPwd.getText().toString().trim();
                String newPwd = etNewPwd.getText().toString().trim();
                if (TextUtils.isEmpty(pwd)) {
                    ToastUtil.toast(mContext, "密码不能为空");
                    return;
                }
                if (TextUtils.isEmpty(newPwd)) {
                    ToastUtil.toast(mContext, "新密码不能为空");
                    return;
                }

                modifyPwd(pwd, newPwd);
            }
        });
    }

    private void modifyPwd(String pwd, final String newPwd) {
        loadingDialog.show("修改密码中...");
        loginEngine.modifyPwd(pwd,newPwd).subscribe(new BaseCommonObserver<UserInfo>(mContext) {
            @Override
            public void onSuccess(UserInfo data, String message) {
                loadingDialog.dismiss();
                    if (data != null) {

                        UserInfo userInfo = UserInfoHelper.getUser();
                        if (null != userInfo) {
                            userInfo.setPwd(newPwd);
                        }
                        UserInfoHelper.saveUser(userInfo);
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

//        loginEngine.modifyPwd(pwd, newPwd).subscribe(new Subscriber<ResultInfo<UserInfo>>() {
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
//            public void onNext(ResultInfo<UserInfo> userInfoWrapperResultInfo) {
//
//            }
//        });
    }
}
