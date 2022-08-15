package com.example.firebaseauth

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.firebaseauth.databinding.ActivityMainBinding
import com.example.firebaseauth.emailPassAuth.LoginActivity
import com.example.firebaseauth.intentKey.IntentKey
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

@Suppress("NAME_SHADOWING")
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var database: DatabaseReference

    @SuppressLint("SetTextI18n")
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

        binding.btSave.setOnClickListener {

            if (binding.btSave.text == "Save") {
                saveData()
            }
        }

        binding.btReadData.setOnClickListener {

            readData()

        }

        binding.btReadData.setOnLongClickListener {

            binding.btSave.text = "Save"
            binding.etAge.text.clear()
            binding.etFirstName.text.clear()
            binding.etLastName.text.clear()
            binding.etUserName.text.clear()

            return@setOnLongClickListener true
        }

        binding.btDelete.setOnClickListener {

            deleteData()
        }

    }

    private fun deleteData() {
        val userName = binding.etUserName.text.toString()

        if (userName.isNotEmpty()) {

            database = FirebaseDatabase.getInstance().getReference("Users")
            database.child(userName).removeValue().addOnCompleteListener {

                if (it.isSuccessful) {

                    message("successfully Deleted!!")
                    binding.etUserName.setText("")

                } else {
                    message("error")
                }

            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateData() {

        val firstName = binding.etFirstName.text.toString()
        val lastName = binding.etLastName.text.toString()
        val age = binding.etAge.text.toString()
        val userName = binding.etUserName.text.toString()

        val user = HashMap<String, String>()
        user["firstName"] = firstName
        user["lastName"] = lastName
        user["age"] = age
        user["userName"] = userName

        database = FirebaseDatabase.getInstance().getReference("Users")
        database.child(userName).updateChildren(user as Map<String, Any>).addOnCompleteListener {

            if (it.isSuccessful) {

                binding.etAge.text.clear()
                binding.etFirstName.text.clear()
                binding.etLastName.text.clear()
                binding.etUserName.text.clear()
                message("successfully updated")
                binding.btSave.text = "Save"


            } else {
                message("failed to update")
            }

        }


    }

    private fun message(message_: String, duration: Int = Toast.LENGTH_LONG) {
        Toast.makeText(this, message_, duration).show()

    }

    @SuppressLint("SetTextI18n")
    private fun readData() {

        val firstName = binding.etFirstName.text.toString()
        val lastName = binding.etLastName.text.toString()
        val age = binding.etAge.text.toString()
        val userName = binding.etUserName.text.toString()

        if (firstName.isEmpty() && lastName.isEmpty() && age.isEmpty() &&
            userName.isNotEmpty()
        ) {

            try {

                database = FirebaseDatabase.getInstance().getReference("Users")
                database.child(userName).get().addOnCompleteListener {

                    if (it.isSuccessful) {

                        if (it.result.exists()) {

                            message("successfully read")
                            val dataSnapshot: DataSnapshot = it.result

                            val firstName = dataSnapshot.child("firstName").value.toString()
                            val lasName = dataSnapshot.child("lastName").value.toString()
                            val age = dataSnapshot.child("age").value.toString()

                            binding.etFirstName.setText(firstName)
                            binding.etLastName.setText(lasName)
                            binding.etAge.setText(age)

                            binding.btSave.text = "Update"

                            binding.btSave.setOnClickListener {

                                if (binding.btSave.text == "Update") {
                                    updateData()
                                }
                            }

                        } else {
                            message("Data does not exist")
                        }

                    } else {
                        message("Failed to read")
                    }

                }

            } catch (e: Exception) {
                message(e.message.toString())
            }
        }

    }

    private fun saveData() {

        val firstName = binding.etFirstName.text.toString()
        val lastName = binding.etLastName.text.toString()
        val age = binding.etAge.text.toString()
        val userName = binding.etUserName.text.toString()

        if (firstName.isNotEmpty() && age.isNotEmpty() && lastName.isNotEmpty() && userName.isNotEmpty()) {

            try {

                database =
                    FirebaseDatabase.getInstance("https://fir-auth-f8001-default-rtdb.firebaseio.com")
                        .getReference("Users")

                val users = User(firstName, lastName, age, userName)

                database.child(userName).setValue(users).addOnSuccessListener {

                    binding.etAge.text.clear()
                    binding.etFirstName.text.clear()
                    binding.etLastName.text.clear()
                    binding.etUserName.text.clear()

                    Toast.makeText(this, "Successfully added", Toast.LENGTH_SHORT).show()

                }.addOnFailureListener {

                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()

                }

            } catch (e: Exception) {

                Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()

            } finally {
                Toast.makeText(this, "finished", Toast.LENGTH_SHORT).show()

            }
        } else {
            message("error")
        }

    }

}