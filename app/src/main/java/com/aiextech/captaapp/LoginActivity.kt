package com.aiextech.captaapp

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.aiextech.captaapp.databinding.ActivityLoginBinding
import com.aiextech.captaapp.utils.CaptchaUI
import com.google.firebase.auth.FirebaseAuth
import com.jacknkiarie.captchaui.CaptchaLayout


class LoginActivity : AppCompatActivity(), CaptchaLayout.OnButtonClickedListener {
    private lateinit var binding: ActivityLoginBinding;
    private var fAuth = FirebaseAuth.getInstance()
    private lateinit var Email: String
    private lateinit var Password: String

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, LoginActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Login"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.elevation = 0f
        initView()
    }

    private fun initView() {
        binding.btnLogin.setOnClickListener {
            Email = binding.tilEmail.text.toString()
            Password = binding.tilPassword.text.toString()
            if (validateFields()) {
                firebaseAuth()
            }
        }
        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
        binding.ivCaptcha.setOnClickListener {
//            showCaptchaDialog()
            capFunctions()
        }
    }

    private fun validateFields(): Boolean {
        return when {
            binding.tilEmail.text.toString().isNullOrEmpty() -> {
                clearError()
                binding.tilEmail.requestFocus()
                binding.tilEmail.error = "Email Field cannot be empty"
                false
            }
            binding.tilPassword.text.isNullOrEmpty() -> {
                clearError()
                binding.tilPassword.requestFocus()
                binding.tilPassword.error = "Password cannot be empty"
                clearError()
                false
            }
            binding.tilPassword.length() < 6 -> {
                binding.tilPassword.error = "Password must be more than  6 char long"
                false
            }
            else -> {
                return true
            }
        }
    }

    private fun firebaseAuth() {
        fAuth.signInWithEmailAndPassword(Email, Password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                } else {
                    Toast.makeText(this, "Error !" + task.exception, Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun clearError() {
        binding.apply {
            tieEmail.error = null
            tiePassword.error = null
        }
    }

    private fun showCaptchaDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_captcha)
//        val body = dialog.findViewById(R.id.captcha_header) as TextView
//        body.text = title
        CaptchaUI.Builder(this)
//            .setCaptchaTextColor(resources.getColor(R.color.purple_200))
//            .setCaptchaLineColor(resources.getColor(R.color.purple_500))
            .setCaptchaCodeLength(4)
            .setCaptchaPositiveText("OK")
            .setCaptchaPositiveTextColor(Color.WHITE)
            .setCaptchaNegativeText("NOPE")
            .setCaptchaButtonListener(this)
            .build()
        val yesBtn = dialog.findViewById(R.id.captcha_positive_button) as Button
        val noBtn = dialog.findViewById(R.id.captcha_negative_button) as TextView
        yesBtn.setOnClickListener {
            dialog.dismiss()
        }
        noBtn.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun capFunctions() {
        CaptchaUI.Builder(this)
            .setCaptchaTextColor(resources.getColor(R.color.purple_200))
            .setCaptchaLineColor(resources.getColor(R.color.purple_500))
            .setCaptchaCodeLength(4)
            .setCaptchaPositiveText("OK")
            .setCaptchaPositiveTextColor(Color.WHITE)
            .setCaptchaNegativeText("NOPE")
            .setCaptchaButtonListener(this)
            .build()
    }

    override fun onNegativeButtonClicked() {
        TODO("Not yet implemented")
    }

    override fun onVerificationCodeVerified() {
        Toast.makeText(this, "Everything is awesome", Toast.LENGTH_SHORT).show()

    }
}
