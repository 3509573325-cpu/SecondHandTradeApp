package com.campus.secondhand.data.model.remote

import com.campus.secondhand.core.base.BaseResponse


data class RechargeResponse(
    val data: RechargeData?
): BaseResponse()

data class RechargeData(
    val balance: Float // 充值后的余额
)