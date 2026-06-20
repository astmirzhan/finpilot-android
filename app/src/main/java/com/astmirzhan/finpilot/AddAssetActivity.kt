package com.astmirzhan.finpilot

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.astmirzhan.finpilot.model.Asset
import com.astmirzhan.finpilot.model.AssetCategory

class AddAssetActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_ASSET = "extra_asset"
    }

    private var selectedRiskLevel = 5

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_asset)

        val assetNameInput = findViewById<EditText>(R.id.assetNameInput)
        val amountInput = findViewById<EditText>(R.id.amountInput)
        val categorySpinner = findViewById<Spinner>(R.id.categorySpinner)
        val riskLevelText = findViewById<TextView>(R.id.riskLevelText)
        val riskSeekBar = findViewById<SeekBar>(R.id.riskSeekBar)
        val longTermCheckBox = findViewById<CheckBox>(R.id.longTermCheckBox)
        val saveButton = findViewById<Button>(R.id.saveAssetButton)
        val cancelButton = findViewById<Button>(R.id.cancelButton)

        val categories = AssetCategory.entries.map { it.displayName }

        categorySpinner.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            categories
        )

        riskSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                selectedRiskLevel = progress + 1
                riskLevelText.text = "Risk level: $selectedRiskLevel/10"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        saveButton.setOnClickListener {
            val name = assetNameInput.text.toString().trim()
            val amountText = amountInput.text.toString().trim()

            if (name.isEmpty()) {
                Toast.makeText(this, "Enter asset name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val amount = amountText.toDoubleOrNull()

            if (amount == null || amount <= 0.0) {
                Toast.makeText(this, "Enter valid amount", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val selectedCategory = AssetCategory.entries[categorySpinner.selectedItemPosition]

            val asset = Asset(
                id = System.currentTimeMillis(),
                name = name,
                category = selectedCategory,
                amount = amount,
                riskLevel = selectedRiskLevel
            )


            val resultIntent = Intent().apply {
                putExtra(EXTRA_ASSET, asset)
                putExtra("is_long_term", longTermCheckBox.isChecked)
            }

            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }

        cancelButton.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }
}