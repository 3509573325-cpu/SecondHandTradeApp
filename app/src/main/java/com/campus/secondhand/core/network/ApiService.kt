package com.campus.secondhand.core.network

import com.campus.secondhand.data.model.remote.Goods
import com.campus.secondhand.data.model.remote.GoodsDetailResponse
import com.campus.secondhand.data.model.remote.GoodsResponse
import com.campus.secondhand.data.model.remote.LoginRequest
import com.campus.secondhand.data.model.remote.LoginResponse
import com.campus.secondhand.data.model.remote.PublishGoodsRequest
import com.campus.secondhand.data.model.remote.PublishGoodsResponse
import com.campus.secondhand.data.model.remote.RechargeRequest
import com.campus.secondhand.data.model.remote.RechargeResponse
import com.campus.secondhand.data.model.remote.RegisterRequest
import com.campus.secondhand.data.model.remote.RegisterResponse
import com.campus.secondhand.data.model.remote.SchoolVerifyRequest
import com.campus.secondhand.data.model.remote.SchoolVerifyResponse
import com.campus.secondhand.data.model.remote.UpdateUserProfileRequest
import com.campus.secondhand.data.model.remote.UpdateUserProfileResponse
import com.campus.secondhand.data.model.remote.UploadImageResponse
import com.campus.secondhand.data.model.remote.UserInfoResponse
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

// 后端 API 接口定义
interface ApiService {

    // 注册接口
    @POST("user/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): RegisterResponse
    // 登录接口
    @POST("user/login")
    suspend fun login(
        @Body request: LoginRequest
    ): LoginResponse

    // 商品列表接口
    @GET("goods/list")
    suspend fun getGoodsList(): GoodsResponse
    //商品详情接口
    @GET("goods/detail/{goodsId}") // 路径参数占位符
    suspend fun getGoodsDetail(@Path("goodsId") goodsId: String): GoodsDetailResponse<Goods>

    // 发布商品接口
    @POST("goods/publish")
    suspend fun publishGoods(
        @Body request: PublishGoodsRequest
    ): PublishGoodsResponse

    //图片上传接口
    @Multipart
    @POST("goods/image")
    suspend fun uploadImage(
        @Part image: MultipartBody.Part // 图片文件Part
    ): UploadImageResponse // 上传返回结果

    // 查询用户信息
    @GET("user/info")
    suspend fun getUserInfo(@Query("userId") userId: String): UserInfoResponse

    // 更新用户资料（用户名+个性签名）
    @POST("user/profile/update")
    suspend fun updateUserProfile(
        @Body request: UpdateUserProfileRequest
    ): UpdateUserProfileResponse

    // 校园认证提交
    @POST("user/school/verify")
    suspend fun submitSchoolVerify(
        @Body request: SchoolVerifyRequest
    ): SchoolVerifyResponse

    // 充值接口
    @POST("user/balance/recharge")
    suspend fun rechargeBalance(
        @Body request: RechargeRequest
    ): RechargeResponse

    // 查询余额接口
    @GET("user/balance")
    suspend fun getBalance(@Query("userId") userId: String): RechargeResponse

}