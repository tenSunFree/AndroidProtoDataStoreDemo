package com.home.androidprotodatastoredemo.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import com.home.androidprotodatastoredemo.databinding.ActivityLoginBinding
import com.home.androidprotodatastoredemo.home.HomeActivity
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    companion object {
        const val CORRECT_EMAIL = "test@gmail.com"
    }

    private lateinit var model: LoginModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        observePreferences()
    }

    private fun initView() {
        val binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply {
            viewContinue.setOnClickListener {
                val text = editText.text.toString()
                if (text == CORRECT_EMAIL) {
                    lifecycleScope.launch { model.setIsLogged(true) }
                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        "請輸入正確的信箱 test@gmail.com", Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun observePreferences() {
        model = LoginModel(
            applicationContext
        )
        model.loginPreferencesFlow.asLiveData().observe(this) { bean ->
            if (bean.isLogged) startHomeActivity()
        }
    }

    private fun startHomeActivity() {
        finish()
        startActivity(Intent(this, HomeActivity::class.java))
    }
}