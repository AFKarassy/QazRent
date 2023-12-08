package com.example.qazrent

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {

    private lateinit var editTextLogin: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var editTextConfirmPassword: EditText
    private lateinit var editTextFirstName: EditText
    private lateinit var editTextName: EditText
    private lateinit var editTextLastName: EditText
    private lateinit var spinnerSex: Spinner
    private lateinit var editTextIIN: EditText
    private lateinit var spinnerUserType: Spinner

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference

        editTextLogin = findViewById(R.id.editTextLogin)
        editTextPassword = findViewById(R.id.editTextPassword)
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword)
        editTextFirstName = findViewById(R.id.editTextFirstName)
        editTextName = findViewById(R.id.editTextName)
        editTextLastName = findViewById(R.id.editTextLastName)
        spinnerSex = findViewById(R.id.spinnerSex)
        editTextIIN = findViewById(R.id.editTextIIN)
        spinnerUserType = findViewById(R.id.spinnerUserType)

        val buttonRegister: Button = findViewById(R.id.buttonRegister)
        buttonRegister.setOnClickListener { register() }
    }

    private fun register() {
        val login = editTextLogin.text.toString().trim()
        val password = editTextPassword.text.toString().trim()
        val firstName = editTextFirstName.text.toString().trim()
        val name = editTextName.text.toString().trim()
        val lastName = editTextLastName.text.toString().trim()
        val selectedSex = spinnerSex.selectedItem.toString()
        val iin = editTextIIN.text.toString().trim()
        val userType = spinnerUserType.selectedItem.toString()
        val administrator = false
        var renter: Boolean
        var tenant: Boolean

        if(userType == "Арендатор"){
            tenant = true
            renter = false
        }
        else {
            renter = true
            tenant = false
        }
        if (TextUtils.isEmpty(login) || TextUtils.isEmpty(password) || TextUtils.isEmpty(firstName) ||
            TextUtils.isEmpty(name) || TextUtils.isEmpty(lastName) || TextUtils.isEmpty(selectedSex) ||
            TextUtils.isEmpty(iin) || TextUtils.isEmpty(userType)
        ) {
            showToast(getString(R.string.fill_all_fields))
            return
        }

        val confirmPassword = editTextConfirmPassword.text.toString().trim()
        if (password != confirmPassword) {
            showToast(getString(R.string.passwords_do_not_match))
            return
        }


        auth.createUserWithEmailAndPassword(login, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    val user = User(firstName, name, lastName, selectedSex, iin, administrator, renter, tenant)
                    FirebaseDatabase.getInstance().getReference().child("users").child(userId!!).setValue(user)
                    databaseReference.child("users").child(userId).setValue(user)
                    showToast(getString(R.string.registration_successful))
                    startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                    finish()
                } else {
                    showToast(getString(R.string.registration_error) + ": " + task.exception?.message)
                }
            }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
