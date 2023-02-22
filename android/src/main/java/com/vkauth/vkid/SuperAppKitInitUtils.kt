package com.vkauth.vkid

import android.app.Application
import android.content.Context
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import com.facebook.react.bridge.ReactApplicationContext
import com.vk.auth.main.VkClientAuthLibConfig
import com.vk.auth.main.VkClientLibverifyInfo
import com.vk.auth.main.VkClientUiInfo
import com.vk.superapp.SuperappKit
import com.vk.superapp.SuperappKitConfig
import com.vk.superapp.core.SuperappConfig
import com.vkauth.vkid.jsinput.App
import com.vkauth.vkid.jsinput.VKID
import java.net.URL

internal object SuperAppKitInitUtils {
  private const val DRAWABLE_DEF_TYPE = "drawable"
  private val HTTP_SCHEMES = listOf("http", "https")

  fun initSuperappKit(reactAppContext: ReactApplicationContext, app: App, vkid: VKID) {
    if (SuperappKit.isInitialized()) {
      return
    }

    val appInfo = SuperappConfig.AppInfo(
      appName = vkid.appName,
      appVersion = vkid.appVersion,
      appId = app.credentials.clientId,
    )

    val logo = resolveLogoUri(reactAppContext, Uri.parse(vkid.icon.getString("uri")))

    val builder = SuperappKitConfig.Builder(reactAppContext.applicationContext as Application)
      .setAuthModelData(
        VkClientAuthLibConfig.AuthModelData(
          clientSecret = app.credentials.clientSecret,
          libverifyInfo = VkClientLibverifyInfo.disabled(),
          ignoreSuccessAuth = true
        )
      )
      .setAuthUiManagerData(VkClientUiInfo(icon48 = logo, icon56 = logo, appName = vkid.appName))
      .setLegalInfoLinks(
        serviceUserAgreement = vkid.links.serviceUserAgreement,
        servicePrivacyPolicy = vkid.links.servicePrivacyPolicy,
        serviceSupport = vkid.links.serviceSupport
      )
      .setApplicationInfo(appInfo)
      .setSilentTokenExchanger(JSSilentTokenExchanger(reactAppContext))
      .sslPinningEnabled(false)
      .build()

    SuperappKit.init(builder)
  }

  private fun resolveLogoUri(context: Context, uri: Uri): Drawable {
    return if (uri.scheme in HTTP_SCHEMES) {
      URL(uri.toString()).openConnection()
        .apply { connect() }
        .getInputStream()
        .use {
          BitmapDrawable(Resources.getSystem(), BitmapFactory.decodeStream(it))
        }
    } else {
      val fileName = uri.toString()
      val resourceId = context.resources.getIdentifier(fileName, DRAWABLE_DEF_TYPE, context.packageName)
      val bitmap = BitmapFactory.decodeResource(context.resources, resourceId)
      BitmapDrawable(Resources.getSystem(), bitmap)
    }
  }
}
