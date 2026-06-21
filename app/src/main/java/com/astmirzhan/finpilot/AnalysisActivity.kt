package com.astmirzhan.finpilot

import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.astmirzhan.finpilot.data.PortfolioRepository
import com.astmirzhan.finpilot.domain.PortfolioAnalyzer
import com.astmirzhan.finpilot.view.PortfolioChartView
import java.util.Locale

class AnalysisActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_analysis)

        updateAnalysis()

        findViewById<Button>(R.id.backFromAnalysisButton).setOnClickListener {
            finish()
        }
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
    }
}
