package com.astmirzhan.finpilot.domain

import com.astmirzhan.finpilot.model.Asset
import com.astmirzhan.finpilot.model.AssetCategory
import com.astmirzhan.finpilot.model.RiskProfile
import java.util.Locale

// Local rule-based "AI" brief. No external services.
object PortfolioInsightEngine {

    data class InsightSection(val title: String, val body: String)

    private val defensiveCategories = setOf(
        AssetCategory.CASH,
        AssetCategory.BONDS,
        AssetCategory.ETF,
        AssetCategory.GOLD
    )

    private val riskyCategories = setOf(
        AssetCategory.CRYPTO,
        AssetCategory.COMMODITIES
    )

    fun generateBrief(assets: List<Asset>, profile: RiskProfile): List<InsightSection> {
        if (assets.isEmpty()) {
            return listOf(
                InsightSection(
                    "Executive summary",
                    "Your portfolio is empty. Add a few assets and the assistant will " +
                        "analyse allocation, risk, and diversification, then suggest a target mix " +
                        "for your ${profile.displayName} profile."
                )
            )
        }

        val total = PortfolioAnalyzer.calculateTotalValue(assets)
        val allocation = PortfolioAnalyzer.calculateAllocation(assets)
        val riskScore = PortfolioAnalyzer.calculateRiskScore(assets)
        val diversification = PortfolioAnalyzer.calculateDiversificationScore(assets)

        val largest = allocation.maxByOrNull { it.value }
        val defensivePercent = allocation.filterKeys { it in defensiveCategories }
            .values.sum()
        val riskyPercent = allocation.filterKeys { it in riskyCategories }
            .values.sum()

        return listOf(
            executiveSummary(assets, total, allocation, riskScore, profile),
            riskDiagnosis(riskScore, largest, riskyPercent),
            diversificationGaps(allocation, diversification, defensivePercent),
            profileAlignment(profile, riskScore, allocation),
            actionPlan(profile, riskScore, largest, defensivePercent, allocation),
            targetAllocation(profile, allocation)
        )
    }

    private fun executiveSummary(
        assets: List<Asset>,
        total: Double,
        allocation: Map<AssetCategory, Double>,
        riskScore: Double,
        profile: RiskProfile
    ): InsightSection {
        val categories = allocation.size
        val stance = when {
            riskScore >= 7.0 -> "growth-tilted and aggressive"
            riskScore >= 4.0 -> "moderately balanced"
            else -> "defensive and conservative"
        }
        val body = "You hold ${assets.size} assets across $categories categories worth " +
            "${money(total)}. Overall the portfolio looks $stance with an average risk score " +
            "of ${oneDp(riskScore)}/10. This brief reviews how that fits your " +
            "${profile.displayName} profile."
        return InsightSection("Executive summary", body)
    }

    private fun riskDiagnosis(
        riskScore: Double,
        largest: Map.Entry<AssetCategory, Double>?,
        riskyPercent: Double
    ): InsightSection {
        val builder = StringBuilder()
        val level = when {
            riskScore >= 7.0 -> "high"
            riskScore >= 4.0 -> "moderate"
            else -> "low"
        }
        builder.append("Your weighted risk score is ${oneDp(riskScore)}/10 ($level risk). ")

        if (largest != null && largest.value > 50.0) {
            builder.append(
                "Concentration risk is present: ${oneDp(largest.value)}% sits in " +
                    "${largest.key.displayName}, so a single category drives most of the portfolio. "
            )
        } else {
            builder.append("No single category dominates the portfolio, which limits concentration risk. ")
        }

        if (riskyPercent > 25.0) {
            builder.append(
                "High-volatility assets (Crypto/Commodities) make up ${oneDp(riskyPercent)}%, " +
                    "which can amplify drawdowns."
            )
        } else {
            builder.append("Exposure to high-volatility assets is contained.")
        }
        return InsightSection("Risk diagnosis", builder.toString().trim())
    }

    private fun diversificationGaps(
        allocation: Map<AssetCategory, Double>,
        diversification: Double,
        defensivePercent: Double
    ): InsightSection {
        val builder = StringBuilder()
        builder.append("Diversification score is ${oneDp(diversification)}/10 across ")
        builder.append("${allocation.size} categories. ")

        if (diversification < 5.0) {
            builder.append("This is weak — spreading value across more categories would reduce risk. ")
        }

        if (defensivePercent < 20.0) {
            builder.append(
                "Defensive assets (Cash, Bonds, ETF, Gold) are only ${oneDp(defensivePercent)}%, " +
                    "leaving little protection in a downturn. "
            )
        }

        val missing = defensiveCategories.filter { (allocation[it] ?: 0.0) <= 0.0 }
        if (missing.isNotEmpty()) {
            builder.append(
                "Missing defensive categories: " +
                    missing.joinToString(", ") { it.displayName } + "."
            )
        }
        return InsightSection("Diversification gaps", builder.toString().trim())
    }

