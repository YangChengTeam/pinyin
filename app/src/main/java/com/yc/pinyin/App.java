package com.yc.pinyin;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.danikula.videocache.HttpProxyCacheServer;
import com.kk.share.UMShareImpl;
import com.tencent.bugly.Bugly;
import com.umeng.commonsdk.UMConfigure;
import com.yc.pinyin.domain.Config;
import com.yc.pinyin.domain.IndexMenuInfo;
import com.yc.pinyin.domain.IndexMenuInfoWrapper;
import com.yc.pinyin.domain.LoginDataInfo;
import com.yc.pinyin.domain.UserInfo;
import com.yc.pinyin.domain.UserInfoWrapper;
import com.yc.pinyin.domain.VipInfo;
import com.yc.pinyin.engin.IndexMenuEngine;
import com.yc.pinyin.engin.LoginEngine;
import com.yc.pinyin.engin.PhoneLoginEngine;
import com.yc.pinyin.helper.SharePreferenceUtils;
import com.yc.pinyin.helper.UserInfoHelper;
import com.yc.pinyin.observer.BaseCommonObserver;
import com.yc.pinyin.ui.fragments.IndexDialogFragment;
import com.yc.pinyin.utils.AssetsUtil;
import com.yc.pinyin.utils.LPUtils;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import yc.com.rthttplibrary.config.GoagalInfo;
import yc.com.rthttplibrary.config.HttpConfig;
import yc.com.rthttplibrary.converter.FastJsonConverterFactory;
import yc.com.rthttplibrary.request.RetrofitHttpRequest;
import yc.com.rthttplibrary.util.FileUtil;
import yc.com.rthttplibrary.util.LogUtil;
import yc.com.rthttplibrary.util.PreferenceUtil;
import yc.com.rthttplibrary.util.TaskUtil;
import yc.com.toutiao_adv.TTAdManagerHolder;

/**
 * Created by zhangkai on 2017/10/17.
 */

public class App extends MultiDexApplication {
    private static App INSTANSE;
    public static boolean isTrial;//是否是VIP试用一天用户或者关注公众号的用户

