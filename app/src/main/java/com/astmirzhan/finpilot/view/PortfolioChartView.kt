package com.astmirzhan.finpilot.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.astmirzhan.finpilot.model.AssetCategory

class PortfolioChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var allocation: Map<AssetCategory, Double> = emptyMap()

    private val barColors = intArrayOf(
        Color.parseColor("#34D399"),
        Color.parseColor("#60A5FA"),
        Color.parseColor("#F59E0B"),
        Color.parseColor("#A78BFA"),
        Color.parseColor("#FB7185"),
        Color.parseColor("#2DD4BF"),
        Color.parseColor("#F472B6"),
        Color.parseColor("#38BDF8"),
        Color.parseColor("#FBBF24")
    )

    private val barPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val trackPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#2A3545")
    }

    private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#F4F7FB")
        textSize = 36f
    }

    private val percentPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#9AA7B8")
        textSize = 36f
        textAlign = Paint.Align.RIGHT
    }

    private val emptyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#9AA7B8")
        textSize = 40f
        textAlign = Paint.Align.CENTER
    }

    private val barRect = RectF()

    fun setAllocation(allocation: Map<AssetCategory, Double>) {
        this.allocation = allocation.toList()
            .sortedByDescending { it.second }
            .toMap()
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (allocation.isEmpty()) {
            canvas.drawText(
                "No allocation data",
                width / 2f,
                height / 2f,
                emptyPaint
            )
            return
        }

        val paddingLeft = 24f
        val paddingRight = 24f
        val paddingTop = 24f
        val rowHeight = 70f
        val barHeight = 28f
        val barLeft = paddingLeft
        val barRight = width - paddingRight

        var rowTop = paddingTop

        allocation.entries.forEachIndexed { index, entry ->
            val category = entry.key
            val percent = entry.value
            val fraction = (percent / 100.0).coerceIn(0.0, 1.0).toFloat()

            canvas.drawText(category.displayName, barLeft, rowTop + 28f, labelPaint)

            canvas.drawText(
                String.format("%.1f%%", percent),
                barRight,
                rowTop + 28f,
                percentPaint
            )

            val barTop = rowTop + 40f
            val barBottom = barTop + barHeight

            barRect.set(barLeft, barTop, barRight, barBottom)
            canvas.drawRoundRect(barRect, 12f, 12f, trackPaint)

            val filledRight = barLeft + (barRight - barLeft) * fraction
            barPaint.color = barColors[index % barColors.size]
            barRect.set(barLeft, barTop, filledRight, barBottom)
            canvas.drawRoundRect(barRect, 12f, 12f, barPaint)

            rowTop += rowHeight
        }
    }
}
