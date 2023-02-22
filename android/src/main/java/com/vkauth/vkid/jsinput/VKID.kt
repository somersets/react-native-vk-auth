package com.vkauth.vkid.jsinput

import com.facebook.react.bridge.ReadableMap

data class VKID(
  val appName: String,
  val appVersion: String,
  val icon: ReadableMap,
  val links: Links
) {
  companion object {
    fun fromMap(map: ReadableMap): VKID {
      return VKID(
        appName = map.getString("appName")!!,
        appVersion = map.getString("appVersion")!!,
        icon = map.getMap("appIcon")!!,
        links = Links.fromMap(map.getMap("appLinks")!!)
      )
    }
  }
}
