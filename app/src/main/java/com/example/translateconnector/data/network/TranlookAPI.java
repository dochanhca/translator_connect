package com.example.translateconnector.data.network;

import com.imoktranslator.model.NotificationSetting;
import com.imoktranslator.model.OrderModel;
import com.imoktranslator.model.TestItem;
import com.imoktranslator.network.param.ChangePasswordParam;
import com.imoktranslator.network.param.ForgotPasswordParam;
import com.imoktranslator.network.param.LoginParam;
import com.imoktranslator.network.param.PersonalInfoParam;
import com.imoktranslator.network.param.ResendOTPParam;
import com.imoktranslator.network.param.UpdatePasswordParam;
import com.imoktranslator.network.param.UpdatePriceParam;
import com.imoktranslator.network.param.UserRegisterParam;
import com.imoktranslator.network.param.VerifyOTPParam;
import com.imoktranslator.network.param.VotePartnerParam;
import com.imoktranslator.network.response.ForgotPasswordResponse;
import com.imoktranslator.network.response.GeneralInfoResponse;
import com.imoktranslator.network.response.IntroduceResponse;
import com.imoktranslator.network.response.ListOrderNotificationResponse;
import com.imoktranslator.network.response.ListPriceResponse;
import com.imoktranslator.network.response.NotificationSettingResponse;
import com.imoktranslator.network.response.OrderInfoResponse;
import com.imoktranslator.network.response.OrderListResponse;
import com.imoktranslator.network.response.PersonalInfoResponse;
import com.imoktranslator.network.response.RatingDetailResponse;
import com.imoktranslator.network.response.RegisterResponse;
import com.imoktranslator.network.response.ResendOTPResponse;
import com.imoktranslator.network.response.SocialNotificationResponse;
import com.imoktranslator.network.response.UpdatePriceResponse;
import com.imoktranslator.network.response.VerifyOTPResponse;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Streaming;

/**
 * Created by ducpv on 3/24/18.
 */

public interface TranlookAPI {

    @GET("posts/{number}")
    Call<TestItem> testAPI(@Path("number") int number);

    @GET("tutorials")
    Call<IntroduceResponse> fetchIntroduceData();

    @POST("register")
    Call<RegisterResponse> register(@Body UserRegisterParam param);

    @POST("oauth/token")
    Call<PersonalInfoResponse> login(@Body LoginParam param);

    @POST("verification")
    Call<VerifyOTPResponse> verifyOTP(@Body VerifyOTPParam param);

    @POST("resend_otp")
    Call<ResendOTPResponse> resendOTP(@Body ResendOTPParam param);

    @POST("forgot_password")
    Call<ForgotPasswordResponse> forgotPassword(@Body ForgotPasswordParam param);

    @POST("update_password")
    Call<UpdatePasswordParam> updatePassword(@Body UpdatePasswordParam param);

    @GET("self/me")
    Call<PersonalInfoResponse> fetchPersonalInfo();

    @GET("self/user_info/{id}")
    Call<PersonalInfoResponse> fetchUserInfo(@Path("id") int id);

    @Multipart
    @POST("self/me")
    Call<PersonalInfoResponse> updateProfileImage(@Part MultipartBody.Part file);

    @POST("self/me")
    Call<PersonalInfoResponse> updateProfileInfo(@Body PersonalInfoParam param);

    @POST("self/me")
    Call<PersonalInfoResponse> updateProfileInfo(@Body Map<String, String> params);

    @POST("self/register_translator")
    Call<PersonalInfoResponse> signUpTranslator(@Body PersonalInfoParam param);

    @Multipart
    @POST("self/register_translator")
    Call<PersonalInfoResponse> signUpTranslator(@Part MultipartBody.Part file);

    @Streaming
    @Multipart
    @POST("self/upload_certificate")
    Call<PersonalInfoResponse> uploadCertificates(@Part List<MultipartBody.Part> files);

    @PUT("self/change_password")
    Call<ChangePasswordParam> changePassword(@Body ChangePasswordParam param);

    @GET("self/delete_avatar")
    Call<PersonalInfoResponse> deleteAvatar();

