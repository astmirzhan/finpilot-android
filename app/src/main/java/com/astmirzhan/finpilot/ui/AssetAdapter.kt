package com.astmirzhan.finpilot.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.astmirzhan.finpilot.R
import com.astmirzhan.finpilot.model.Asset
import java.util.Locale

class AssetAdapter(
    private var assets: List<Asset>,
    private val onAssetClick: (Asset) -> Unit
) : RecyclerView.Adapter<AssetAdapter.AssetViewHolder>() {

    class AssetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val assetNameText: TextView = itemView.findViewById(R.id.assetNameText)
        val assetCategoryText: TextView = itemView.findViewById(R.id.assetCategoryText)
        val assetAmountText: TextView = itemView.findViewById(R.id.assetAmountText)
        val assetRiskText: TextView = itemView.findViewById(R.id.assetRiskText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssetViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_asset, parent, false)

        return AssetViewHolder(view)
    }

    override fun onBindViewHolder(holder: AssetViewHolder, position: Int) {
        val asset = assets[position]

        holder.assetNameText.text = asset.name
        holder.assetCategoryText.text = asset.category.displayName
        holder.assetAmountText.text = String.format(Locale.US, "\$%,.2f", asset.amount)
        holder.assetRiskText.text = "Risk: ${asset.riskLevel}/10"


        holder.itemView.setOnClickListener {
            onAssetClick(asset)
        }
    }

    override fun getItemCount(): Int = assets.size

    fun updateAssets(newAssets: List<Asset>) {
        assets = newAssets
        notifyDataSetChanged()
    }
}