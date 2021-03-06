package com.yc.pinyin.domain;

/**
 * Created by zhangkai on 2017/10/19.
 */

public class Config {


    public static boolean DEBUG = false;

    public final static int PHONOGRAM_VIP = 5;
    public final static int PHONICS_VIP = 6;
    public final static int PHONOGRAMORPHONICS_VIP = 7;
    public final static int SUPER_VIP = 8;

    public static final String APPID = "?app_id=6";

    public static final String INIT_URL = getBaseUrl() + "index/init" + APPID;
    public static final String ORDER_URL = getBaseUrl() + "index/pay" + APPID;
    public static final String VIP_LIST_URL = getBaseUrl() + "index/vip_list" + APPID;
    public final static String CHECK_URL = getBaseUrl() + "index/orders_check" + APPID;
    public final static String QUERY_URL = getBaseUrl() + "index/orders_query" + APPID;
    public static final String PAY_WAY_LIST_URL = getBaseUrl() + "index/payway_list" + APPID;
    public static final String TYPE_LIST_URL = getBaseUrl() + "index/vip_flag_list" + APPID;

    public static final String PHONOGRAM_LIST_URL = getBaseUrl() + "index/phonetic_list" + APPID;
    public static final String MCLASS_LIST_URL = getBaseUrl() + "index/phonetic_class" + APPID;

    public static final String INDEX_MENU_URL = getBaseUrl() + "index/menu_adv" + APPID;

    public static final String FOLLOW_DATE = "follow_data";


    public static final String AV_APPID = "1108023823";

    public static final String AV_SPLASH_ID = "5070254031870826";

    public static final String index_dialog = "index_dialog";

    public static final String TOUTIAO_AD_ID="5044638";

    public static final String TOUTIAO_SPLASH_ID="887291487";
    public static String getBaseUrl() {
        String baseUrl = "http://tic.upkao.com/api/";
        String debugBaseUrl = "http://120.76.202.236:1980/api/";
        return (DEBUG ? debugBaseUrl : baseUrl);
    }
}
