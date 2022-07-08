package com.aiextech.captaapp

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextWatcher
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.widget.doOnTextChanged
import com.aiextech.captaapp.databinding.ActivityRegisterBinding
import com.aiextech.captaapp.extensions.isEmail
import com.aiextech.captaapp.utils.CaptchaUI
import com.google.firebase.auth.FirebaseAuth
import com.jacknkiarie.captchaui.CaptchaLayout

class RegisterActivity : AppCompatActivity(), CaptchaLayout.OnButtonClickedListener {
    private lateinit var binding: ActivityRegisterBinding
    private var fAuth = FirebaseAuth.getInstance()
    private lateinit var Email: String
    private lateinit var Password: String
    private lateinit var fullName: String
    private lateinit var cPassword: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.elevation = 0f
        supportActionBar?.title=""
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(Color.parseColor("#FFFFFFFF")))
        initViews();
      checkUser()
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, RegisterActivity::class.java)
        }
    }


    private fun initViews() {
        binding.btnRegister.setOnClickListener {
            fullName = binding.tilFullName.text.toString();
            cPassword = binding.tilConPassword.text.toString();
            Email = binding.tilEmail.text.toString();
            Password = binding.tilPassword.text.toString()

            if (validateFields()) {
                checkBox()
            }
        }
        binding.ivCaptcha.setOnClickListener {
            capFunctions()
        }


    }


    private fun validateFields(): Boolean {
        clearError()
        return when {
            binding.tilFullName.text.isNullOrEmpty() -> {
                clearError()
                binding.tilFullName.requestFocus()
                binding.tilFullName.error = "FirstName cannot be empty"
                false
            }
            binding.tilEmail.text.isNullOrEmpty() -> {
                clearError()
                binding.tilEmail.requestFocus()
                binding.tilEmail.error = "Email cannot be empty"
                false
            }
            !binding.tilEmail.text.toString().isEmail() -> {
                clearError()
                binding.tilEmail.requestFocus()
                binding.tilEmail.error = "Email Pattern doesnot match"
                false
            }
            binding.tilPassword.text.isNullOrEmpty() -> {
                clearError()
                binding.tilPassword.requestFocus()
                binding.tilPassword.error = "Password cannot be empty"
                false
            }
            binding.tilPassword.text.toString().length < 8 -> {
                clearError()
                binding.tilPassword.requestFocus()
                binding.tvStrongPassword.visibility = View.VISIBLE
                binding.tvStrongPassword.text = "Weak Password"
                binding.tvStrongPassword.setTextColor(
                    AppCompatResources.getColorStateList(
                        this,
                        R.color.red
                    )
                )
                binding.tilPassword.error = "Password must be more than  8 char long"
                false
            }
            !binding.tilPassword.text.toString().matches(".*[A-Z].*".toRegex()) -> {
                clearError()
                binding.tilPassword.requestFocus()
                binding.tilPassword.error =
                    "Password must contain 1 Upper-case Character" //upper case huda ni yo eror aayo
                false
            }
            !binding.tilPassword.text.toString().matches(".*[a-z].*".toRegex()) -> {
                clearError()
                binding.tilPassword.requestFocus()
                binding.tilPassword.error = "Password must contain 1 Lower-case Character"
                false
            }
            !binding.tilPassword.text.toString().matches(".*[@#\$%+=].*".toRegex()) -> {
                clearError()
                binding.tilPassword.requestFocus()
                binding.tilPassword.error = "Password must contain 1  Special Character"
                false
            }
            !binding.tilPassword.text.toString().matches(".*[0-9].*".toRegex()) -> {
                clearError()
                binding.tilPassword.requestFocus()
                binding.tilPassword.error = "Password must contain 1  numeric  Character"
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



    private fun checkBox() {
        if (!binding.cbRememberMe.isChecked) {
            Toast.makeText(
                this,
                "Captcha validation needed for security reason ",
                Toast.LENGTH_SHORT
            ).show()

        }else{
            capFunctions()
        }

    }

    private fun checkUser() {
        if (fAuth.currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
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

    private fun showCaptchaDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_captcha)
//        val body = dialog.findViewById(R.id.captcha_header) as TextView
//        body.text = title
        CaptchaUI.Builder(this)
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
}
