package com.vkauth.vkid.jsinput

import com.facebook.react.bridge.ReadableMap

data class App(
  val credentials: Credentials,
  val mode: Mode
) {
  companion object {
    fun fromMap(map: ReadableMap): App {
      return App(
        credentials = Credentials.fromMap(map.getMap("credentials")!!),
        mode = Mode.fromString(map.getString("mode")!!)
      )
    }
  }
}