    @POST("self/order")
    Call<OrderInfoResponse> createOrder(@Body OrderModel param);

    @GET("self/order")
    Call<OrderListResponse> getOrders(@Query("status[]") List<Integer> statuses,
                                      @Query("sort") String sort, @Query("type") String type,
                                      @Query("page") int page, @Query("per_page") int perPage);

    @GET("self/order")
    Call<OrderListResponse> getOrders(@Query("sort") String sort, @Query("type") String type,
                                      @Query("page") int page, @Query("per_page") int perPage);

    @GET("self/order")
    Call<OrderListResponse> getOrders(@Query("status[]") List<Integer> statuses,
                                      @Query("sort") String sort, @Query("type") String type,
                                      @Query("trans_screen") String tranScreen,
                                      @Query("page") int page, @Query("per_page") int perPage);

    @POST("self/order/{id}")
    Call<OrderInfoResponse> updateOrderStatus(@Path("id") int id, @Body Map<String, String> params);

    @DELETE("self/order/{id}")
    Call<Void> deleteOrder(@Path("id") int id);

    @POST("self/order/{id}/price")
    Call<UpdatePriceResponse> updateOrderPrice(@Path("id") int id, @Body UpdatePriceParam params);

    @GET("self/order/{id}/price")
    Call<ListPriceResponse> getListPrice(@Path("id") int id,
                                         @Query("sort") String sort, @Query("type") String type);

    @POST("self/order/{order_id}/price/{price_id}")
    Call<Void> acceptPrice(@Path("order_id") int orderId, @Path("price_id") int priceId);

    @DELETE("self/order/{order_id}/price/{price_id}")
    Call<Void> deletePrice(@Path("order_id") int orderId, @Path("price_id") int priceId);

    @GET("self/fcm_notifications")
    Call<SocialNotificationResponse> getSocialNotifications(@Query("page") int page, @Query("per_page") int perPage);

    @GET("self/notification/order")
    Call<ListOrderNotificationResponse> getOrderNotifications(@Query("page") int page, @Query("per_page") int perPage);

    @DELETE("self/notification/{notification_id}/order")
    Call<Void> deleteNotification(@Path("notification_id") int notificationId);

    @DELETE("self/fcm_notifications/{notification_id}")
    Call<Void> deleteSocialNotification(@Path("notification_id") int notificationId);

    @POST("self/notification/message")
    Call<Void> sendChatNotification(@Body Map<String, Object> params);

    @GET("self/notification/{id}/read")
    Call<Void> markAsRead(@Path("id") int notificationId);

    @GET("self/fcm_notifications/{id}/read")
    Call<Void> socialNotificationMarkAsRead(@Path("id") int notificationId);

    @POST("self/notification/mute")
    @FormUrlEncoded
    Call<Void> blockNotification(@Field("sender_id") int senderId, @Field("order_id") int orderId);

    @POST("self/notification/unmute")
    @FormUrlEncoded
    Call<Void> unblockNotification(@Field("sender_id") int senderId, @Field("order_id") int orderId);

    @POST("self/review")
    Call<Void> votePartner(@Body VotePartnerParam param);

    @GET("self/home")
    Call<GeneralInfoResponse> getGeneralInfo();

    @GET("self/review/{user_id_view}")
    Call<RatingDetailResponse> getRatingDetail(@Path("user_id_view") int userId);

    @GET("self/accept_expand/{order_id}")
    Call<Void> acceptExpand(@Path("order_id") int orderId);

    @GET("self/cancel_expand/{order_id}")
    Call<Void> cancelExpand(@Path("order_id") int orderId);

    @POST("self/fcm_notifications")
    Call<Void> sendNotification(@Body Map<String, Object> params);

    @GET("self/setting_notification")
    Call<NotificationSettingResponse> getNotificationSetting();

    @POST("self/setting_notification")
    Call<NotificationSettingResponse> settingNotification(@Body NotificationSetting params);

    @GET("self/fcm_notification/view")
    Call<Void> viewAllNotification();

    @GET("self/notification/view")
    Call<Void> viewAllOrderNotification();
}
