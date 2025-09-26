package com.example.lab3

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SensorComposeDynamicTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun lightSensorText_updatesCorrectly() {
        val simulatedLight = 456.78f

        composeTestRule.setContent {
            Lighting(lightValue = simulatedLight)
        }

        composeTestRule.onNodeWithText("The light level is 456.78 lux").assertExists()
    }

    @Test
    fun accelerometerSensorText_updatesCorrectly() {
        val simulatedAccel = listOf(1.1f, 2.2f, 3.3f)

        composeTestRule.setContent {
            Acceleration(accelerationValues = simulatedAccel)
        }

        composeTestRule.onNodeWithText(
            "Acceleration (x=1.10, y=2.20, z=3.30) m/s²"
        ).assertExists()
    }

    @Test
    fun stepsText_updatesCorrectly() {
        val simulatedSteps = 123

        composeTestRule.setContent {
            Steps(steps = simulatedSteps)
        }

        composeTestRule.onNodeWithText("Steps since app launch: 123").assertExists()
    }
}