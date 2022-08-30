package com.example.chatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatapp.adapters.ChatListAdapter
import com.example.chatapp.models.Person
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val auth by lazy { FirebaseAuth.getInstance().currentUser }

    private lateinit var chatListAdapter: ChatListAdapter
    private val db = Firebase.firestore
    var contactList: MutableList<Person?> = mutableListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val layoutManager= LinearLayoutManager(this)
        rvChatList.layoutManager = layoutManager


        verifyCurrentUser()
        getUserContacts()



    }

    private fun verifyCurrentUser(){

        if (auth?.uid == null){
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            this.finish()
        }

    }

    private fun getUserContacts(){

        var listOfContacts : List<Any>


        auth?.uid?.let { db.collection("users").document(it).get().addOnSuccessListener {
            document-> listOfContacts = document.data?.get("contacts") as List<Any>


                for(contact in listOfContacts){
                    db.collection("users").whereEqualTo(FieldPath.documentId(), contact ).get()
                        .addOnSuccessListener {
                                docs->

                            for (doc in docs.documents){
                                val newPerson = doc.toObject(Person::class.java)

                                contactList.add(newPerson)

                            }
                            chatListAdapter = ChatListAdapter(this, contactList)
                            rvChatList.adapter = chatListAdapter

                        }
                }



        }
        }

    }
}