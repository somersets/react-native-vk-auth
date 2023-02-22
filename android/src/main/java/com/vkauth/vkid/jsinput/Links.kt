package com.vkauth.vkid.jsinput

import com.facebook.react.bridge.ReadableMap

data class Links(
  val serviceUserAgreement: String,
  val servicePrivacyPolicy: String,
  val serviceSupport: String?
) {
  companion object {
    fun fromMap(map: ReadableMap): Links {
      return Links(
        serviceUserAgreement = map.getString("serviceUserAgreement")!!,
        servicePrivacyPolicy = map.getString("servicePrivacyPolicy")!!,
        serviceSupport = map.getString("serviceSupport"),
      )
    }
  }
}
