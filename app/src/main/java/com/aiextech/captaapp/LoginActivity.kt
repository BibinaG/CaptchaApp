package com.aiextech.captaapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.aiextech.captaapp.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth


class LoginActivity : AppCompatActivity(){
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
            startActivity(Intent(this, SignUp::class.java))
        }
        binding.ivCaptcha.setOnClickListener {
//            showCaptchaDialog()
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



}
