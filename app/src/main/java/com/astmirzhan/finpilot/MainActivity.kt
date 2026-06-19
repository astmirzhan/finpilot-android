package com.astmirzhan.finpilot

import android.os.Bundle
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.astmirzhan.finpilot.data.PortfolioRepository
import com.astmirzhan.finpilot.domain.PortfolioAnalyzer
import java.util.Locale

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )
            insets
        }
    }

    override fun onResume() {
        super.onResume()
        updateDashboard()
    }

    private fun updateDashboard() {
        val assets = PortfolioRepository.getAssets(this)
        val profile = PortfolioRepository.getRiskProfile(this)

        val totalValue = PortfolioAnalyzer.calculateTotalValue(assets)
        val riskScore = PortfolioAnalyzer.calculateRiskScore(assets)
        val diversificationScore =
            PortfolioAnalyzer.calculateDiversificationScore(assets)

        findViewById<TextView>(R.id.totalValueText).text =
            String.format(Locale.US, "$%,.2f", totalValue)

        findViewById<TextView>(R.id.riskScoreText).text =
            String.format(Locale.US, "Risk score: %.1f/10", riskScore)

        findViewById<TextView>(R.id.diversificationScoreText).text =
            String.format(
                Locale.US,
                "Diversification: %.1f/10",
                diversificationScore
            )

        findViewById<TextView>(R.id.activeProfileText).text =
            "Profile: ${profile.displayName}"

        findViewById<ProgressBar>(R.id.riskProgress).progress =
            (riskScore * 10).toInt()

        findViewById<ProgressBar>(R.id.diversificationProgress).progress =
            (diversificationScore * 10).toInt()
    }
}