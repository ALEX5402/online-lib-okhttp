package com.libonline.module

import com.squareup.moshi.Json

data class Database (
    @Json(name = "serverstatus") val serverStatus: String?,
    @Json(name = "noticemode") val noticeMode: String?,
    @Json(name = "dataclearmode") val dataClearMode: String?,
    @Json(name = "servermeassage") val serverMessage: String?,
    @Json(name = "opentime") val openTime: String?,
    @Json(name = "libs") val libs: String?,
    @Json(name = "notice_title") val noticeTitle: String?,
    @Json(name = "notice_body") val noticeBody: String?
)

