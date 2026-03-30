package com.campus.secondhand.data.model.local

import androidx.room.TypeConverter
import java.math.BigDecimal

/**
 * BigDecimal 类型转换器：将 BigDecimal 与 String 互转
 * 选择 String 作为中间类型是为了避免精度丢失（比转 Double 更安全）
 */
object BigDecimalConverter {

    // 将 BigDecimal 转为 String 存入数据库
    @TypeConverter
    @JvmStatic
    fun fromBigDecimal(value: BigDecimal?): String? {
        return value?.toString()
    }

    // 将数据库中的 String 转回 BigDecimal
    @TypeConverter
    @JvmStatic
    fun toBigDecimal(value: String?): BigDecimal? {
        return value?.let { BigDecimal(it) } ?: BigDecimal.ZERO
    }
}