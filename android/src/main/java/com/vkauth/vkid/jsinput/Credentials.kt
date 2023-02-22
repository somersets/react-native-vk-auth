package com.vkauth.vkid.jsinput

import com.facebook.react.bridge.ReadableMap

data class Credentials(
  val clientId: String,
  val clientSecret: String
) {
  companion object {
    fun fromMap(map: ReadableMap): Credentials {
      return Credentials(
        clientId = map.getString("clientId")!!,
        clientSecret = map.getString("clientSecret")!!
      )
    }
  }
}
