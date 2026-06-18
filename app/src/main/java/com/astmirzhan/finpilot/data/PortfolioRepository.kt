package com.astmirzhan.finpilot.data

import android.content.Context
import com.astmirzhan.finpilot.model.Asset
import com.astmirzhan.finpilot.model.AssetCategory
import com.astmirzhan.finpilot.model.RiskProfile
import org.json.JSONArray
import org.json.JSONObject

object PortfolioRepository {

    private const val PREFS_NAME = "finpilot_preferences"
    private const val KEY_ASSETS = "assets"
    private const val KEY_RISK_PROFILE = "risk_profile"

    fun getAssets(context: Context): List<Asset> {
        val preferences = context.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )

        val json = preferences.getString(KEY_ASSETS, null)
            ?: return emptyList()

        return try {
            val jsonArray = JSONArray(json)

            List(jsonArray.length()) { index ->
                val item = jsonArray.getJSONObject(index)

                Asset(
                    id = item.getLong("id"),
                    name = item.getString("name"),
                    category = AssetCategory.valueOf(
                        item.getString("category")
                    ),
                    amount = item.getDouble("amount"),
                    riskLevel = item.getInt("riskLevel")
                )
            }
        } catch (exception: Exception) {
            emptyList()
        }
    }

    fun saveAssets(context: Context, assets: List<Asset>) {
        val jsonArray = JSONArray()

        assets.forEach { asset ->
            val item = JSONObject().apply {
                put("id", asset.id)
                put("name", asset.name)
                put("category", asset.category.name)
                put("amount", asset.amount)
                put("riskLevel", asset.riskLevel)
            }

            jsonArray.put(item)
        }

        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_ASSETS, jsonArray.toString())
            .apply()
    }

    fun addAsset(context: Context, asset: Asset) {
        val assets = getAssets(context).toMutableList()
        assets.add(asset)
        saveAssets(context, assets)
    }

    fun deleteAsset(context: Context, assetId: Long) {
        val assets = getAssets(context).filterNot { it.id == assetId }
        saveAssets(context, assets)
    }

    fun saveRiskProfile(context: Context, profile: RiskProfile) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_RISK_PROFILE, profile.name)
            .apply()
    }

    fun getRiskProfile(context: Context): RiskProfile {
        val storedValue = context
            .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_RISK_PROFILE, RiskProfile.BALANCED.name)

        return storedValue
            ?.let { runCatching { RiskProfile.valueOf(it) }.getOrNull() }
            ?: RiskProfile.BALANCED
    }
}