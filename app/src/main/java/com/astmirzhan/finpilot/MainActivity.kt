package com.astmirzhan.finpilot

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.astmirzhan.finpilot.data.AuthRepository
import com.astmirzhan.finpilot.data.PortfolioRepository
import com.astmirzhan.finpilot.domain.PortfolioAnalyzer
import com.astmirzhan.finpilot.domain.PortfolioInsightEngine
import com.astmirzhan.finpilot.model.Asset
import com.astmirzhan.finpilot.ui.BottomNavHelper
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private val addAssetLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val asset = getAssetFromResult(result.data)

                if (asset != null) {
                    PortfolioRepository.addAsset(this, asset)
                    updateDashboard()
                }
            }
        }

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

        setupClickListeners()
    }

    override fun onResume() {
        super.onResume()
        BottomNavHelper.setup(this, BottomNavHelper.Tab.HOME)
        updateDashboard()
    }

    private fun setupClickListeners() {
        findViewById<View>(R.id.addAssetButton).setOnClickListener {
            addAssetLauncher.launch(Intent(this, AddAssetActivity::class.java))
        }

        findViewById<View>(R.id.logoutButton).setOnClickListener {
            AuthRepository.logout(this)
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun updateDashboard() {
        val assets = PortfolioRepository.getAssets(this)
        val profile = PortfolioRepository.getRiskProfile(this)

        val totalValue = PortfolioAnalyzer.calculateTotalValue(assets)
        val riskScore = PortfolioAnalyzer.calculateRiskScore(assets)
        val diversificationScore =
            PortfolioAnalyzer.calculateDiversificationScore(assets)

        findViewById<TextView>(R.id.totalValueText).text =
            String.format(Locale.US, "\$%,.2f", totalValue)

        findViewById<TextView>(R.id.riskScoreText).text =
            String.format(Locale.US, "Risk score: %.1f/10", riskScore)

        findViewById<TextView>(R.id.diversificationScoreText).text =
            String.format(Locale.US, "Diversification: %.1f/10", diversificationScore)

        findViewById<TextView>(R.id.activeProfileText).text =
            "Profile: ${profile.displayName}"

        findViewById<ProgressBar>(R.id.riskProgress).progress =
            (riskScore * 10).toInt().coerceIn(0, 100)

        findViewById<ProgressBar>(R.id.diversificationProgress).progress =
            (diversificationScore * 10).toInt().coerceIn(0, 100)

        val brief = PortfolioInsightEngine.generateBrief(assets, profile)
        findViewById<TextView>(R.id.mainInsightText).text =
            brief.firstOrNull()?.body ?: "Add assets to get your first AI brief."
    }

    @Suppress("DEPRECATION")
    private fun getAssetFromResult(data: Intent?): Asset? {
        return data?.getSerializableExtra(AddAssetActivity.EXTRA_ASSET) as? Asset
    }
}
