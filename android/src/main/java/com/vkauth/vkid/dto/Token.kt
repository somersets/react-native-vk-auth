package com.vkauth.vkid.dto

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.WritableMap
import com.vkauth.vkid.jstutils.JsOutputParam

internal data class Token(
  val value: String
) : JsOutputParam {
  override fun toMap(): WritableMap {
    return Arguments.createMap().apply {
      putString("value", value)
    }
  }
}