    public static String privacyPolicy;

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANSE = this;
        initGoagal(getApplicationContext());
        getIndexMenu();
        login();
        privacyPolicy = AssetsUtil.readAsset(this, "privacy_policy.txt");
        TTAdManagerHolder.init(this, Config.TOUTIAO_AD_ID);
    }

    public static App getApp() {
        return INSTANSE;
    }

    public static void initGoagal(Context context) {
        //全局信息初始化
        GoagalInfo.get().init(context);

        //设置文件唯一性 防止手机相互拷贝
        FileUtil.setUuid(GoagalInfo.get().uuid);

        HttpConfig.setPublickey("-----BEGIN PUBLIC KEY-----\n" +
                "MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEA1zQ4FOFmngBVc05sg7X5\n" +
                "Z/e3GrhG4rRAiGciUCsrd/n4wpQcKNoOeiRahxKT1FVcC6thJ/95OgBN8jaDzKdd\n" +
                "cMUti9gGzBDpGSS8MyuCOBXc6KCOYzL6Q4qnlGW2d09blZSpFUluDBBwB86yvOxk\n" +
                "5oEtnf6WPw2wiWtm7JR1JrE1k+adYfy+Cx9ifJX3wKZ5X3n+CdDXbUCPBD63eMBn\n" +
                "dy1RYOgI1Sc67bQlQGoFtrhXOGrJ8vVoRNHczaGeBOev96/V0AiEY2f5Kw5PAWhw\n" +
                "NrAF94DOLu/4OyTVUg9rDC7M97itzBSTwvJ4X5JA9TyiXL6c/77lThXvX+8m/VLi\n" +
                "mLR7PNq4e0gUCGmHCQcbfkxZVLsa4CDg2oklrT4iHvkK4ZtbNJ2M9q8lt5vgsMkb\n" +
                "bLLqe9IuTJ9O7Pemp5Ezf8++6FOeUXBQTwSHXuxBNBmZAonNZO1jACfOzm83zEE2\n" +
                "+Libcn3EBgxPnOB07bDGuvx9AoSzLjFk/T4ScuvXKEhk1xqApSvtPADrRSskV0aE\n" +
                "G5F8PfBF//krOnUsgqAgujF9unKaxMJXslAJ7kQm5xnDwn2COGd7QEnOkFwqMJxr\n" +
                "DmcluwXXaZXt78mwkSNtgorAhN6fXMiwRFtwywqoC3jYXlKvbh3WpsajsCsbTiCa\n" +
                "SBq4HbSs5+QTQvmgUTPwQikCAwEAAQ==\n" +
                "-----END PUBLIC KEY-----");

        new RetrofitHttpRequest.Builder()
                .url(Config.getBaseUrl())
                .convert(FastJsonConverterFactory.create());
        //设置http默认参数
        String agent_id = "1";
        Map<String, String> params = new HashMap<>();
        if (GoagalInfo.get().channelInfo != null && GoagalInfo.get().channelInfo.agent_id != null) {
            params.put("from_id", GoagalInfo.get().channelInfo.from_id + "");
            params.put("author", GoagalInfo.get().channelInfo.author + "");
            agent_id = GoagalInfo.get().channelInfo.agent_id;
        }
        params.put("agent_id", agent_id);
        params.put("imeil", GoagalInfo.get().uuid);
        String sv = getSV();
        params.put("sv", sv);
        params.put("device_type", "2");
        if (GoagalInfo.get().packageInfo != null) {
            params.put("app_version", GoagalInfo.get().packageInfo.versionCode + "");
        }
        HttpConfig.setDefaultParams(params);
        yc.com.rthttplibrary.config.HttpConfig.setDefaultParams(params);

        //腾迅自动更新
        Bugly.init(context, context.getString(R.string.bugly_id), false);


        //动态设置渠道信息
//        String appId_agentId = context.getResources().getString(R.string.app_name) + "-渠道id" + agent_id;
////        MobclickAgent.startWithConfigure(new MobclickAgent.UMAnalyticsConfig(context,
////                "5a4c31338f4a9d2161000159", appId_agentId));
////        //友盟统计
////        UMGameAgent.setDebugMode(true);
////        UMGameAgent.init(context);
////        UMGameAgent.setPlayerLevel(1);
////        MobclickAgent.setScenarioType(context, MobclickAgent.EScenarioType.E_UM_NORMAL);

        UMConfigure.preInit(context, "5bcd726cb465f50926000194", "Umeng");
        if (SharePreferenceUtils.getInstance().getBoolean(Config.index_dialog)) {
            UMConfigure.init(context, "5bcd726cb465f50926000194", "Umeng", UMConfigure.DEVICE_TYPE_PHONE, null);
        }

//        KSYHardwareDecodeWhiteList.getInstance().init(context);
        //友盟分享
        UMShareImpl.Builder builder = new UMShareImpl.Builder();
        builder.setWeixin("wx6b6c6d943013289c", "c4257eeaa83fd55f1bd69d5fc9df483c")
                .setQQ("1106626200", "Vpa3dJ9rkFNy5kyi")
                .setDebug(true)
                .build(context);
    }

    public static String getSV() {
        return Build.MODEL.contains(Build.BRAND) ? Build.MODEL + " " + Build.VERSION.RELEASE : Build.BRAND + " " + Build.MODEL + " " + Build.VERSION.RELEASE;
    }

    private LoginDataInfo loginDataInfo;

    public LoginDataInfo getLoginDataInfo() {
        return loginDataInfo;
    }

    private LoginEngine loginEngin;

    public void getLoginInfo(final Runnable task) {
        if (loginEngin == null) {
            loginEngin = new LoginEngine(this);
        }
        TaskUtil.getImpl().runTask(new Runnable() {
            @Override
            public void run() {
                String data = PreferenceUtil.getImpl(getApplicationContext()).getString(Config.INIT_URL, "");
                if (!data.isEmpty()) {
                    try {
                        final LoginDataInfo resultInfo = JSON.parseObject(data, new TypeReference<LoginDataInfo>() {
                        }.getType());
                        if (resultInfo != null) {
                            loginDataInfo = resultInfo;
                        }

                    } catch (Exception e) {
                        LogUtil.msg("getLoginInfo本地缓存" + e);
                    }
                }
            }
        });
        loginEngin.rxGetInfo().subscribe(new BaseCommonObserver<LoginDataInfo>(this) {
            @Override
            public void onSuccess(final LoginDataInfo data, String message) {
                if (data != null) {
                    TaskUtil.getImpl().runTask(new Runnable() {
                        @Override
                        public void run() {
                            PreferenceUtil.getImpl(getApplicationContext()).putString(Config.INIT_URL, JSON.toJSONString
                                    (data));
                            loginDataInfo = data;
                            if (task != null) {
                                task.run();
                            }
                        }
                    });
                }
            }

            @Override
            public void onFailure(int code, String errorMsg) {
                if (loginDataInfo == null) {
                    getLoginInfo(task);
                    return;
                }

                if (task != null) {
                    task.run();
                }
            }

            @Override
            public void onRequestComplete() {

            }
        });


    }

    private PhoneLoginEngine phoneLoginEngine;

    private void login() {
        if (phoneLoginEngine == null) {
            phoneLoginEngine = new PhoneLoginEngine(this);
        }
        final UserInfo userInfo = UserInfoHelper.getUser();
        if (userInfo != null) {
            String phone = userInfo.getMobile();
            final String pwd = userInfo.getPwd();
            if (!TextUtils.isEmpty(phone) && !TextUtils.isEmpty(pwd))

                phoneLoginEngine.login(phone, pwd).subscribe(new BaseCommonObserver<UserInfoWrapper>(this) {
                    @Override
                    public void onSuccess(UserInfoWrapper data, String message) {
                        if (data != null) {
                            UserInfo info = data.getUserInfo();
                            List<VipInfo> vipList = data.getVipList();
                            if (info != null) {
                                info.setPwd(pwd);
                            }
                            UserInfoHelper.saveUser(userInfo);
                            UserInfoHelper.saveVipList(vipList);
                        }
                    }

                    @Override
                    public void onFailure(int code, String errorMsg) {

                    }

                    @Override
                    public void onRequestComplete() {

                    }
                });

//            phoneLoginEngine.login(phone, pwd).subscribe(
//                    new Subscriber<ResultInfo<UserInfoWrapper>>() {
//                        @Override
//                        public void onCompleted() {
//
//                        }
//
//                        @Override
//                        public void onError(Throwable e) {
//
//                        }
//
//                        @Override
//                        public void onNext(ResultInfo<UserInfoWrapper> userInfoWrapperResultInfo) {
//                            if (userInfoWrapperResultInfo != null && userInfoWrapperResultInfo.code == HttpConfig.STATUS_OK
//                                    && userInfoWrapperResultInfo.data != null) {
//                                UserInfo info = userInfoWrapperResultInfo.data.getUserInfo();
//                                List<VipInfo> vipList = userInfoWrapperResultInfo.data.getVipList();
//                                if (info != null) {
//                                    info.setPwd(pwd);
//                                }
//                                UserInfoHelper.saveUser(userInfo);
//                                UserInfoHelper.saveVipList(vipList);
//                            }
//                        }
//                    });
        }

    }


    private HttpProxyCacheServer proxy;

    public static HttpProxyCacheServer getProxy() {
        App app = (App) INSTANSE.getApplicationContext();
        return app.proxy == null ? (app.proxy = app.newProxy()) : app.proxy;
    }

    private IndexMenuEngine indexMenuEngine;

    private void getIndexMenu() {
        if (indexMenuEngine == null) {
            indexMenuEngine = new IndexMenuEngine(this);
        }
        indexMenuEngine.getIndexMenuInfo().subscribe(new BaseCommonObserver<IndexMenuInfoWrapper>(this) {
            @Override
            public void onSuccess(IndexMenuInfoWrapper data, String message) {
                if (data != null && data.getH5page() != null) {
                    IndexMenuInfo h5page = data.getH5page();
                    PreferenceUtil.getImpl(App.this).putString(Config.INDEX_MENU_URL, JSON.toJSONString(h5page));
                }
            }

            @Override
            public void onFailure(int code, String errorMsg) {

            }

            @Override
            public void onRequestComplete() {

            }
        });

//        indexMenuEngine.getIndexMenuInfo().subscribe(new Action1<ResultInfo<IndexMenuInfoWrapper>>() {
//            @Override
//            public void call(ResultInfo<IndexMenuInfoWrapper> indexMenuInfoWrapperResultInfo) {
//                if (indexMenuInfoWrapperResultInfo != null && indexMenuInfoWrapperResultInfo.code == HttpConfig.STATUS_OK
//                        && indexMenuInfoWrapperResultInfo.data != null && indexMenuInfoWrapperResultInfo.data.getH5page() != null) {
//                    IndexMenuInfo h5page = indexMenuInfoWrapperResultInfo.data.getH5page();
//                    PreferenceUtil.getImpl(App.this).putString(Config.INDEX_MENU_URL, JSON.toJSONString(h5page));
//                }
//            }
//        });

    }

    /**
     * 构造100M大小的缓存池
     *
     * @return
     */
    private HttpProxyCacheServer newProxy() {
        int cacheSize = 100 * 1024 * 1024;
        String videoCacheDir = LPUtils.getInstance().getVideoCacheDir(getApplicationContext());
        //如果SD卡已挂载并且可读写
        if (null == videoCacheDir) {
            return null;
        }
        //优先使用内部缓存
        return new HttpProxyCacheServer.Builder(this)
                .cacheDirectory(new File(videoCacheDir))
                .maxCacheSize(cacheSize)//1BG缓存大小上限
                .build();
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
