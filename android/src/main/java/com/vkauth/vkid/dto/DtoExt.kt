package com.vkauth.vkid.dto

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.WritableMap
import com.vk.auth.main.VkClientAuthLib
import com.vk.dto.common.id.UserId
import com.vk.superapp.api.dto.account.ProfileShortInfo

fun UserId.toMap(): WritableMap {
  return Arguments.createMap().apply {
    putDouble("value", value.toDouble())
  }
}

fun ProfileShortInfo.toMap(): WritableMap {
  return Arguments.createMap().apply {
    putMap("userID", VkClientAuthLib.getUserId().toMap())
    putString("firstName", firstName)
    putString("lastName", lastName)
    putString("phone", phone)
    putString("photo200", photo200)
    putString("userHash", userHash)
  }
}
