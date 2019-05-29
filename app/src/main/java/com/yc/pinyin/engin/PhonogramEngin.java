package com.yc.pinyin.engin;

import android.content.Context;

import com.alibaba.fastjson.TypeReference;
import com.kk.securityhttp.domain.ResultInfo;
import com.kk.securityhttp.engin.BaseEngin;
import com.yc.pinyin.domain.Config;
import com.yc.pinyin.domain.PhonogramListInfo;

import rx.Observable;

/**
 * Created by zhangkai on 2017/12/19.
 */

public class PhonogramEngin extends BaseEngin {
    public PhonogramEngin(Context context) {
        super(context);
    }

    @Override
    public String getUrl() {
        return Config.PHONOGRAM_LIST_URL;
    }

    public Observable<ResultInfo<PhonogramListInfo>> getPhonogramList() {
        return rxpost(new TypeReference<ResultInfo<PhonogramListInfo>>() {
        }.getType(), null, true, true, true);
    }
}
