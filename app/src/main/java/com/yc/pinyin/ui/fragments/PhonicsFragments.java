package com.yc.pinyin.ui.fragments;

import android.graphics.Paint;
import android.text.Html;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xinqu.videoplayer.XinQuVideoPlayer;
import com.yc.pinyin.App;
import com.yc.pinyin.R;
import com.yc.pinyin.domain.GoodInfo;
import com.yc.pinyin.domain.MClassInfo;
import com.yc.pinyin.domain.MClassListInfo;
import com.yc.pinyin.engin.MClassEngine;
import com.yc.pinyin.helper.SeekBarHelper;
import com.yc.pinyin.helper.UserInfoHelper;
import com.yc.pinyin.observer.BaseCommonObserver;
import com.yc.pinyin.ui.activitys.MainActivity;
import com.yc.pinyin.ui.pager.PhonicsVideoPager;
import com.yc.pinyin.ui.popupwindow.PayPopupWindow;
import com.yc.pinyin.ui.views.PhoniceSeekBarView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

/**
 * Created by zhangkai on 2017/12/15.
 * 首页自然拼读
 */

public class PhonicsFragments extends BaseFragment {

    private Map<Integer, PhonicsVideoPager> playerViews;
    private List<MClassInfo> mMClassInfos;
    private PhoniceVideoPlayerPagerAdapter mPlayerPagerAdapter;
    private PhoniceSeekBarView mPhoniceView;
    private ViewPager mViewPager;
    private TextView mTvOriPrice;
    private TextView mTvNewPrice;
    private TextView mTvPhDesp;
    private TextView mStrokeTitle;
    private GoodInfo mGoodInfo;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_phonics;
    }

    @Override
    public void init() {
        playerViews = new HashMap<>();
        mViewPager = (ViewPager) getView(R.id.view_pager);
        mPhoniceView = (PhoniceSeekBarView) getView(R.id.phonice_view);
        initPagerAdapter();
        mTvOriPrice = (TextView) getView(R.id.tv_ori_price);
        mTvNewPrice = (TextView) getView(R.id.tv_new_price);
        mTvPhDesp = (TextView) getView(R.id.tv_ph_desp);
        mStrokeTitle = (TextView) getView(R.id.stroke_title);
    }

    private void initPagerAdapter() {
        mPlayerPagerAdapter = new PhoniceVideoPlayerPagerAdapter();
        mViewPager.setAdapter(mPlayerPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                XinQuVideoPlayer.releaseAllVideos();
                mPhoniceView.setIndex(position);
                if (position >= 1 && !MainActivity.getMainActivity().isPhonicsVip()) {
                    if (!App.isTrial) {
                        mPhoniceView.setIndex(0);
                        mViewPager.setCurrentItem(0, false);
                        if (UserInfoHelper.isLogin(getActivity())) {
                            PayPopupWindow payPopupWindow = new PayPopupWindow(getActivity());
//                        payPopupWindow.setTimeListener(MainActivity.getMainActivity());
                            payPopupWindow.show(getActivity().getWindow().getDecorView(), Gravity.CENTER);
                        }
                        return;
                    }
                }
                updataPhoniceContent(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mPhoniceView.setIndexListener(new SeekBarHelper.IndexListener() {
            @Override
            public void leftClick(int position) {
                if (null != mViewPager && mViewPager.getChildCount() > 0) {
                    mViewPager.setCurrentItem(position);
                }
            }

            @Override
            public void rightClcik(int position) {
                if (null != mViewPager && mViewPager.getChildCount() > 0) {
                    mViewPager.setCurrentItem(position);
                }
            }
        });
    }

    /**
     * 刷新右边的拼读介绍
     *
     * @param position
     */
    private void updataPhoniceContent(int position) {
        if (null != mMClassInfos && mMClassInfos.size() > 0) {
            MClassInfo mClassInfo = mMClassInfos.get(position);
            if (null != mClassInfo && null != mGoodInfo) {
                mStrokeTitle.setText(TextUtils.isEmpty(mClassInfo.getDesp()) ? "趣味音标课" : mClassInfo.getDesp());
//                mStrokeTitle.setText();
                mTvOriPrice.setText("原价" + mGoodInfo.getPrice() + "元");
                mTvOriPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                mTvNewPrice.setText(Html.fromHtml("感恩钜惠<font color='#FD0000'><big><big>" + mGoodInfo.getReal_price() + "</big></big></font>元"));
                mTvPhDesp.setText(mClassInfo.getTitle());
            }
        }
    }

    @Override
    public void loadData() {
        MClassEngine mClassEngin = new MClassEngine(getActivity());
        mClassEngin.getMClassList().subscribe(new BaseCommonObserver<MClassListInfo>(getActivity()) {
            @Override
            public void onSuccess(MClassListInfo data, String message) {
                if (null != data && null != data.getMClassInfos()) {
                    mMClassInfos = data.getMClassInfos();
                    mGoodInfo = data.getInfo();
                    if (null != mPlayerPagerAdapter) {
                        mPlayerPagerAdapter.notifyDataSetChanged();
                    } else {
                        initPagerAdapter();
                    }
                    updataPhoniceContent(0);
                    mPhoniceView.showIndex(mMClassInfos.size());
                    mPhoniceView.setIndex(0);
                } else {
                    loadData();
                }
            }

            @Override
            public void onFailure(int code, String errorMsg) {

            }

            @Override
            public void onRequestComplete() {

            }
        });

//        mClassEngin.getMClassList().observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<ResultInfo<MClassListInfo>>() {
//
//            @Override
//            public void call(ResultInfo<MClassListInfo> mClassListInfoResultInfo) {
//                if (null != mClassListInfoResultInfo && 1 == mClassListInfoResultInfo.code && null != mClassListInfoResultInfo.data && null != mClassListInfoResultInfo.data.getMClassInfos()) {
//                    mMClassInfos = mClassListInfoResultInfo.data.getMClassInfos();
//                    mGoodInfo = mClassListInfoResultInfo.data.getInfo();
//                    if (null != mPlayerPagerAdapter) {
//                        mPlayerPagerAdapter.notifyDataSetChanged();
//                    } else {
//                        initPagerAdapter();
//                    }
//                    updataPhoniceContent(0);
//                    mPhoniceView.showIndex(mMClassInfos.size());
//                    mPhoniceView.setIndex(0);
//                } else {
//                    loadData();
//                }
//            }
//        });
    }

    @Override
    public void onPause() {
        super.onPause();
        XinQuVideoPlayer.goOnPlayOnPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (null != playerViews) {
            playerViews.clear();
        }
    }

    /**
     * 视频播放列表
     */
    private class PhoniceVideoPlayerPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return null == mMClassInfos ? 0 : mMClassInfos.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            MClassInfo data = mMClassInfos.get(position);
            if (null != data) {
                PhonicsVideoPager videoPager = new PhonicsVideoPager(getActivity(), data);
                View view = videoPager.getItemView();
                view.setId(position);
                playerViews.put(position, videoPager);
                container.addView(view);
                return view;
            }
            return null;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(container.findViewById(position));
            playerViews.remove(position);
        }
    }
}
