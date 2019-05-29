package com.yc.pinyin.engin;

import android.content.Context;

import com.alibaba.fastjson.TypeReference;
import com.kk.securityhttp.domain.ResultInfo;
import com.kk.securityhttp.engin.BaseEngin;
import com.yc.pinyin.domain.Config;
import com.yc.pinyin.domain.TypeListInfo;


import rx.Observable;

/**
 * Created by zhangkai on 2017/3/1.
 */

public class TypeEngin extends BaseEngin<ResultInfo<TypeListInfo>> {
    public TypeEngin(Context context) {
        super(context);
    }

    @Override
    public String getUrl() {
        return Config.TYPE_LIST_URL;
    }

    public Observable<ResultInfo<TypeListInfo>> getTypeList() {
        return rxpost(new TypeReference<ResultInfo<TypeListInfo>>() {
        }.getType(), null, true, true, true);
    }

}
