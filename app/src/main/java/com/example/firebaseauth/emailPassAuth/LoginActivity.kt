package com.example.firebaseauth.emailPassAuth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.firebaseauth.intentKey.IntentKey
import com.example.firebaseauth.MainActivity
import com.example.firebaseauth.databinding.ActivityLoginBinding
import com.example.firebaseauth.phoneAuth.PhoneActivity
import com.example.firebaseauth.phoneAuth.PhoneMainActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btRegister.setOnClickListener {

            if (binding.etEmail.text!!.isEmpty() && binding.etPass.text!!.isEmpty()) {
                Toast.makeText(
                    this, "Please Enter required fills !!",
                    Toast.LENGTH_LONG
                ).show()
            } else {

                val email: String = binding.etEmail.text.toString().trim { it <= ' ' }
                val pass: String = binding.etPass.text.toString().trim { it <= ' ' }

                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, pass)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {

                            Toast.makeText(
                                this, "you are registered successfully!!",
                                Toast.LENGTH_LONG
                            ).show()

                            val intent = Intent(this, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK
                            intent.putExtra(
                                IntentKey.user_id,
                                FirebaseAuth.getInstance().currentUser!!.uid
                            )
                            intent.putExtra(IntentKey.email_id, email)
                            startActivity(intent)
                            finish()

                        } else {

                            Toast.makeText(
                                this, task.exception!!.message,
                                Toast.LENGTH_LONG
                            ).show()
                        }

                    }
            }

        }

        binding.tvRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)


        }

        binding.tvRegisterPhone.setOnClickListener {
            val intent = Intent(this, PhoneActivity::class.java)
            startActivity(intent)

        }

    }
}
