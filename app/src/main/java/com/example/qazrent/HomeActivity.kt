package com.example.qazrent

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import de.hdodenhof.circleimageview.CircleImageView
import android.content.SharedPreferences


class HomeActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private var isAdmin = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        isAdmin = sharedPreferences.getBoolean("isAdmin", false)

        val textViewWelcome: TextView = findViewById(R.id.textViewWelcome)
        val buttonPersonalAccount: Button = findViewById(R.id.buttonPersonalAccount)
        val buttonRegisterUsers: Button = findViewById(R.id.buttonRegisterUsers)
        val imageViewProfile: CircleImageView = findViewById(R.id.imageViewProfile)

        val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        user?.let {
            val username = it.email

            if (isAdmin) {
                textViewWelcome.text = "Права доступа: админ. Здравствуйте, $username!"
                buttonRegisterUsers.visibility = View.VISIBLE
            } else {
                textViewWelcome.text = "Права доступа: пользователь. Здравствуйте, $username!"
                buttonRegisterUsers.visibility = View.GONE
            }

            val userId = FirebaseAuth.getInstance().currentUser?.uid
            val userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId!!)
            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val photoUrl = dataSnapshot.child("photoUrl").value as? String
                    if (!photoUrl.isNullOrBlank()) {
                        // Загрузка и отображение фотографии
                        Glide.with(this@HomeActivity)
                            .load(photoUrl)
                            .placeholder(R.drawable.logo)
                            .into(imageViewProfile)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                }
            })
        }
    }


    fun openPersonalAccount(view: View) {

    }

    fun openRegisterUsers(view: View) {

    }

    fun logout(view: View) {
        FirebaseAuth.getInstance().signOut()
        startActivity(Intent(this@HomeActivity, MainActivity::class.java))
        finish()
    }

    fun changePassword(view: View) {
        val intent = Intent(this, ChangePasswordActivity::class.java)
        startActivity(intent)
    }

}