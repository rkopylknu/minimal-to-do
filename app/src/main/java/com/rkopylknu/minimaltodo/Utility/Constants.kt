package com.rkopylknu.minimaltodo.util

import com.rkopylknu.minimaltodo.R

const val STORAGE_FILE_NAME = "todoitems.json"
const val REMINDER_SERVICE_NAME = "reminder_service"

const val SHARED_PREFS_NAME = "app_preferences"
const val PREFS_THEME_KEY = "theme"
const val PREFS_THEME_LIGHT = "light"
const val PREFS_THEME_DARK = "dark"

val PREFS_TO_THEMES = mapOf(
    PREFS_THEME_LIGHT to R.style.Theme_MinimalToDo_Light,
    PREFS_THEME_DARK to R.style.Theme_MaterialToDo_Dark
)

val TO_DO_ITEM_COLORS = setOf(
    -0x1a8c8d,
    -0xf9d6e,
    -0x459738,
    -0x6a8a33,
    -0x867935,
    -0x9b4a0a,
    -0xb03c09,
    -0xb22f1f,
    -0xb24954,
    -0x7e387c,
    -0x512a7f,
    -0x759b,
    -0x2b1ea9,
    -0x2ab1,
    -0x48b3,
    -0x5e7781,
    -0x6f5b52
)