package com.example.chronometer


import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.widget.Chronometer
import android.widget.Chronometer.OnChronometerTickListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chronometer.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Timer


class MainActivity : AppCompatActivity(), OnChronometerTickListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var chronometer1: Chronometer
    private lateinit var chronometer2: Chronometer
    private var pauseAt: Long = 0
    private var chronoIsTicking = false
    private var tenSecondsCounter: Int = 0
    private var toastCounter: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        chronometer1 = binding.myChrono

        chronometer2 = binding.chronometer2
        chronometer2.onChronometerTickListener = this
        chronometer2.start()

        buttons()
    }



    override fun onDestroy() {
        super.onDestroy()
        chronometer1.stop()
        chronometer2.stop()
    }


    /** Кнопки хронометра: запуск, пауза и сброс.
     */
    private fun buttons() = with(binding) {

        /* Кнопка Play. */
        btPlay.setOnClickListener {
            if (chronoIsTicking == false) {
                chronoIsTicking = true
                chronometer1.base = SystemClock.elapsedRealtime() - pauseAt
                chronometer1.start()
                pauseAt = 0
            }
        }

        /* Кнопка Пауза. */
        btPause.setOnClickListener {
            if (chronoIsTicking == true) {
                chronoIsTicking = false
                pauseAt = SystemClock.elapsedRealtime() - chronometer1.base
                chronometer1.stop()
            }
        }

        /* Кнопка Сброс. */
        btReset.setOnClickListener {
            chronometer1.base = SystemClock.elapsedRealtime()
            pauseAt = 0
            chronometer1.stop()
            chronoIsTicking = false
        }
    }


    /** Функция отвечает за то, что происходит каждую секунду времени, пока работает хронометр.
     */
    override fun onChronometerTick(chrono: Chronometer) {

        /* Каждую секунду обновляет label длительности сессии. */
        firstThreadUpdateLabelEverySecond(chrono.text.toString())

        if (tenSecondsCounter >= 10) {
            toastCounter++

            /* Каждый 4ый показ Toast меняем время на SURPRISE !!! */
            if (toastCounter >= 4) {
                thirdThreadShowSurprise()
                toastCounter = 0

            /* Каждые 10 секунд показываем время в виде Toast. */
            } else {
                secondThreadShowToastEvery10Seconds(chrono.text.toString())
            }

            tenSecondsCounter = 0
        }

        tenSecondsCounter++
    }


    /** Первый поток.
     */
    private fun firstThreadUpdateLabelEverySecond(textTime: String) {
        CoroutineScope(Dispatchers.IO).launch {
            runOnUiThread {
                binding.tvLabel.text = textTime
            }
        }
    }


    /** Второй поток.
     */
    private fun secondThreadShowToastEvery10Seconds(textTime: String) {
        CoroutineScope(Dispatchers.IO).launch {
            runOnUiThread {
                Toast.makeText(this@MainActivity, textTime, Toast.LENGTH_SHORT).show()
            }
        }
    }


    /** Третий поток.
     */
    private fun thirdThreadShowSurprise() {
        CoroutineScope(Dispatchers.IO).launch {
            secondThreadShowToastEvery10Seconds("SURPRISE !!!")
        }
    }


}