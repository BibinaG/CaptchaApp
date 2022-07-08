package com.aiextech.captaapp

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.aiextech.captaapp.databinding.ActivitySignUpBinding
import com.aiextech.captaapp.extensions.isEmail
import com.aiextech.captaapp.utils.CaptchaUI
import com.aiextech.captaapp.utils.PasswordStrengthCalculator
import com.aiextech.captaapp.utils.StrengthLevel
import com.google.firebase.auth.FirebaseAuth
import com.jacknkiarie.captchaui.CaptchaLayout

class SignUp : AppCompatActivity(), CaptchaLayout.OnButtonClickedListener {
    private lateinit var binding: ActivitySignUpBinding
    private var fAuth = FirebaseAuth.getInstance()
    private lateinit var Email: String
    private lateinit var Password: String
    private lateinit var cPassword: String

    private var color: Int = R.color.weak


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.elevation = 0f
        supportActionBar?.title=""
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(Color.parseColor("#FFFFFFFF")))

        val passwordStrengthCalculator = PasswordStrengthCalculator()
        binding.passwordInput1.addTextChangedListener(passwordStrengthCalculator)

        passwordStrengthCalculator.strengthLevel.observe(this, Observer { strengthLevel ->
            displayStrengthLevel(strengthLevel)
        })
        passwordStrengthCalculator.strengthColor.observe(this, Observer { strengthColor ->
            color = strengthColor
        })
        passwordStrengthCalculator.lowerCase.observe(this, Observer { value ->
            displayPasswordSuggestions(value, binding.lowerCaseImg, binding.lowerCaseTxt)
        })
        passwordStrengthCalculator.upperCase.observe(this, Observer { value ->
            displayPasswordSuggestions(value, binding.upperCaseImg, binding.upperCaseTxt)
        })
        passwordStrengthCalculator.digit.observe(this, Observer { value ->
            displayPasswordSuggestions(value, binding.digitImg, binding.digitTxt)
        })
        passwordStrengthCalculator.specialChar.observe(this, Observer { value ->
            displayPasswordSuggestions(value, binding.specialCharImg, binding.specialCharTxt)
        })

        initViews()


    }


    private fun displayPasswordSuggestions(
        value: Int,
        imageView: ImageView,
        textView: TextView
    ) {
        if (value == 1) {
            imageView.setColorFilter(ContextCompat.getColor(this, R.color.bulletproof))
            textView.setTextColor(ContextCompat.getColor(this, R.color.bulletproof))
        } else {
            imageView.setColorFilter(ContextCompat.getColor(this, R.color.darkGray))
            textView.setTextColor(ContextCompat.getColor(this, R.color.darkGray))
        }
    }

    private fun displayStrengthLevel(strengthLevel: StrengthLevel) {
        binding.button.isEnabled = strengthLevel == StrengthLevel.BULLETPROOF
        binding.strengthLevelTxt.text = strengthLevel.name
        binding.strengthLevelTxt.setTextColor(ContextCompat.getColor(this, color))
//        strength_level_indicator.setBackgroundColor(ContextCompat.getColor(this, color))
    }

    private fun initViews() {
        binding.button.setOnClickListener {
            cPassword = binding.tilConPassword.text.toString();
            Email = binding.tieEmail.text.toString();
            Password = binding.passwordInput1.text.toString()
            validateFields()
            if (binding.button.isEnabled) {
                checkBox()
            }
        }
    }

    private fun firebaseAuth() {
        fAuth.createUserWithEmailAndPassword(Email, Password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "User created Sucessfully", Toast.LENGTH_SHORT).show()
                    // fAuth.updateCurrentUser(FirebaseUser(fi))
                    startActivity(Intent(this, MainActivity::class.java))
                } else {
                    Toast.makeText(this, "Error !" + task.exception, Toast.LENGTH_SHORT).show()
                    binding.button.isEnabled

                }
            }
    }

    private fun validateFields(): Boolean {
        clearError()
        return when {
            binding.tieEmail.text.isNullOrEmpty() -> {
                clearError()
                binding.tilEmail.requestFocus()
                binding.tilEmail.error = "Email cannot be empty"
                false
            }
            !binding.tieEmail.text.toString().isEmail() -> {
                clearError()
                binding.tilEmail.requestFocus()
                binding.tilEmail.error = "Email Pattern doesnot match"
                false
            }
            binding.passwordInput1.text.isNullOrEmpty() -> {
                clearError()
                binding.passwordInput1.requestFocus()
                binding.passwordInput1.error = "Password cannot be empty"
                false
            }
            binding.tilConPassword.text.isNullOrEmpty() -> {
                clearError()
                binding.tilConPassword.requestFocus()
                binding.tilConPassword.error = "Confirm Password is empty"
                false
            }
            (Password != (cPassword)) -> {
                clearError()
                binding.tilConPassword.requestFocus()
                binding.tilConPassword.error = " Password does not match"
                false
            }
            else -> {

                return true
            }
        }
    }

    private fun clearError() {
        binding.apply {
            tieEmail.error = null
            passwordInput1.error = null
        }
    }

    private fun checkBox() {
        if (!binding.cbRememberMe.isChecked) {
            Toast.makeText(
                this,
                "Captcha validation needed for security reason ",
                Toast.LENGTH_SHORT
            ).show()

        } else {
            capFunctions()
        }

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
        Toast.makeText(this, "Validate field!", Toast.LENGTH_SHORT).show()
    }

    override fun onVerificationCodeVerified() {
        firebaseAuth()
        startActivity(Intent(this, MainActivity::class.java))
        Toast.makeText(this, "WelCome to Dashboard", Toast.LENGTH_SHORT).show()

    }


}