package com.yc.pinyin.ui.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import com.yc.pinyin.R;
import com.yc.pinyin.domain.MyOrderInfo;

import java.util.List;

/**
 * Created by suns  on 2020/4/3 20:44.
 */
public class OrderInfoAdapter extends BaseQuickAdapter<MyOrderInfo, BaseViewHolder> {


    public OrderInfoAdapter(List<MyOrderInfo> data) {
        super(R.layout.order_item_view, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, MyOrderInfo item) {
//        SimpleDateFormat sm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        helper.setText(R.id.tv_order_id, "订单号：" + item.getOrder_sn())
                .setText(R.id.tv_money, "¥ " + item.getMoney())
                .setText(R.id.tv_date, item.getAdd_time())
                .setText(R.id.tv_state, "订单状态：" + item.getStatus_txt());
    }
}
