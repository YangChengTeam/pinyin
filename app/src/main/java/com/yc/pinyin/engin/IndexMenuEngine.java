package com.yc.pinyin.engin;

import android.content.Context;

import com.alibaba.fastjson.TypeReference;
import com.kk.securityhttp.domain.ResultInfo;
import com.kk.securityhttp.engin.BaseEngin;
import com.yc.pinyin.domain.Config;
import com.yc.pinyin.domain.IndexMenuInfoWrapper;

import rx.Observable;

/**
 * Created by wanglin  on 2019/2/26 17:54.
 */
public class IndexMenuEngine extends BaseEngin<ResultInfo<IndexMenuInfoWrapper>> {
    public IndexMenuEngine(Context context) {
        super(context);
    }

    @Override
    public String getUrl() {
        return Config.INDEX_MENU_URL;
    }

    public Observable<ResultInfo<IndexMenuInfoWrapper>> getIndexMenuInfo() {
        return rxpost(new TypeReference<ResultInfo<IndexMenuInfoWrapper>>() {
        }.getType(), null, true, true, true);
    }
}
