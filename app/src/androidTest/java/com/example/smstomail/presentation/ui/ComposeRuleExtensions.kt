package com.example.smstomail.presentation.ui

import androidx.activity.ComponentActivity
import androidx.annotation.StringRes
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.rules.ActivityScenarioRule

fun <A: ComponentActivity> AndroidComposeTestRule<ActivityScenarioRule<A>, A>.onNodeWithStringId(@StringRes id: Int): SemanticsNodeInteraction = onNodeWithText(activity.getString(id))
fun <A: ComponentActivity> AndroidComposeTestRule<ActivityScenarioRule<A>, A>.onNodeWithTagStringId(@StringRes id: Int): SemanticsNodeInteraction = onNodeWithTag(activity.getString(id))