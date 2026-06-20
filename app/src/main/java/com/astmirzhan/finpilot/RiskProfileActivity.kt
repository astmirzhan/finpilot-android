package com.astmirzhan.finpilot

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import com.astmirzhan.finpilot.model.RiskProfile

class RiskProfileActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_SELECTED_PROFILE = "extra_selected_profile"
        const val EXTRA_CURRENT_PROFILE = "extra_current_profile"
    }

    private lateinit var riskProfileRadioGroup: RadioGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_risk_profile)

        riskProfileRadioGroup = findViewById(R.id.riskProfileRadioGroup)

        val currentProfileName = intent.getStringExtra(EXTRA_CURRENT_PROFILE)
        val currentProfile = runCatching {
            RiskProfile.valueOf(currentProfileName ?: RiskProfile.BALANCED.name)
        }.getOrDefault(RiskProfile.BALANCED)

        selectCurrentProfile(currentProfile)

        findViewById<Button>(R.id.saveRiskProfileButton).setOnClickListener {
            val selectedProfile = getSelectedProfile()

            // Возвращаем выбранный профиль обратно в MainActivity
            val resultIntent = Intent().apply {
                putExtra(EXTRA_SELECTED_PROFILE, selectedProfile.name)
            }

            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }

        findViewById<Button>(R.id.cancelRiskProfileButton).setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }

    private fun selectCurrentProfile(profile: RiskProfile) {
        val checkedId = when (profile) {
            RiskProfile.CONSERVATIVE -> R.id.conservativeRadioButton
            RiskProfile.BALANCED -> R.id.balancedRadioButton
            RiskProfile.AGGRESSIVE -> R.id.aggressiveRadioButton
        }

        riskProfileRadioGroup.check(checkedId)
    }

    private fun getSelectedProfile(): RiskProfile {
        return when (riskProfileRadioGroup.checkedRadioButtonId) {
            R.id.conservativeRadioButton -> RiskProfile.CONSERVATIVE
            R.id.aggressiveRadioButton -> RiskProfile.AGGRESSIVE
            else -> RiskProfile.BALANCED
        }
    }
}