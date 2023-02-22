package com.vkauth.vkid.dto

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.WritableMap
import com.vkauth.vkid.jstutils.JsOutputParam

internal sealed class UserSession(val type: String) : JsOutputParam {
  object Authorized: UserSession(AUTHORIZED) {
    override fun toMap(): WritableMap {
      return Arguments.createMap().apply {
        putString("type", type)
      }
    }
  }

  object Authenticated : UserSession(AUTHENTICATED) {
    override fun toMap(): WritableMap {
      return Arguments.createMap().apply {
        putString("type", type)
      }
    }
  }

  private companion object {
    private const val AUTHORIZED = "authorized"
    private const val AUTHENTICATED = "authenticated"
  }
}
