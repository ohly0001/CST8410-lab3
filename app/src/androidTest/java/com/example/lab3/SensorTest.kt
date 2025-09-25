package com.example.lab3

import android.widget.TextView
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.junit.runner.RunWith

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class SensorTextDisplayTest {

    private lateinit var activity: MainActivity

    @Before
    fun setup() {
        activity = Robolectric.buildActivity(MainActivity::class.java).setup().get()
    }

    @Test
    fun `light sensor text is displayed`() {
        val lightValue = 123.4f
        activity.updateLightText(lightValue) // pretend this updates the TextView
        val textView = activity.findViewById<TextView>(R.id.lightTextView)
        assertEquals("Light: $lightValue", textView.text.toString())
    }

    @Test
    fun `accelerometer sensor text is displayed`() {
        val x = 1.0f
        val y = 2.0f
        val z = 3.0f
        activity.updateAccelerometerText(x, y, z)
        val textView = activity.findViewById<TextView>(R.id.accelerometerTextView)
        assertEquals("Accelerometer: x=$x, y=$y, z=$z", textView.text.toString())
    }

    @Test
    fun `step counter text is displayed`() {
        val steps = 42
        activity.updateStepCounterText(steps)
        val textView = activity.findViewById<TextView>(R.id.stepCounterTextView)
        assertEquals("Steps: $steps", textView.text.toString())
    }

    fun updateLightText(value: Float) {
        findViewById<TextView>(R.id.lightTextView).text = "Light: $value"
    }

    fun updateAccelerometerText(x: Float, y: Float, z: Float) {
        findViewById<TextView>(R.id.accelerometerTextView).text = "Accelerometer: x=$x, y=$y, z=$z"
    }

    fun updateStepCounterText(steps: Int) {
        findViewById<TextView>(R.id.stepCounterTextView).text = "Steps: $steps"
    }
}
