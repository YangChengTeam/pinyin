package com.yc.pinyin.httpinterface;


import android.content.Context;

import com.yc.pinyin.domain.GoodListInfo;
import com.yc.pinyin.domain.IndexMenuInfoWrapper;
import com.yc.pinyin.domain.LoginDataInfo;
import com.yc.pinyin.domain.MClassListInfo;
import com.yc.pinyin.domain.MyOrderInfoWrapper;
import com.yc.pinyin.domain.PhonogramListInfo;
import com.yc.pinyin.domain.UserInfo;
import com.yc.pinyin.domain.UserInfoWrapper;
import com.yc.pinyin.pay.OrderInfo;
import com.yc.pinyin.pay.OrderParamsInfo;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;
import yc.com.rthttplibrary.bean.ResultInfo;


/**
 * Created by suns  on 2020/7/28 14:54.
 */
public interface HttpRequestInterface {

    @POST("index/phonetic_list")
    Observable<ResultInfo<PhonogramListInfo>> getPhonogramList(@Query("app_id") String app_id);

    @POST("index/init")
    Observable<ResultInfo<LoginDataInfo>> getIndexInfo(@Query("app_id") String app_id);

    @POST("index/vip_list")
    Observable<ResultInfo<GoodListInfo>> getGoodList(@Query("app_id") String app_id);

    @POST("index/phonetic_class")
    Observable<ResultInfo<MClassListInfo>> getMClassList(@Query("app_id") String app_id);

    @POST("index/menu_adv")
    Observable<ResultInfo<IndexMenuInfoWrapper>> getIndexMenuInfo(@Query("app_id") String app_id);

    @FormUrlEncoded
    @POST("user/login")
    Observable<ResultInfo<UserInfoWrapper>> login(@Field("username") String username, @Field("pwd") String password, @Query("app_id") String app_id);

    @FormUrlEncoded
    @POST("user/send_code")
    Observable<ResultInfo<String>> sendCode(@Field("mobile") String phone, @Query("app_id") String app_id);


    @FormUrlEncoded
    @POST("user/reg")
    Observable<ResultInfo<UserInfoWrapper>> register(@Field("mobile") String mobile, @Field("code") String code, @Field("pwd") String pwd, @Query("app_id") String app_id);

    @FormUrlEncoded
    @POST("user/upd_pwd")
    Observable<ResultInfo<UserInfo>> modifyPwd(@Field("user_id") String user_id, @Field("pwd") String pwd, @Field("new_pwd") String newPwd, @Query("app_id") String app_id);


    @FormUrlEncoded
    @POST("user/code_login")
    Observable<ResultInfo<UserInfoWrapper>> codeLogin(@Field("mobile") String mobile, @Field("code") String code, @Query("app_id") String app_id);

    @FormUrlEncoded
    @POST("user/set_pwd")
    Observable<ResultInfo<String>> setPwd(@Field("user_id") String user_id, @Field("new_pwd") String pwd, @Query("app_id") String app_id);

    @FormUrlEncoded
    @POST("orders/lists")
    Observable<ResultInfo<MyOrderInfoWrapper>> getOrderList(@Field("user_id") String user_id, @Field("page") int page, @Field("page_size") int page_size, @Query("app_id") String app_id);

    @FormUrlEncoded
    @POST("index/pay")
    Observable<ResultInfo<OrderInfo>> pay(@FieldMap Map<String, Object> params, @Query("app_id") String app_id);


    @FormUrlEncoded
    @POST("index/pay")
    Observable<ResultInfo<OrderInfo>> pay1(@Field("goods_id") String goods_id,
                                           @Field("goods_num") int goods_num,
                                           @Field("is_payway_split") String is_payway_split,
                                           @Field("payway_name") String payway_name,
                                           @Field("money") float price,
                                           @Field("ds_money") String ds_money,
                                           @Field("type") String type,
                                           @Field("user_id") String user_id,
                                           @Query("app_id") String app_id);

    @FormUrlEncoded
    @POST
    Observable<ResultInfo<String>> checkOrder(@Url String url, @FieldMap Map<String, String> params);
}
