package com.astmirzhan.finpilot

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.astmirzhan.finpilot.data.AuthRepository

class LoginActivity : AppCompatActivity() {

    private var registerMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val usernameInput = findViewById<EditText>(R.id.usernameInput)
        val passwordInput = findViewById<EditText>(R.id.passwordInput)
        val primaryButton = findViewById<Button>(R.id.primaryAuthButton)
        val toggleButton = findViewById<Button>(R.id.toggleModeButton)

        updateMode()

        toggleButton.setOnClickListener {
            registerMode = !registerMode
            updateMode()
        }

        primaryButton.setOnClickListener {
            val username = usernameInput.text.toString().trim()
            val password = passwordInput.text.toString()

            if (username.isEmpty()) {
                Toast.makeText(this, "Enter a username", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 4) {
                Toast.makeText(this, "Password must be at least 4 characters", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            if (registerMode) {
                handleRegister(username, password)
            } else {
                handleLogin(username, password)
            }
        }
    }

    private fun handleRegister(username: String, password: String) {
        if (AuthRepository.isRegistered(this, username)) {
            Toast.makeText(this, "Username already exists", Toast.LENGTH_SHORT).show()
            return
        }
        AuthRepository.register(this, username, password)
        Toast.makeText(this, "Account created", Toast.LENGTH_SHORT).show()
        openMain()
    }

    private fun handleLogin(username: String, password: String) {
        if (AuthRepository.login(this, username, password)) {
            Toast.makeText(this, "Welcome back", Toast.LENGTH_SHORT).show()
            openMain()
        } else {
            Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateMode() {
        val title = findViewById<TextView>(R.id.loginTitle)
        val subtitle = findViewById<TextView>(R.id.loginSubtitle)
        val primaryButton = findViewById<Button>(R.id.primaryAuthButton)
        val toggleButton = findViewById<Button>(R.id.toggleModeButton)

        if (registerMode) {
            title.text = "Create account"
            subtitle.text = "Set up your local FinPilot profile"
            primaryButton.text = "Register"
            toggleButton.text = "I already have an account"
        } else {
            title.text = "Welcome back"
            subtitle.text = "Sign in to your portfolio"
            primaryButton.text = "Login"
            toggleButton.text = "Create an account"
        }
    }

    private fun openMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
