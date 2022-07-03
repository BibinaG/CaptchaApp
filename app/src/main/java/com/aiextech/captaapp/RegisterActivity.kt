package com.aiextech.captaapp

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.aiextech.captaapp.databinding.ActivityRegisterBinding
import com.aiextech.captaapp.extensions.isEmail
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private var fAuth = FirebaseAuth.getInstance()
    private lateinit var Email: String
    private lateinit var Password: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.elevation = 0f
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(Color.parseColor("#FFFFFFFF")))

        initViews();
        checkUser()
    }

    private fun initViews() {
        binding.btnRegister.setOnClickListener {
            Email = binding.tilEmail.text.toString();
            Password = binding.tilPassword.text.toString()
            if (validateFields()) {
                firebaseAuth()
            }
        }
    }

    private fun validateFields(): Boolean {
        return when {
            binding.tilFullName.text.toString().isEmail() -> {
                clearError()
                binding.tilEmail.requestFocus()
                binding.tilEmail.error = "FirstName cannot be empty"
                false
            }
            binding.tilEmail.text.isNullOrEmpty() -> {
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


    private fun checkUser() {
        if (fAuth.currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        // firebaseAuth()
    }

    private fun firebaseAuth() {
        fAuth.createUserWithEmailAndPassword(Email, Password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "USer created", Toast.LENGTH_SHORT).show()
                // fAuth.updateCurrentUser(FirebaseUser(fi))
                startActivity(Intent(this, MainActivity::class.java))

            } else {
                Toast.makeText(this, "Error !" + task.exception, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun clearError() {
        binding.apply {
            tilFullName.error = null
            tieEmail.error = null
            tiePassword.error = null
        }
    }
}