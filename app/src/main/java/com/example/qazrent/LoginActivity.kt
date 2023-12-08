package com.example.qazrent

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var signupRedirectText: TextView
    private lateinit var resetRedirectText: TextView

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        emailEditText = findViewById(R.id.emailEt)
        passwordEditText = findViewById(R.id.passET)
        loginButton = findViewById(R.id.login_button)
        signupRedirectText = findViewById(R.id.signupRedirectText)
        resetRedirectText = findViewById(R.id.resetRedirectText)

        loginButton.setOnClickListener {
            loginUser()
        }
        signupRedirectText.setOnClickListener {
            val loginIntent = Intent(this, RegisterActivity::class.java)
            startActivity(loginIntent)
        }
        resetRedirectText.setOnClickListener {
            val loginIntent = Intent(this, ResetPassword::class.java)
            startActivity(loginIntent)
        }
    }

    override fun onStart() {
        super.onStart()

        val currentUser: FirebaseUser? = auth.currentUser
        if (currentUser != null) {

            startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
            finish()
        }
    }

    private fun loginUser() {
        val email: String = emailEditText.text.toString().trim()
        val password: String = passwordEditText.text.toString().trim()

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this@LoginActivity, "Поля не должны быть пустыми", Toast.LENGTH_SHORT).show()
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser

                    user?.let {
                        val userUid = it.uid
                        val firestore = FirebaseFirestore.getInstance()
                        val userDocRef = firestore.collection("users").document(userUid)

                        userDocRef.get()
                            .addOnSuccessListener { document ->
                                if (document.exists()) {
                                    val isAdmin = document.getBoolean("Administrator") ?: false
                                    saveUserIsAdmin(isAdmin)

                                    if (isAdmin) {
                                        startActivity(
                                            Intent(this@LoginActivity, HomeActivity::class.java)
                                                .putExtra("isAdmin", isAdmin)
                                        )
                                    } else {
                                        startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                                    }
                                    finish()
                                } else {
                                    // Handle the case where the user document doesn't exist
                                    Toast.makeText(
                                        this@LoginActivity,
                                        "Ошибка входа: Документ пользователя не найден",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                            .addOnFailureListener { exception ->
                                // Handle exceptions while fetching user data from Firestore
                                Toast.makeText(
                                    this@LoginActivity,
                                    "Ошибка входа: $exception",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        "Ошибка входа: " + task.exception?.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

    }

    private fun saveUserIsAdmin(isAdmin: Boolean) {
        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("isAdmin", isAdmin)
        editor.apply()
    }
}