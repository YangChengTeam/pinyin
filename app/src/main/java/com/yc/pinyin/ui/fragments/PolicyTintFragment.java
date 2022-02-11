package com.yc.pinyin.ui.fragments;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;


import com.jakewharton.rxbinding.view.RxView;
import com.umeng.commonsdk.UMConfigure;
import com.yc.pinyin.R;
import com.yc.pinyin.domain.Config;
import com.yc.pinyin.helper.SharePreferenceUtils;
import com.yc.pinyin.utils.SpanUtils;

import yc.com.rthttplibrary.util.ScreenUtil;


/**
 * Created by suns  on 2021/1/28 14:02.
 */
public class PolicyTintFragment extends DialogFragment {


    private TextView tvContent;
    private TextView tvAgreeBtn;
    private TextView tvExitBtn;

    private View rootView;

    @Nullable
    @Override
    public View onCreateView(@Nullable LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Window window = getDialog().getWindow();


        if (rootView == null) {
            getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
            rootView = inflater.inflate(R.layout.fragment_policy_tint, container, false);
//            window.setLayout((int) (RxDeviceTool.getScreenWidth(getActivity()) * getWidth()), getHeight());//这2行,和上面的一样,注意顺序就行;
            window.setWindowAnimations(getAnimationId());
        }
        getDialog().setCancelable(false);
        getDialog().setCanceledOnTouchOutside(false);

        initView();


        return rootView;

    }


    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//注意此处

            WindowManager.LayoutParams layoutParams = window.getAttributes();

            layoutParams.width = (int) (ScreenUtil.getWidth(getActivity()) * getWidth());
            layoutParams.height = getHeight();
            window.setAttributes(layoutParams);
        }

    }

    public View getView(int resId) {
        return rootView.findViewById(resId);
    }


    protected float getWidth() {
        return 0.6f;
    }


    public int getAnimationId() {
        return 0;
    }


    public int getHeight() {
        return ViewGroup.LayoutParams.WRAP_CONTENT;
    }


    public void initView() {
        setCancelable(false);

        tvContent = (TextView) getView(R.id.tv_content);
        tvAgreeBtn = (TextView) getView(R.id.tv_agree_btn);
        tvExitBtn = (TextView) getView(R.id.tv_exit_btn);

        String link = "为了加强对您个人信息的保护,根据最新法律法规要求,更新了隐私政策,请您仔细阅读并确认\"<a href=\"https://answer.bshu.com/v1/treaty\">隐私权相关政策</a>\"(特别是加粗或下划线标注的内容),同时我们使用了友盟SDK来进行应用统计、应用分享，相关的友盟隐私政策如下:\n" +
                "使用SDK名称：友盟SDK\n" +
                "服务类型：友盟统计、分享\n" +
                "收集个人信息类型：设备信息（IMEI/Mac/android ID/IDFA/OPENUDID/GUID/SIM卡IMSI/地理位置信息）\n" +
                "如有疑问，请仔细阅读\"<a href=\"https://www.umeng.com/page/policy\">友盟隐私政策</a>\",\n" +
                "我们严格按照政策内容使用和保存您的个人信息,为您提供更好的服务,感谢您的信任。";

        tvContent.setLinkTextColor(ContextCompat.getColor(requireActivity(), R.color.colorPrimary));
        tvContent.setMovementMethod(LinkMovementMethod.getInstance());

        tvContent.setText(SpanUtils.getStringBuilder(getActivity(), link, getString(R.string.privacy_title)));

        RxView.clicks(tvAgreeBtn).subscribe(aVoid -> {
            SharePreferenceUtils.getInstance().putBoolean(Config.index_dialog,true);
            UMConfigure.init(requireActivity(), "5bcd726cb465f50926000194", "umeng", UMConfigure.DEVICE_TYPE_PHONE, "");
            if (onGrantListener != null) {
                onGrantListener.onAgree();
            }
            dismiss();
        });
        RxView.clicks(tvExitBtn).subscribe(aVoid -> {
            dismiss();
            android.os.Process.killProcess(android.os.Process.myPid());
        });
    }

    private OnGrantListener onGrantListener;

    public void setOnGrantListener(OnGrantListener onGrantListener) {
        this.onGrantListener = onGrantListener;
    }

    public interface OnGrantListener {
        void onAgree();
    }
}
