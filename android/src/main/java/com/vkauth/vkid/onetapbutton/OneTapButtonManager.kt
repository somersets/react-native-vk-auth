package com.vkauth.vkid.onetapbutton

import android.content.Context
import android.graphics.Color
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.uimanager.LayoutShadowNode
import com.facebook.react.uimanager.SimpleViewManager
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.annotations.ReactProp
import com.facebook.react.views.text.ReactTextShadowNode
import com.vk.auth.ui.fastloginbutton.VkFastLoginButton

class OneTabButtonManager : SimpleViewManager<VkFastLoginButton>() {
  override fun getName(): String = COMPONENT_NAME

  override fun createShadowNodeInstance(context: ReactApplicationContext): LayoutShadowNode {
    return ReactTextShadowNode()
  }

  override fun createViewInstance(context: ThemedReactContext): VkFastLoginButton {
    return VkFastLoginButton(context)
  }

  @ReactProp(name = "backgroundStyle")
  fun setStyle(view: VkFastLoginButton, style: ReadableMap) {
    android.util.Log.d(TAG, "setStyle($style)")

    val vkStyle = when (style.getString("style")) {
      "BLUE" -> VkFastLoginButton.ButtonStyle.BLUE
      "WHITE" -> VkFastLoginButton.ButtonStyle.WHITE
      "CUSTOM" -> VkFastLoginButton.ButtonStyle.CUSTOM
      else -> null
    } ?: return

    if (vkStyle == VkFastLoginButton.ButtonStyle.CUSTOM) {
      val vkIconColor = when (style.getString("customVkIconColor")) {
        "BLUE" -> VkFastLoginButton.VkIconColor.BLUE
        "WHITE" -> VkFastLoginButton.VkIconColor.WHITE
        else -> null
      } ?: return

      view.setCustomStyle(
        bgColor = Color.parseColor(style.getString("customBackgroundColor")),
        textColor = Color.parseColor(style.getString("customTextColor")),
        vkIconColor = vkIconColor
      )
    } else {
      view.setButtonStyle(vkStyle)
    }

  }

  @ReactProp(name = "iconGravity")
  fun setGravity(view: VkFastLoginButton, gravity: String) {
    android.util.Log.d(TAG, "setGravity($gravity)")

    val vkGravity = when (gravity) {
      "START" -> VkFastLoginButton.VkIconGravity.START
      "TEXT" -> VkFastLoginButton.VkIconGravity.TEXT
      else -> null
    } ?: return

    view.setVkIconGravity(vkGravity)
  }

  @ReactProp(name = "firstLineFieldType")
  fun setFirstLineField(view: VkFastLoginButton, type: String) {
    android.util.Log.d(TAG, "setFirstLineField($type)")

    val vkType = when (type) {
      "ACTION" -> VkFastLoginButton.LineFieldType.ACTION
      "PHONE" -> VkFastLoginButton.LineFieldType.PHONE
      "NONE" -> VkFastLoginButton.LineFieldType.NONE
      else -> null
    } ?: return

    view.setFirstLineField(vkType)
  }

  @ReactProp(name = "secondLineFieldType")
  fun setSecondLineField(view: VkFastLoginButton, type: String) {
    android.util.Log.d(TAG, "setSecondLineField($type)")
    val vkType = when (type) {
      "ACTION" -> VkFastLoginButton.LineFieldType.ACTION
      "PHONE" -> VkFastLoginButton.LineFieldType.PHONE
      "NONE" -> VkFastLoginButton.LineFieldType.NONE
      else -> null
    } ?: return

    view.setSecondLineField(vkType)
  }

  @ReactProp(name = "oneLineTextSize")
  fun setOneLineTextSize(view: VkFastLoginButton, size: Float) {
    android.util.Log.d(TAG, "setOneLineTextSize($size)")
    view.setOneLineTextSize(size)
  }

  @ReactProp(name = "firstLineTextSize")
  fun setFirstLineTextSize(view: VkFastLoginButton, size: Float) {
    android.util.Log.d(TAG, "setFirstLineTextSize($size)")
    view.setFirstLineTextSize(size)
  }

  @ReactProp(name = "secondLineTextSize")
  fun setSecondLineTextSize(view: VkFastLoginButton, size: Float) {
    android.util.Log.d(TAG, "setSecondLineTextSize($size)")
    view.setSecondLineTextSize(size)
  }

  @ReactProp(name = "avatarSize")
  fun setAvatarSize(view: VkFastLoginButton, size: Float) {
    android.util.Log.d(TAG, "setAvatarSize($size)")

    view.setAvatarSize(size.toInt())
  }

  @ReactProp(name = "iconSize")
  fun setVkIconSize(view: VkFastLoginButton, size: Float) {
    android.util.Log.d(TAG, "setVkIconSize($size)")
    view.setVkIconSize(size.toInt())
  }

  @ReactProp(name = "progressSize")
  fun setProgressSize(view: VkFastLoginButton, size: Float) {
    android.util.Log.d(TAG, "setProgressSize($size)")
    view.setProgressSize(size.toInt())
  }

  @ReactProp(name = "texts")
  fun setTexts(view: VkFastLoginButton, texts: ReadableMap) {
    android.util.Log.d(TAG, "setTexts($texts)")

    view.setTextGetter(
      object : VkFastLoginButton.TextGetter() {
        override fun getNoUserText(
          context: Context,
          actionTextSize: VkFastLoginButton.ActionTextSize
        ): String {
          return texts.getString("noUserText") ?: super.getNoUserText(context, VkFastLoginButton.ActionTextSize.BIG)
        }

        override fun getActionText(
          context: Context,
          firstName: String,
          lastName: String,
          actionTextSize: VkFastLoginButton.ActionTextSize
        ): String {
          val text = texts.getString("actionText")
            ?.replace("{firstName}", firstName)
            ?.replace("{lastName}", lastName)
          return text ?: super.getActionText(context, firstName, lastName, VkFastLoginButton.ActionTextSize.BIG)
        }

        override fun getPhoneText(context: Context, phone: String): String {
          val text = texts.getString("phoneText")
            ?.replace("{phone}", phone)
          return text ?: super.getPhoneText(context, phone)
        }
      }
    )
  }

  private companion object {
    private const val COMPONENT_NAME = "RTCVkOneTapButton"
    private const val TAG = "OneTabButtonManager"
  }
}
