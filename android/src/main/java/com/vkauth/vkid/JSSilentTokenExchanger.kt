package com.vkauth.vkid

import com.facebook.react.bridge.ReactApplicationContext
import com.vk.auth.main.SilentAuthSource
import com.vk.auth.main.VkFastLoginModifiedUser
import com.vk.auth.main.VkSilentTokenExchanger
import com.vkauth.vkid.dto.SilentAuthInfo
import com.vkauth.vkid.dto.Token
import com.vkauth.vkid.jstutils.JsCbSender

internal class JSSilentTokenExchanger(
  private val context: ReactApplicationContext
) : VkSilentTokenExchanger {
  private val jsCallbackSender = JsCbSender()

  override fun exchangeSilentToken(
    user: com.vk.silentauth.SilentAuthInfo,
    modifiedUser: VkFastLoginModifiedUser?,
    source: SilentAuthSource
  ): VkSilentTokenExchanger.Result {
    jsCallbackSender.sendCallback(context, SILENT_DATA_EVENT, SilentAuthInfo(
      silentToken = Token(user.token),
      uuid = user.uuid,
      firstName = user.firstName,
      lastName = user.lastName,
    ).toMap())
    return VkSilentTokenExchanger.Result.Success(accessToken = "will be exchanged in js part", uid = 1)
  }

  private companion object {
    private const val SILENT_DATA_EVENT = "onSilentDataReceive"
  }
}