    private fun profileAlignment(
        profile: RiskProfile,
        riskScore: Double,
        allocation: Map<AssetCategory, Double>
    ): InsightSection {
        val crypto = allocation[AssetCategory.CRYPTO] ?: 0.0
        val body = when (profile) {
            RiskProfile.CONSERVATIVE -> if (riskScore > 4.5) {
                "Your selected profile is Conservative, but the current risk score of " +
                    "${oneDp(riskScore)}/10 is above the preferred range (under 4.5). The portfolio " +
                    "is taking more risk than your profile suggests."
            } else {
                "Your Conservative profile aligns well with the current low risk score of " +
                    "${oneDp(riskScore)}/10."
            }

            RiskProfile.BALANCED -> if (riskScore in 4.0..7.0) {
                "Your risk score of ${oneDp(riskScore)}/10 sits inside the balanced range (4–7), " +
                    "so the portfolio matches your Balanced profile."
            } else {
                "Your Balanced profile prefers a risk score of 4–7, but the portfolio is at " +
                    "${oneDp(riskScore)}/10, which is outside that band."
            }

            RiskProfile.AGGRESSIVE -> if (riskScore < 5.0) {
                "Your Aggressive profile targets higher growth, but the risk score is only " +
                    "${oneDp(riskScore)}/10 — the portfolio may be too defensive for your goals."
            } else {
                "Your Aggressive profile aligns with the current risk score of ${oneDp(riskScore)}/10."
            }
        }
        val cryptoNote = if (crypto > 20.0 && profile != RiskProfile.AGGRESSIVE) {
            " Crypto is ${oneDp(crypto)}%, which is high for a ${profile.displayName} profile."
        } else {
            ""
        }
        return InsightSection("Profile alignment", body + cryptoNote)
    }

    private fun actionPlan(
        profile: RiskProfile,
        riskScore: Double,
        largest: Map.Entry<AssetCategory, Double>?,
        defensivePercent: Double,
        allocation: Map<AssetCategory, Double>
    ): InsightSection {
        val steps = mutableListOf<String>()

        if (largest != null && largest.value > 50.0) {
            steps.add("Trim ${largest.key.displayName} to reduce single-category exposure.")
        }
        if (defensivePercent < 20.0) {
            steps.add("Add defensive assets such as Cash, Bonds, or ETF before increasing high-risk exposure.")
        }
        if ((allocation[AssetCategory.ETF] ?: 0.0) < 20.0) {
            steps.add("Increase ETF allocation to improve broad diversification.")
        }
        if (profile == RiskProfile.CONSERVATIVE && riskScore > 4.5) {
            steps.add("Lower overall risk by replacing some high-risk holdings with stable assets.")
        }
        if (profile == RiskProfile.AGGRESSIVE && riskScore < 5.0) {
            steps.add("Add growth assets such as Stocks or ETFs to match your aggressive goals.")
        }
        if (steps.isEmpty()) {
            steps.add("Maintain the current mix and rebalance periodically to keep weights on target.")
        }

        val body = steps.mapIndexed { index, step -> "${index + 1}. $step" }
            .joinToString("\n")
        return InsightSection("Action plan", body)
    }

    private fun targetAllocation(
        profile: RiskProfile,
        allocation: Map<AssetCategory, Double>
    ): InsightSection {
        val target = when (profile) {
            RiskProfile.CONSERVATIVE -> linkedMapOf(
                AssetCategory.BONDS to 30,
                AssetCategory.CASH to 20,
                AssetCategory.ETF to 30,
                AssetCategory.STOCKS to 15,
                AssetCategory.CRYPTO to 0
            )
            RiskProfile.BALANCED -> linkedMapOf(
                AssetCategory.ETF to 35,
                AssetCategory.STOCKS to 30,
                AssetCategory.BONDS to 15,
                AssetCategory.CASH to 10,
                AssetCategory.CRYPTO to 5
            )
            RiskProfile.AGGRESSIVE -> linkedMapOf(
                AssetCategory.STOCKS to 40,
                AssetCategory.CRYPTO to 20,
                AssetCategory.ETF to 25,
                AssetCategory.BONDS to 10,
                AssetCategory.CASH to 5
            )
        }

        val body = target.entries.joinToString("\n") { (category, weight) ->
            val current = allocation[category] ?: 0.0
            "${category.displayName}: target $weight%  (now ${oneDp(current)}%)"
        }
        return InsightSection("Suggested target allocation", body)
    }

    private fun money(value: Double): String =
        String.format(Locale.US, "\$%,.2f", value)

    private fun oneDp(value: Double): String =
        String.format(Locale.US, "%.1f", value)
}
