package com.vkauth.vkid.dto

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.WritableMap
import com.vkauth.vkid.jstutils.JsOutputParam


internal data class SilentAuthInfo(
  val silentToken: Token,
  val uuid: String,
  val firstName: String,
  val lastName: String,
) : JsOutputParam {
  override fun toMap(): WritableMap {
    return Arguments.createMap().apply {
      putMap("token", silentToken.toMap())
      putString("uuid", uuid)
      putString("firstName", firstName)
      putString("lastName", lastName)
    }
  }
}
