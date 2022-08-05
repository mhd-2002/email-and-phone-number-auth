package com.example.firebaseauth.phoneAuth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.firebaseauth.R
import com.example.firebaseauth.databinding.ActivityPhoneMainBinding
import com.example.firebaseauth.intentKey.IntentKey
import com.google.firebase.auth.FirebaseAuth

class PhoneMainActivity : AppCompatActivity() {

    lateinit var binding: ActivityPhoneMainBinding
    lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhoneMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        val phoneNumber = auth.currentUser?.phoneNumber

        binding.tvPhoneNumber.append(phoneNumber)

        binding.btSignOut.setOnClickListener {

            auth.signOut()
            startActivity(Intent(this, PhoneActivity::class.java))

        }

    }
}