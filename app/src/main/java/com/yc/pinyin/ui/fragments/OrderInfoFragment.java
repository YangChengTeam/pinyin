package com.yc.pinyin.ui.fragments;

import android.graphics.Rect;
import android.view.View;
import android.widget.ImageView;

import com.jakewharton.rxbinding.view.RxView;
import com.kk.loading.LoadingDialog;

import com.yc.pinyin.R;
import com.yc.pinyin.domain.MyOrderInfo;
import com.yc.pinyin.domain.MyOrderInfoWrapper;
import com.yc.pinyin.engin.OrderListEngine;
import com.yc.pinyin.observer.BaseCommonObserver;
import com.yc.pinyin.ui.activitys.MainActivity;
import com.yc.pinyin.ui.adapter.OrderInfoAdapter;

import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import rx.Subscriber;
import rx.functions.Action1;
import yc.com.rthttplibrary.util.ScreenUtil;

/**
 * Created by suns  on 2020/4/3 15:46.
 */
public class OrderInfoFragment extends BaseFragment {


    private ImageView ivBack;
    private OrderInfoAdapter orderInfoAdapter;
    private OrderListEngine orderListEngine;
    private int page = 1;
    private int page_size = 10;
    private LoadingDialog loadingDialog;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_order_info;
    }

    @Override
    public void init() {
        orderListEngine = new OrderListEngine(getActivity());
        loadingDialog = new LoadingDialog(getActivity());
        RecyclerView recyclerView = (RecyclerView) getView(R.id.recyclerView);
        ivBack = (ImageView) getView(R.id.iv_back);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        orderInfoAdapter = new OrderInfoAdapter(null);

        recyclerView.setAdapter(orderInfoAdapter);
        recyclerView.addItemDecoration(new ItemDecoration());
        initData();
        initListener();
    }

    private void initListener() {
        RxView.clicks(ivBack).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                if (getActivity() != null) {
//                    PersonCenterFragment centerFragment= new PersonCenterFragment();
                    ((MainActivity) getActivity()).popFragment();
                }
            }
        });
    }

    private void initData() {
//        List<MyOrderInfo> orderInfos = new ArrayList<>();
//        for (int i = 0; i < 5; i++) {
//            orderInfos.add(new MyOrderInfo());
//        }


        if (page == 1) loadingDialog.show("获取订单中...");

        orderListEngine.getOrderList(page, page_size).subscribe(new BaseCommonObserver<MyOrderInfoWrapper>(getActivity()) {
            @Override
            public void onSuccess(MyOrderInfoWrapper data, String message) {
                if (page == 1) loadingDialog.dismiss();

                if (data != null) {
                    List<MyOrderInfo> orderList = data.getOrderList();

                    setNewData(orderList);
                }

            }

            @Override
            public void onFailure(int code, String errorMsg) {
                if (page == 1) loadingDialog.dismiss();
            }

            @Override
            public void onRequestComplete() {

            }
        });
//        orderListEngine.getOrderList(page, page_size).subscribe(new Subscriber<ResultInfo<MyOrderInfoWrapper>>() {
//            @Override
//            public void onCompleted() {
//                if (page == 1) loadingDialog.dismiss();
//            }
//
//            @Override
//            public void onError(Throwable e) {
//
//            }
//
//            @Override
//            public void onNext(ResultInfo<MyOrderInfoWrapper> listResultInfo) {
//                if (listResultInfo != null) {
//                    if (listResultInfo.code == HttpConfig.STATUS_OK && listResultInfo.data != null) {
//                        List<MyOrderInfo> orderList = listResultInfo.data.getOrderList();
//
//                        setNewData(orderList);
//                    }
//                }
//            }
//        });
    }

    private void setNewData(List<MyOrderInfo> orderInfos) {
        if (page == 1) {
            orderInfoAdapter.setNewData(orderInfos);
        } else {
            orderInfoAdapter.addData(orderInfos);
        }

        if (orderInfos.size() == page_size) {
            page++;
            orderInfoAdapter.loadMoreComplete();
        } else {
            orderInfoAdapter.loadMoreEnd();
        }
    }


    private class ItemDecoration extends RecyclerView.ItemDecoration {
        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.set(0, 0, 0, ScreenUtil.dip2px(getActivity(), 10));
        }
    }
}
