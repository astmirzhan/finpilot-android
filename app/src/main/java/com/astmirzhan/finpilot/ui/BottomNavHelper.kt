package com.astmirzhan.finpilot.ui

import android.content.Intent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.astmirzhan.finpilot.AnalysisActivity
import com.astmirzhan.finpilot.MainActivity
import com.astmirzhan.finpilot.PortfolioActivity
import com.astmirzhan.finpilot.R
import com.astmirzhan.finpilot.RiskProfileActivity

object BottomNavHelper {

    enum class Tab { HOME, PORTFOLIO, RISK, ANALYSIS }

    fun setup(activity: AppCompatActivity, current: Tab) {
        bindTab(
            activity, current, Tab.HOME,
            R.id.navHome, R.id.navHomeIcon, R.id.navHomeLabel,
            MainActivity::class.java
        )
        bindTab(
            activity, current, Tab.PORTFOLIO,
            R.id.navPortfolio, R.id.navPortfolioIcon, R.id.navPortfolioLabel,
            PortfolioActivity::class.java
        )
        bindTab(
            activity, current, Tab.RISK,
            R.id.navRisk, R.id.navRiskIcon, R.id.navRiskLabel,
            RiskProfileActivity::class.java
        )
        bindTab(
            activity, current, Tab.ANALYSIS,
            R.id.navAnalysis, R.id.navAnalysisIcon, R.id.navAnalysisLabel,
            AnalysisActivity::class.java
        )
    }

    private fun bindTab(
        activity: AppCompatActivity,
        current: Tab,
        tab: Tab,
        containerId: Int,
        iconId: Int,
        labelId: Int,
        target: Class<*>
    ) {
        val container = activity.findViewById<View>(containerId) ?: return
        val icon = activity.findViewById<ImageView>(iconId)
        val label = activity.findViewById<TextView>(labelId)

        val selected = tab == current
        val tint = if (selected) R.color.fin_lime_dark else R.color.fin_text_secondary
        val color = ContextCompat.getColor(activity, tint)

        container.setBackgroundResource(
            if (selected) R.drawable.bg_nav_selected else android.R.color.transparent
        )
        icon.setColorFilter(color)
        label.setTextColor(color)

        container.setOnClickListener {
            if (selected) return@setOnClickListener
            val intent = Intent(activity, target)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            activity.startActivity(intent)
        }
    }
}
