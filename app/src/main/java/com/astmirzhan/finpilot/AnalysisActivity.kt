package com.astmirzhan.finpilot

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.astmirzhan.finpilot.data.PortfolioRepository
import com.astmirzhan.finpilot.domain.PortfolioAnalyzer
import com.astmirzhan.finpilot.domain.PortfolioInsightEngine
import com.astmirzhan.finpilot.ui.BottomNavHelper
import com.astmirzhan.finpilot.view.PortfolioChartView
import java.util.Locale
import kotlin.math.sqrt

class AnalysisActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var lightSensor: Sensor? = null

    private var lastShakeTime = 0L

    private companion object {
        const val SHAKE_THRESHOLD = 13.0f
        const val SHAKE_COOLDOWN_MS = 2000L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_analysis)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

        updateAnalysis()

        if (lightSensor == null) {
            findViewById<TextView>(R.id.lightStatusText).text = "Light sensor unavailable"
        }

        findViewById<Button>(R.id.manualSensorDemoButton).setOnClickListener {
            Toast.makeText(this, "Manual sensor demo triggered", Toast.LENGTH_SHORT).show()
            findViewById<TextView>(R.id.sensorModeText).text = "Manual demo mode"
            findViewById<TextView>(R.id.accelerometerStatusText).text = "Manual refresh used"
            updateAnalysis()
        }
    }

    override fun onResume() {
        super.onResume()
        BottomNavHelper.setup(this, BottomNavHelper.Tab.ANALYSIS)
        updateAnalysis()
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        lightSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> handleAccelerometer(event)
            Sensor.TYPE_LIGHT -> handleLight(event)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not needed for this app.
    }

    private fun handleAccelerometer(event: SensorEvent) {
        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        val gForce = sqrt(x * x + y * y + z * z) - SensorManager.GRAVITY_EARTH

        if (gForce > SHAKE_THRESHOLD) {
            val now = System.currentTimeMillis()
            if (now - lastShakeTime < SHAKE_COOLDOWN_MS) return
            lastShakeTime = now

            updateAnalysis()
            findViewById<TextView>(R.id.accelerometerStatusText).text =
                "Accelerometer: shake detected"
            Toast.makeText(this, "Quick analysis refreshed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleLight(event: SensorEvent) {
        val lux = event.values[0]

        val mode = if (lux < 30f) "Night analysis mode" else "Day review mode"
        findViewById<TextView>(R.id.sensorModeText).text = mode
        findViewById<TextView>(R.id.lightStatusText).text =
            String.format(Locale.US, "Light: %.1f lux", lux)
    }

    private fun updateAnalysis() {
        val assets = PortfolioRepository.getAssets(this)
        val profile = PortfolioRepository.getRiskProfile(this)

        val totalValue = PortfolioAnalyzer.calculateTotalValue(assets)
        val riskScore = PortfolioAnalyzer.calculateRiskScore(assets)
        val diversificationScore = PortfolioAnalyzer.calculateDiversificationScore(assets)
        val recommendations = PortfolioAnalyzer.generateRecommendations(assets, profile)

        val allocation = PortfolioAnalyzer.calculateAllocation(assets)
        findViewById<PortfolioChartView>(R.id.portfolioChartView).setAllocation(allocation)

        findViewById<TextView>(R.id.analysisProfileText).text =
            "Profile: ${profile.displayName}"

        findViewById<TextView>(R.id.analysisTotalValueText).text =
            String.format(Locale.US, "Total value: \$%,.2f", totalValue)

        findViewById<TextView>(R.id.analysisRiskScoreText).text =
            String.format(Locale.US, "Risk score: %.1f/10", riskScore)

        findViewById<TextView>(R.id.analysisDiversificationText).text =
            String.format(Locale.US, "Diversification: %.1f/10", diversificationScore)

        findViewById<ProgressBar>(R.id.analysisRiskProgress).progress =
            (riskScore * 10).toInt().coerceIn(0, 100)

        findViewById<ProgressBar>(R.id.analysisDiversificationProgress).progress =
            (diversificationScore * 10).toInt().coerceIn(0, 100)

        findViewById<TextView>(R.id.recommendationsText).text =
            recommendations.joinToString(separator = "\n\n") { recommendation ->
                "• $recommendation"
            }

        renderBrief(PortfolioInsightEngine.generateBrief(assets, profile))
    }

    private fun renderBrief(sections: List<PortfolioInsightEngine.InsightSection>) {
        val container = findViewById<LinearLayout>(R.id.aiBriefContainer)
        container.removeAllViews()

        sections.forEachIndexed { index, section ->
            val title = TextView(this).apply {
                text = section.title
                setTextColor(ContextCompat.getColor(this@AnalysisActivity, R.color.fin_text_primary))
                textSize = 15f
                setTypeface(typeface, android.graphics.Typeface.BOLD)
                val topMargin = if (index == 0) dp(10) else dp(18)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { setMargins(0, topMargin, 0, 0) }
            }

            val body = TextView(this).apply {
                text = section.body
                setTextColor(ContextCompat.getColor(this@AnalysisActivity, R.color.fin_text_secondary))
                textSize = 14f
                gravity = Gravity.START
                setLineSpacing(dp(3).toFloat(), 1f)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { setMargins(0, dp(6), 0, 0) }
            }

            container.addView(title)
            container.addView(body)
        }
    }

    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()
}
