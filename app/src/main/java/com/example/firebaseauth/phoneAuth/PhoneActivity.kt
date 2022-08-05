package com.example.firebaseauth.phoneAuth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.transition.Visibility
import com.example.firebaseauth.databinding.ActivityPhoneBinding
import com.example.firebaseauth.intentKey.IntentKey
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import java.util.concurrent.TimeUnit

class PhoneActivity : AppCompatActivity() {

    lateinit var binding: ActivityPhoneBinding
    lateinit var auth: FirebaseAuth
     lateinit var phoneNumber : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhoneBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()

        binding.btSendOtp.setOnClickListener {

            val prenNumber = binding.etPhoneNumber2.text.trim().toString()
             phoneNumber = binding.etPhoneNumber.text.trim().toString()

            if (phoneNumber.isNotEmpty() && phoneNumber.length == 10) {

                binding.phoneProgressBar.visibility = View.VISIBLE

                phoneNumber = prenNumber + phoneNumber

                val options = PhoneAuthOptions.newBuilder(auth)
                    .setPhoneNumber(phoneNumber)       // Phone number to verify
                    .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                    .setActivity(this)                 // Activity (for callback binding)
                    .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
                    .build()
                PhoneAuthProvider.verifyPhoneNumber(options)

            } else {

                message("Please Enter Correct Number")
            }
        }


    }

    private fun init() {
        binding.phoneProgressBar.visibility = View.INVISIBLE
        auth = FirebaseAuth.getInstance()
    }

    fun message(message_: String, duration: Int = Toast.LENGTH_LONG) {
        Toast.makeText(this, message_, duration).show()

    }

    private fun sendToMain(user: FirebaseUser){
        startActivity(Intent(this , PhoneMainActivity::class.java))
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

            val intent = Intent(this@PhoneActivity, OTPActivity::class.java)
            intent.putExtra(IntentKey.otp, verificationId)
            intent.putExtra(IntentKey.resendingToken, token)
            intent.putExtra(IntentKey.phoneNumber, phoneNumber)
            startActivity(intent)
            binding.phoneProgressBar.visibility = View.INVISIBLE

        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = task.result?.user
                    // Sign in success, update UI with the signed-in user's information
                    message("Authenticate Successfully")
                    if (user != null) {
                        sendToMain(user)
                    }


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

    override fun onStart() {
        super.onStart()

        if (auth.currentUser != null) {
            startActivity(Intent(this, PhoneMainActivity::class.java))

        }

    }

}