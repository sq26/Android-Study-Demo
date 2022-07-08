package com.sq26.experience.ui.fragment

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ObservableField
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import com.sq26.experience.databinding.FragmentSensorsBinding


class SensorsFragment : Fragment() {
    private lateinit var sensorManager: SensorManager
    private var light: Sensor? = null
    private var gravity: Sensor? = null
    private var linearAcceleration: Sensor? = null
    private var rotationVector: Sensor? = null
    private var ambientTemperature: Sensor? = null
    private val viewModel by viewModels<SensorsViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentSensorsBinding.inflate(inflater).apply {
            lifecycleOwner = viewLifecycleOwner
            main = viewModel
            sensorManager =
                requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager

            light = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
            gravity = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)
            linearAcceleration = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
            rotationVector = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
            ambientTemperature = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)

        }.root
    }

    private val lightSensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            //传感器报告了新值
            event?.let {
                viewModel.light.set(it.values.getOrElse(0) { 0.toFloat() }.toString())

            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            //传感器的准确度发生了变化
        }
    }

    private val gravitySensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            event?.let {
                viewModel.gravityX.set("X:" + it.values.getOrElse(0) { 0.toFloat() }.toString())
                viewModel.gravityY.set("Y:" + it.values.getOrElse(1) { 0.toFloat() }.toString())
                viewModel.gravityZ.set("Z:" + it.values.getOrElse(2) { 0.toFloat() }.toString())

            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        }
    }

    private val linearAccelerationSensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            event?.let {
                viewModel.linearAccelerationX.set("X:" + it.values.getOrElse(0) { 0.toFloat() }
                    .toString())
                viewModel.linearAccelerationY.set("Y:" + it.values.getOrElse(1) { 0.toFloat() }
                    .toString())
                viewModel.linearAccelerationZ.set("Z:" + it.values.getOrElse(2) { 0.toFloat() }
                    .toString())
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        }
    }

    private val rotationVectorSensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            event?.let {
                viewModel.rotationVectorX.set("X:" + it.values.getOrElse(0) { 0.toFloat() }
                    .toString())
                viewModel.rotationVectorY.set("Y:" + it.values.getOrElse(1) { 0.toFloat() }
                    .toString())
                viewModel.rotationVectorZ.set("Z:" + it.values.getOrElse(2) { 0.toFloat() }
                    .toString())
                viewModel.rotationVector.set("标量:" + it.values.getOrElse(3) { 0.toFloat() }
                    .toString())
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        }
    }

    private val ambientTemperatureSensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            event?.let {
                viewModel.ambientTemperature.set("°C:" + it.values.getOrElse(0) { 0.toFloat() }
                    .toString())
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        }
    }

    override fun onResume() {
        super.onResume()
        light?.let {
            sensorManager.registerListener(
                lightSensorEventListener,
                it,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
        gravity?.let {
            sensorManager.registerListener(
                gravitySensorEventListener,
                it,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
        linearAcceleration?.let {
            sensorManager.registerListener(
                linearAccelerationSensorEventListener,
                it,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
        rotationVector?.let {
            sensorManager.registerListener(
                rotationVectorSensorEventListener,
                it,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
        ambientTemperature?.let {
            sensorManager.registerListener(
                ambientTemperatureSensorEventListener,
                it,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }

    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(lightSensorEventListener)
        sensorManager.unregisterListener(gravitySensorEventListener)
        sensorManager.unregisterListener(linearAccelerationSensorEventListener)
        sensorManager.unregisterListener(rotationVectorSensorEventListener)
        sensorManager.unregisterListener(ambientTemperatureSensorEventListener)
    }

}

class SensorsViewModel : ViewModel() {

    val light = ObservableField("")
    val gravityX = ObservableField("")
    val gravityY = ObservableField("")
    val gravityZ = ObservableField("")
    val linearAccelerationX = ObservableField("")
    val linearAccelerationY = ObservableField("")
    val linearAccelerationZ = ObservableField("")
    val rotationVectorX = ObservableField("")
    val rotationVectorY = ObservableField("")
    val rotationVectorZ = ObservableField("")
    val rotationVector = ObservableField("")
    val ambientTemperature = ObservableField("")

}