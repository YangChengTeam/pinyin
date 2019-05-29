package com.yc.pinyin.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.yc.pinyin.R;
import com.yc.pinyin.domain.ExampleInfo;
import com.yc.pinyin.listener.PerfectClickListener;
import com.yc.pinyin.ui.views.holder.LearnRecyclerHolder;
import com.yc.pinyin.utils.LPUtils;
import java.util.List;

/**
 * TinyHung@Outlook.com
 * 2017/12/18.
 * 学音标音标适配器
 */

public class RecyclerViewLPContentListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<ExampleInfo> mData=null;
    private String mLearn=null;
    private final LayoutInflater mInflater;


    public RecyclerViewLPContentListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new LearnRecyclerHolder(mInflater.inflate(R.layout.lp_content_list_item,null));
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final LearnRecyclerHolder viewHolder= (LearnRecyclerHolder) holder;
        ExampleInfo data = mData.get(position);
        if(null!=data){
            viewHolder.tv_item_content.setText(data.getWord());
            viewHolder.tv_item_content_lp.setText(Html.fromHtml(LPUtils.getInstance().addWordLetterColor(data.getWordPhonetic(),data.getLetter())));
            viewHolder.ll_item.setOnClickListener(new PerfectClickListener() {
                @Override
                protected void onClickView(View v) {
                    if(null!=mOnItemClickListener){
                        mOnItemClickListener.onItemClick(viewHolder,position);
                    }
                }
            });
        }
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return null==mData?0:mData.size();
    }


    public void setNewData(List<ExampleInfo> data,String learn) {
        this.mData=data;
        this.mLearn=learn;
        this.notifyDataSetChanged();
    }

    public List<ExampleInfo> getData() {
        return mData;
    }

    public interface OnItemClickListener{
        void onItemClick(LearnRecyclerHolder holder, int position);
    }
    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }
}
