package com.vkauth.vkid

import androidx.fragment.app.FragmentActivity
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.vk.api.sdk.VKApiCallback
import com.vk.auth.main.VkClientAuthCallback
import com.vk.auth.main.VkClientAuthLib
import com.vk.auth.ui.fastlogin.VkFastLoginBottomSheetFragment
import com.vk.dto.common.id.UserId
import com.vk.superapp.api.dto.account.ProfileShortInfo
import com.vk.superapp.bridges.LogoutReason
import com.vkauth.vkid.dto.UserSession
import com.vkauth.vkid.dto.toMap
import com.vkauth.vkid.jstutils.JsCbSender

class AuthDelegate(
  private val context: ReactApplicationContext
) {
  private val jsCallbackSender = JsCbSender()

  init {
    VkClientAuthLib.addAuthCallback(object : VkClientAuthCallback {
      override fun onLogout(logoutReason: LogoutReason) {
        super.onLogout(logoutReason)
        jsCallbackSender.sendCallback(context, ON_LOGOUT_EVENT, null)
      }
    })
  }

  fun startAuth() {
    val activity = context.currentActivity as? FragmentActivity ?: return

    if (isShown()) {
      return
    }

    VkFastLoginBottomSheetFragment.Builder()
      .setDismissOnComplete(false)
      .show(activity.supportFragmentManager, FAST_LOGIN_TAG)
  }

  fun closeAuth() {
    val activity = context.currentActivity as? FragmentActivity ?: return
    (activity.supportFragmentManager.findFragmentByTag(FAST_LOGIN_TAG) as? BottomSheetDialogFragment)
      ?.dismissAllowingStateLoss()
      ?: run {
        (activity.supportFragmentManager
          .fragments
          .find { it is VkFastLoginBottomSheetFragment } as? BottomSheetDialogFragment)
          ?.dismissAllowingStateLoss()
      }
  }

  fun accessTokenChangedSuccess(accessToken: String, userId: Double) {
    VkClientAuthLib.saveAccessToken(
      accessToken = accessToken,
      userId = UserId(userId.toLong()),
      secret = null
    )

    val userSessionJson = UserSession.Authorized.toMap()
    jsCallbackSender.sendCallback(context, ON_AUTH_EVENT, userSessionJson)

    closeAuth()
  }

  fun accessTokenChangedFailed() {
    closeAuth()
  }

  fun logout() {
    VkClientAuthLib.logout()
  }

  fun getUserSessions(promise: Promise) {
    val creds = VkClientAuthLib.getAccessToken() ?: run {
      promise.resolve(Arguments.createArray())
      return
    }

    val array = Arguments.createArray().apply {
      pushMap(UserSession.Authorized.toMap())
    }
    promise.resolve(array)
  }

  private fun isShown(): Boolean {
    val activity = context.currentActivity as? FragmentActivity ?: return false

    val byTag = (activity.supportFragmentManager.findFragmentByTag(FAST_LOGIN_TAG) as? BottomSheetDialogFragment) != null

    if (byTag) {
      return true
    }

    return activity.supportFragmentManager
      .fragments
      .any { it is VkFastLoginBottomSheetFragment }
  }

  fun getUserProfile(promise: Promise) {
    val profile = VkClientAuthLib.getProfileInfo()?.toMap() ?: run {
      VkClientAuthLib.updateUserInfo(
        object : VKApiCallback<ProfileShortInfo> {
          override fun fail(error: Exception) {
            promise.reject(error)
          }
          override fun success(result: ProfileShortInfo) {
            promise.resolve(result.toMap())
          }
        }
      )
      return
    }
    promise.resolve(profile)
  }

  private companion object {
    private const val FAST_LOGIN_TAG = "vk_fast_login"
    private const val ON_AUTH_EVENT = "onAuth"
    private const val ON_LOGOUT_EVENT = "onLogout"
  }
}
