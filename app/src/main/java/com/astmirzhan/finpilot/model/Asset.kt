package com.astmirzhan.finpilot.model

import java.io.Serializable

data class Asset(
    val id: Long,
    val name: String,
    val category: AssetCategory,
    val amount: Double,
    val riskLevel: Int
) : Serializable