package com.vkauth.vkid.jsinput

enum class Mode(val value: String) {
  DEBUG("DEBUG"),
  TEST("TEST"),
  RELEASE("RELEASE");

  companion object {
    fun fromString(value: String): Mode {
      return values().first { it.value == value }
    }
  }
}
