package com.astmirzhan.finpilot

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.astmirzhan.finpilot.data.PortfolioRepository
import com.astmirzhan.finpilot.domain.PortfolioAnalyzer
import com.astmirzhan.finpilot.model.Asset
import com.astmirzhan.finpilot.ui.AssetAdapter
import com.astmirzhan.finpilot.ui.BottomNavHelper
import java.util.Locale

class PortfolioActivity : AppCompatActivity() {

    private lateinit var adapter: AssetAdapter
    private lateinit var emptyPortfolioText: TextView
    private lateinit var portfolioSummaryText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_portfolio)

        emptyPortfolioText = findViewById(R.id.emptyPortfolioText)
        portfolioSummaryText = findViewById(R.id.portfolioSummaryText)

        setupRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        BottomNavHelper.setup(this, BottomNavHelper.Tab.PORTFOLIO)
        updatePortfolio()
    }

    private fun setupRecyclerView() {
        adapter = AssetAdapter(emptyList()) { asset ->
            showAssetDetails(asset)
        }

        findViewById<RecyclerView>(R.id.assetsRecyclerView).apply {
            layoutManager = LinearLayoutManager(this@PortfolioActivity)
            adapter = this@PortfolioActivity.adapter
        }
    }

    private fun updatePortfolio() {
        val assets = PortfolioRepository.getAssets(this)
        val totalValue = PortfolioAnalyzer.calculateTotalValue(assets)

        adapter.updateAssets(assets)

        portfolioSummaryText.text = String.format(
            Locale.US,
            "%d assets • \$%,.2f",
            assets.size,
            totalValue
        )

        emptyPortfolioText.visibility = if (assets.isEmpty()) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    private fun showAssetDetails(asset: Asset) {
        val message = """
            Category: ${asset.category.displayName}
            Amount: ${String.format(Locale.US, "\$%,.2f", asset.amount)}
            Risk level: ${asset.riskLevel}/10
        """.trimIndent()

        AlertDialog.Builder(this)
            .setTitle(asset.name)
            .setMessage(message)
            .setPositiveButton("OK", null)
            .setNegativeButton("Delete") { _, _ ->
                PortfolioRepository.deleteAsset(this, asset.id)
                updatePortfolio()
            }
            .show()
    }
}