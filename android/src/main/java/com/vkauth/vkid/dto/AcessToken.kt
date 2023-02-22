package com.vkauth.vkid.dto

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.WritableMap
import com.vk.dto.common.id.UserId
import com.vkauth.vkid.jstutils.JsOutputParam

internal data class AccessToken(
  val token: Token,
  val userId: UserId
) : JsOutputParam {

  override fun toMap(): WritableMap {
    return Arguments.createMap().apply {
      putMap("token", token.toMap())
      putMap("userId", userId.toMap())
    }
  }
}
