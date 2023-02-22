package com.vkauth

import com.facebook.react.ReactPackage
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.uimanager.ViewManager
import com.vkauth.vkid.AuthDelegate
import com.vkauth.vkid.InitDelegate
import com.vkauth.vkid.onetapbutton.OneTabButtonManager


class VkAuthPackage : ReactPackage {
  override fun createNativeModules(reactContext: ReactApplicationContext): List<NativeModule> {
    return listOf(VkAuthModule(reactContext,
      InitDelegate(reactContext),
      AuthDelegate(reactContext)
    ))
  }

  override fun createViewManagers(reactContext: ReactApplicationContext): List<ViewManager<*, *>> {
    return listOf(OneTabButtonManager())
  }
}
