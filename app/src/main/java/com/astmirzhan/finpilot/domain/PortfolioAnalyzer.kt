package com.astmirzhan.finpilot.domain

import com.astmirzhan.finpilot.model.Asset
import com.astmirzhan.finpilot.model.AssetCategory
import com.astmirzhan.finpilot.model.RiskProfile

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
        }.coerceIn(0.0, 10.0)
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

    fun generateRecommendations(
        assets: List<Asset>,
        riskProfile: RiskProfile
    ): List<String> {
        if (assets.isEmpty()) {
            return listOf("Add assets to start portfolio analysis.")
        }

        val recommendations = mutableListOf<String>()

        val allocation = calculateAllocation(assets)
        val riskScore = calculateRiskScore(assets)
        val diversificationScore = calculateDiversificationScore(assets)

        val cryptoPercent = allocation[AssetCategory.CRYPTO] ?: 0.0
        val cashPercent = allocation[AssetCategory.CASH] ?: 0.0
        val etfPercent = allocation[AssetCategory.ETF] ?: 0.0
        val bondsPercent = allocation[AssetCategory.BONDS] ?: 0.0

        val largestCategory = allocation.maxByOrNull { it.value }

        if (largestCategory != null && largestCategory.value > 60.0) {
            recommendations.add(
                "Your portfolio is concentrated in ${largestCategory.key.displayName}."
            )
        }

        if (cryptoPercent > 20.0 && riskProfile != RiskProfile.AGGRESSIVE) {
            recommendations.add("Crypto allocation is high for your selected risk profile.")
        }

        if (cashPercent < 5.0) {
            recommendations.add("Cash reserve is low for short-term safety.")
        }

        if (etfPercent < 20.0) {
            recommendations.add("ETF allocation is low, diversification could be improved.")
        }

        if (diversificationScore < 5.0) {
            recommendations.add("Portfolio diversification is weak.")
        }

        when (riskProfile) {
            RiskProfile.CONSERVATIVE -> {
                if (riskScore > 5.0) {
                    recommendations.add("Your portfolio looks too risky for a conservative profile.")
                }

                if (bondsPercent < 20.0 && cashPercent < 10.0) {
                    recommendations.add("A conservative profile usually needs more defensive assets.")
                }
            }

            RiskProfile.BALANCED -> {
                if (riskScore in 4.0..7.0) {
                    recommendations.add("Your portfolio matches a balanced profile.")
                } else {
                    recommendations.add("Your portfolio does not fully match a balanced profile.")
                }
            }

            RiskProfile.AGGRESSIVE -> {
                if (riskScore < 5.0) {
                    recommendations.add("Your portfolio may be too defensive for an aggressive profile.")
                } else {
                    recommendations.add("Your portfolio matches an aggressive profile.")
                }
            }
        }

        if (recommendations.isEmpty()) {
            recommendations.add("Portfolio health looks stable based on the current rules.")
        }

        return recommendations.distinct()
    }
}