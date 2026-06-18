package com.astmirzhan.finpilot.domain

import com.astmirzhan.finpilot.model.Asset
import com.astmirzhan.finpilot.model.AssetCategory

object PortfolioAnalyzer {

    fun calculateTotalValue(assets: List<Asset>): Double {
        return assets.sumOf { it.amount }
    }

    fun calculateAllocation(
        assets: List<Asset>
    ): Map<AssetCategory, Double> {
        val totalValue = calculateTotalValue(assets)

        if (totalValue <= 0.0) {
            return emptyMap()
        }

        return assets
            .groupBy { it.category }
            .mapValues { (_, categoryAssets) ->
                categoryAssets.sumOf { it.amount } / totalValue * 100.0
            }
    }

    fun calculateRiskScore(assets: List<Asset>): Double {
        val totalValue = calculateTotalValue(assets)

        if (totalValue <= 0.0) {
            return 0.0
        }

        return assets.sumOf { asset ->
            asset.riskLevel * (asset.amount / totalValue)
        }
    }

    fun calculateDiversificationScore(assets: List<Asset>): Double {
        val allocation = calculateAllocation(assets)

        if (allocation.size <= 1) {
            return 0.0
        }

        val concentration = allocation.values.sumOf { percentage ->
            val share = percentage / 100.0
            share * share
        }

        val categoryCount = AssetCategory.entries.size
        val maximumDiversity = 1.0 - 1.0 / categoryCount

        return ((1.0 - concentration) / maximumDiversity * 10.0)
            .coerceIn(0.0, 10.0)
    }
}
