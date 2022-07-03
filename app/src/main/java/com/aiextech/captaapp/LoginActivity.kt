package com.aiextech.captaapp

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.aiextech.captaapp.databinding.ActivityLoginBinding
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.safetynet.SafetyNet
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import java.util.concurrent.Executor


class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding;
    private var fAuth = FirebaseAuth.getInstance()
    private lateinit var Email: String
    private lateinit var Password: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Login"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.elevation = 0f
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(Color.parseColor("#FFFFFFFF")))
        initView()
    }

    private fun initView() {
        if (binding.cbRememberMe.isChecked) {
            showCaptchaDialog()
        }
        binding.btnLogin.setOnClickListener {
            Email = binding.tilEmail.text.toString()
            Password = binding.tilPassword.text.toString()
            if (validateFields()) {
                firebaseAuth()
            }
        }
        binding.btnLogin.isEnabled = false
        Toast.makeText(this, "Verification is required !", Toast.LENGTH_SHORT).show()

        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
        binding.ivCaptcha.setOnClickListener {
            showCaptchaDialog()
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
        val body = dialog.findViewById(R.id.captcha_header) as TextView
        body.text = title
        val yesBtn = dialog.findViewById(R.id.captcha_positive_button) as Button
        val noBtn = dialog.findViewById(R.id.captcha_negative_button) as TextView
        yesBtn.setOnClickListener {
            dialog.dismiss()
        }
        noBtn.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }


    fun onClick(view: View) {
        if (binding.cbRememberMe.isChecked) {
            SafetyNet.getClient(this)
                .verifyWithRecaptcha("6LeeorkgAAAAAFny-483PjKVxUBkjZsLjxtEq0KP")
                .addOnSuccessListener(this as Executor, OnSuccessListener { response ->
                    // Indicates communication with reCAPTCHA service was
                    // successful.
                    val userResponseToken = response.tokenResult
                    if (response.tokenResult?.isNotEmpty() == true) {
                        // Validate the user response token using the
                        // reCAPTCHA siteverify API.
                    }
                })
                .addOnFailureListener(this as Executor, OnFailureListener { e ->
                    if (e is ApiException) {
                        // An error occurred when communicating with the
                        // reCAPTCHA service. Refer to the status code to
                        // handle the error appropriately.
                        Log.d("d", "Error: ${CommonStatusCodes.getStatusCodeString(e.statusCode)}")
                    } else {
                        // A different, unknown type of error occurred.
                        Log.d("b", "Error: ${e.message}")
                    }
                })

        }
    }
}
