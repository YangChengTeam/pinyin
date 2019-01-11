package com.yc.phonogram.utils;

import com.blankj.utilcode.constant.TimeConstants;
import com.blankj.utilcode.util.TimeUtils;
import com.yc.phonogram.App;

/**
 * Created by admin on 2018/1/18.
 */

public class CommonUtils {

    public static void initTrial(String time) {
        if (!StringUtils.isEmpty(time)) {
            long spaceSec = TimeUtils.getTimeSpanByNow(time, TimeConstants.SEC);
            if (spaceSec < 24 * 60 * 60) {
                App.isTrial = true;
            } else {
                App.isTrial = false;
            }
        }
    }
}
