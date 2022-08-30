package com.example.chatapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chatapp.models.Person
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*

import java.sql.Timestamp
import java.time.LocalDateTime.now
import java.util.*


class LoginActivity : AppCompatActivity() {


    private val auth = FirebaseAuth.getInstance()
    private lateinit var listener: FirebaseAuth.AuthStateListener

    private val providers: List<AuthUI.IdpConfig> by lazy {
        arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build()

            )
    }
    private val signInIntent: Intent by lazy {
        AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()
    }
    private val db = Firebase.firestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        listener = FirebaseAuth.AuthStateListener { p0 ->
            if (p0.currentUser != null){//already logged in
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                this.finish()
            }else{

                signInLauncher.launch(signInIntent)

            }
        }




    }

    override fun onStart() {
        super.onStart()
       auth.addAuthStateListener(listener)
    }

    override fun onStop() {
        auth.removeAuthStateListener(listener)
        super.onStop()
    }

    private fun addNewUser(user: FirebaseUser){
        val newUser= Person(user.displayName, listOf(), Timestamp(System.currentTimeMillis()), user.email)
        db.collection("users").document(user.uid).set(newUser)
    }

    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        this.onSignInResult(res)
    }




    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        if (result.resultCode == RESULT_OK) {
            // Successfully signed in
           val user = auth.currentUser

            if (user != null) {
                //check if the collection is new
               db.collection("users").document(user.uid).get().addOnCompleteListener { task->
                    if (task.isSuccessful && task.result != null && !task.result.exists()){
                        addNewUser(user)
                    }
                }


            }

            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            this.finish()

        } else {

            if (response?.error?.errorCode == ErrorCodes.NO_NETWORK){
                Toast.makeText(this, "Check your internet connection", Toast.LENGTH_LONG).show()
            }else{
                response?.error?.message?.let { Log.e("ERROR", it) }
            }

        }
    }

    fun startLogin(view: View) {
        //for firebase UI
        signInLauncher.launch(signInIntent)
    }


}