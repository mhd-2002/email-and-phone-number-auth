package com.example.firebaseauth.phoneAuth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import com.example.firebaseauth.databinding.ActivityOtpactivityBinding
import com.example.firebaseauth.intentKey.IntentKey
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import java.util.concurrent.TimeUnit

class OTPActivity : AppCompatActivity() {

    lateinit var binding: ActivityOtpactivityBinding
    lateinit var auth: FirebaseAuth
    private lateinit var OTP: String
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var phoneNumber: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        OTP = intent.getStringExtra(IntentKey.otp).toString()
        resendToken = intent.getParcelableExtra(IntentKey.resendingToken)!!
        phoneNumber = intent.getStringExtra(IntentKey.phoneNumber)!!

        binding.otpProgressBar.visibility = View.INVISIBLE

        auth = FirebaseAuth.getInstance()
        binding.otpEditText1.requestFocus()
        addTextChangeListener()
        resendOTPTvVisibility()

        binding.verifyOTPBtn.setOnClickListener {

            val typedOTP = binding.otpEditText1.text.toString() +
                    binding.otpEditText2.text.toString() +
                    binding.otpEditText3.text.toString() +
                    binding.otpEditText4.text.toString() +
                    binding.otpEditText5.text.toString() +
                    binding.otpEditText6.text.toString()

            if (typedOTP.isNotEmpty() && typedOTP.length == 6) {

                val credential: PhoneAuthCredential = PhoneAuthProvider.getCredential(
                    OTP, typedOTP
                )
                binding.otpProgressBar.visibility = View.VISIBLE
                signInWithPhoneAuthCredential(credential)

            } else message("Please Enter OTP correctly")

        }

        binding.resendTextView.setOnClickListener {
            resendVerificationCode()
            resendOTPTvVisibility()
        }


    }

    private fun resendOTPTvVisibility() {

        binding.otpEditText1.setText("")
        binding.otpEditText2.setText("")
        binding.otpEditText3.setText("")
        binding.otpEditText4.setText("")
        binding.otpEditText5.setText("")
        binding.otpEditText6.setText("")
        binding.resendTextView.visibility = View.INVISIBLE
        binding.resendTextView.isEnabled = false

        Handler(Looper.myLooper()!!).postDelayed(Runnable {

            binding.resendTextView.visibility = View.VISIBLE
            binding.resendTextView.isEnabled = true

        }, 60000)
    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {

            signInWithPhoneAuthCredential(credential)

        }

        override fun onVerificationFailed(e: FirebaseException) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.


            if (e is FirebaseAuthInvalidCredentialsException) {
                message("Invalid request code")

            } else if (e is FirebaseTooManyRequestsException) {
                message("The SMS quota for the project has been exceeded")
            }

            // Show a message and update the UI
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {

            OTP = verificationId
            resendToken = token

        }
    }

    private fun resendVerificationCode() {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(callbacks)
            .setForceResendingToken(resendToken)// OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun message(message_: String, duration: Int = Toast.LENGTH_LONG) {
        Toast.makeText(this, message_, duration).show()

    }

    private fun addTextChangeListener() {
        binding.otpEditText1.addTextChangedListener(EditTextWatcher(binding.otpEditText1))
        binding.otpEditText2.addTextChangedListener(EditTextWatcher(binding.otpEditText2))
        binding.otpEditText3.addTextChangedListener(EditTextWatcher(binding.otpEditText3))
        binding.otpEditText4.addTextChangedListener(EditTextWatcher(binding.otpEditText4))
        binding.otpEditText5.addTextChangedListener(EditTextWatcher(binding.otpEditText5))
        binding.otpEditText6.addTextChangedListener(EditTextWatcher(binding.otpEditText6))

    }

    inner class EditTextWatcher(private val view: View) : TextWatcher {

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun afterTextChanged(p0: Editable?) {
            val text = p0.toString()

            when (view.id) {
                binding.otpEditText1.id -> if (text.length == 1) binding.otpEditText2.requestFocus()
                binding.otpEditText2.id -> if (text.length == 1) binding.otpEditText3.requestFocus() else if (text.isEmpty()) binding.otpEditText1.requestFocus()
                binding.otpEditText3.id -> if (text.length == 1) binding.otpEditText4.requestFocus() else if (text.isEmpty()) binding.otpEditText2.requestFocus()
                binding.otpEditText4.id -> if (text.length == 1) binding.otpEditText5.requestFocus() else if (text.isEmpty()) binding.otpEditText3.requestFocus()
                binding.otpEditText5.id -> if (text.length == 1) binding.otpEditText6.requestFocus() else if (text.isEmpty()) binding.otpEditText4.requestFocus()
                binding.otpEditText6.id -> if (text.isEmpty()) binding.otpEditText5.requestFocus()


            }

        }


    }

    private fun sendToMain(user: FirebaseUser?) {


    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    message("Authenticate Successfully")
                    binding.otpProgressBar.visibility = View.VISIBLE

                    val intent = Intent(this, PhoneMainActivity::class.java)
                    startActivity(intent)

                } else {
                    // Sign in failed, display a message and update the UI
                    message(task.exception.toString())

                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        message(task.exception.toString())

                    }
                    // Update UI
                }
            }
    }

}