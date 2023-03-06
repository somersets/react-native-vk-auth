package com.vkauth

import com.facebook.react.bridge.*
import com.vkauth.vkid.AuthDelegate
import com.vkauth.vkid.InitDelegate
import com.vkauth.vkid.jsinput.App
import com.vkauth.vkid.jsinput.VKID

class VkAuthModule(
  reactContext: ReactApplicationContext,
  private val initDelegate: InitDelegate,
  private val authDelegate: AuthDelegate
) :
  ReactContextBaseJavaModule(reactContext) {

  override fun getName(): String {
    return "VkAuth"
  }

  @ReactMethod
  fun initialize(app: ReadableMap, vkid: ReadableMap) {
    initDelegate.initialize(App.fromMap(app), VKID.fromMap(vkid))
  }

  @ReactMethod
  fun startAuth() {
    authDelegate.startAuth()
  }

  @ReactMethod
  fun closeAuth() {
    authDelegate.closeAuth()
  }

  // to ignore react warning
  @ReactMethod
  fun addListener(eventName: String) {
  }

  @ReactMethod
  fun removeAllListeners() {
  }

  @ReactMethod
  fun removeListeners(type: Int?) {
  }

  @ReactMethod
  fun accessTokenChangedSuccess(token: String, userId: Int) {
    authDelegate.accessTokenChangedSuccess(token, userId)
  }

  @ReactMethod
  fun accessTokenChangedFailed(error: ReadableMap) {
    authDelegate.accessTokenChangedFailed()
  }

  @ReactMethod
  fun logout() {
    authDelegate.logout()
  }

  @ReactMethod
  fun getUserSessions(promise: Promise) {
    authDelegate.getUserSessions(promise)
  }

  @ReactMethod
  fun getUserProfile(promise: Promise) {
    authDelegate.getUserProfile(promise)
  }
}
