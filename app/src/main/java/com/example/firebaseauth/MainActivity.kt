package com.example.firebaseauth

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.firebaseauth.databinding.ActivityMainBinding
import com.example.firebaseauth.emailPassAuth.LoginActivity
import com.example.firebaseauth.intentKey.IntentKey
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val user = intent.getStringExtra(IntentKey.user_id)
        val email = intent.getStringExtra(IntentKey.email_id)

        binding.tvUser.text = user
        binding.tvEmail.text = email

        binding.btLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()

        }


    }
}