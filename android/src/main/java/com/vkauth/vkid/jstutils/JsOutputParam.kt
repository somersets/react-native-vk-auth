package com.vkauth.vkid.jstutils

import com.facebook.react.bridge.WritableMap

internal interface JsOutputParam {
  fun toMap(): WritableMap
}
