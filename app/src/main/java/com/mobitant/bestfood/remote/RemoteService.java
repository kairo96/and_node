package com.mobitant.bestfood.remote;

import com.mobitant.bestfood.item.FoodInfoItem;
import com.mobitant.bestfood.item.KeepItem;
import com.mobitant.bestfood.item.MemberInfoItem;

import java.util.ArrayList;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * 서버에 호출할 메소드를 선언하는 인터페이스
 */
public interface RemoteService {
    String BASE_URL = "http://192.168.1.188:3000";
    String MEMBER_ICON_URL = BASE_URL + "/member/";
    String IMAGE_URL = BASE_URL + "/img/";

    //사용자 정보
    @GET("/member/{phone}")
    Call<MemberInfoItem> selectMemberInfo(@Path("phone") String phone);

    @POST("/member/info")
    Call<String> insertMemberInfo(@Body MemberInfoItem memberInfoItem);

    @FormUrlEncoded
    @POST("/member/phone")
    Call<String> insertMemberPhone(@Field("phone") String phone);

    @Multipart
    @POST("/member/icon_upload")
    Call<ResponseBody> uploadMemberIcon(@Part("member_seq") RequestBody memberSeq,
                                        @Part MultipartBody.Part file);

    //맛집 정보
    @GET("/food/info/{info_seq}")
    Call<FoodInfoItem> selectFoodInfo(@Path("info_seq") int foodInfoSeq,
                                      @Query("member_seq") int memberSeq);

    @POST("/food/info")
    Call<String> insertFoodInfo(@Body FoodInfoItem infoItem);

    @Multipart
    @POST("/food/info/image")
    Call<ResponseBody> uploadFoodImage(@Part("info_seq") RequestBody infoSeq,
                                       @Part("image_memo") RequestBody imageMemo,
                                       @Part MultipartBody.Part file);

    @GET("/food/list")
    Call<ArrayList<FoodInfoItem>> listFoodInfo(@Query("member_seq") int memberSeq,
                                               @Query("user_latitude") double userLatitude,
                                               @Query("user_longitude") double userLongitude,
                                               @Query("order_type") String orderType,
                                               @Query("current_page") int currentPage);


    //지도
    @GET("/food/map/list")
    Call<ArrayList<FoodInfoItem>> listMap(@Query("member_seq") int memberSeq,
                                          @Query("latitude") double latitude,
                                          @Query("longitude") double longitude,
                                          @Query("distance") int distance,
                                          @Query("user_latitude") double userLatitude,
                                          @Query("user_longitude") double userLongitude);


    //즐겨찾기
    @POST("/keep/insert/{member_seq}/{info_seq}")
    Call<String> insertKeep(@Path("member_seq") int memberSeq, @Path("info_seq") int infoSeq);

    @DELETE("/keep/delete/{member_seq}/{info_seq}")
    Call<String> deleteKeep(@Path("member_seq") int memberSeq, @Path("info_seq") int infoSeq);

    @GET("/keep/list")
    Call<ArrayList<KeepItem>> listKeep(@Query("member_seq") int memberSeq,
                                       @Query("user_latitude") double userLatitude,
                                       @Query("user_longitude") double userLongitude);
}