package com.vkauth.vkid

import com.facebook.react.bridge.ReactApplicationContext
import com.vkauth.vkid.jsinput.App
import com.vkauth.vkid.jsinput.VKID

class InitDelegate(
  private val context: ReactApplicationContext
) {
  fun initialize(app: App, vkid: VKID) {
    SuperAppKitInitUtils.initSuperappKit(context, app, vkid)
  }
}
