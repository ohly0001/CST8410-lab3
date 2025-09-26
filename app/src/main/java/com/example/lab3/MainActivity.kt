package com.example.lab3

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.lab3.ui.theme.Lab3Theme

class MainActivity : ComponentActivity() {
    private val requestActivityRecognitionPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                Log.d("Permission", "ACTIVITY_RECOGNITION granted")
            } else {
                Log.w("Permission", "ACTIVITY_RECOGNITION denied")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACTIVITY_RECOGNITION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestActivityRecognitionPermission.launch(Manifest.permission.ACTIVITY_RECOGNITION)
        }
        setContent {
            Lab3Theme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Column(verticalArrangement = Arrangement.SpaceAround) {
                        Text(text = "Sensor Readings", fontWeight = FontWeight.SemiBold)

                        // Light sensor
                        val lightValue = remember { mutableFloatStateOf(0f) }
                        RememberSensorValue(Sensor.TYPE_LIGHT) { event ->
                            lightValue.floatValue = event.values[0]
                        }
                        Lighting(lightValue.floatValue)

                        // Accelerometer
                        val accelerationValues = remember { mutableStateListOf(0f, 0f, 0f) }
                        RememberSensorValue(Sensor.TYPE_ACCELEROMETER) { event ->
                            accelerationValues[0] = event.values[0]
                            accelerationValues[1] = event.values[1]
                            accelerationValues[2] = event.values[2]
                        }
                        Acceleration(accelerationValues)

                        // Step counter
                        val steps = remember { mutableIntStateOf(0) }
                        val initialSteps = remember { mutableIntStateOf(-1) }
                        RememberSensorValue(Sensor.TYPE_STEP_COUNTER) { event ->
                            val totalSteps = event.values[0].toInt()
                            if (initialSteps.intValue == -1) {
                                initialSteps.intValue = totalSteps
                            }
                            steps.intValue = totalSteps - initialSteps.intValue
                        }
                        Steps(steps.intValue)
                    }
                }
            }
        }
    }
}

@Composable
fun Lighting(lightValue: Float) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(R.drawable.sun_icon),
            contentDescription = "Sun Icon",
            modifier = Modifier.size(30.dp)
        )
        Text(text = "The light level is ${"%.2f".format(lightValue)} lux")
    }
}

@Composable
fun Acceleration(accelerationValues: List<Float>) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(R.drawable.speed_icon),
            contentDescription = "Speed Dial Icon",
            modifier = Modifier.size(30.dp)
        )
        Text(
            text = "Acceleration (x=${"%.2f".format(accelerationValues[0])}, " +
                    "y=${"%.2f".format(accelerationValues[1])}, " +
                    "z=${"%.2f".format(accelerationValues[2])}) m/s²"
        )
    }
}

@Composable
fun Steps(steps: Int) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(R.drawable.walking_icon),
            contentDescription = "Walking Icon",
            modifier = Modifier.size(30.dp)
        )
        Text(text = "Steps since app launch: $steps")
    }
}

@Composable
fun RememberSensorValue(
    sensorType: Int,
    delay: Int = SensorManager.SENSOR_DELAY_NORMAL,
    onEvent: (SensorEvent) -> Unit
) {
    val context = LocalContext.current
    val sensorManager = remember {
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current

    DisposableEffect(sensorType, lifecycleOwner) {
        val sensor = sensorManager.getDefaultSensor(sensorType)
        val listener = object : SensorEventListener {
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
            override fun onSensorChanged(event: SensorEvent) = onEvent(event)
        }

        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    sensor?.let {
                        sensorManager.registerListener(listener, it, delay)
                    }
                }
                Lifecycle.Event.ON_PAUSE -> {
                    sensorManager.unregisterListener(listener)
                }
                else -> {}
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            sensorManager.unregisterListener(listener)
        }
    }
}