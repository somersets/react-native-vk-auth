package com.vkauth.vkid.jstutils

import com.facebook.react.bridge.ReactContext
import com.facebook.react.bridge.WritableMap
import com.facebook.react.modules.core.DeviceEventManagerModule

class JsCbSender {
  fun sendCallback(
    context: ReactContext,
    callbackName: String,
    callbackParams: WritableMap?
  ) {
    context.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
      .emit(callbackName, callbackParams)
  }
}
