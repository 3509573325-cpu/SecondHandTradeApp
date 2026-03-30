package com.campus.secondhand.data.model.ui

// 发货方式枚举
enum class GoodsShipType(val typeName: String) {
    FREE_SHIP("包邮"),
    SELF_PICK("自提"),
    CITY_DELIVERY("同区配送")
}