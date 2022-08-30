package com.example.chatapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatapp.adapters.ChatAdapter
import com.example.chatapp.models.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


import kotlinx.android.synthetic.main.activity_chat.*
import java.util.*

class ChatActivity : AppCompatActivity() {
    private val auth by lazy { FirebaseAuth.getInstance().currentUser }

    private val chatEmail by lazy {
        intent.getStringExtra("CHAT_EMAIL")
    }

    private val db = Firebase.firestore
    private var messages: MutableList<Message> = mutableListOf()
    private lateinit var chatAdapter: ChatAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        setSupportActionBar(toolbar)
        supportActionBar?.title = ""
        //supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        val userName = intent.getStringExtra("CHAT_NAME")
        txvCurrentChatName.text= userName

        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        rv_chat.layoutManager = layoutManager


        fetchChatMessages()

        sendButton.setOnClickListener {
            sendChatMessage(extTypeHere.text.toString())
            extTypeHere.text.clear()
            rv_chat.scrollToPosition(chatAdapter.itemCount -1)
        }


    }




    private fun fetchChatMessages(){
        db.collection("messages").whereEqualTo("from", chatEmail).whereEqualTo("to", auth?.email).addSnapshotListener{
            value, e ->
            if (e != null){
                Log.w("ERROR_LISTENER", "listen:error", e)
                return@addSnapshotListener
            }

            for (dc in value!!.documentChanges){
                when(dc.type){
                    DocumentChange.Type.ADDED-> {
                        val addedMessage = dc.document.toObject(Message::class.java)
                        messages.add(addedMessage)
                    }
                    DocumentChange.Type.MODIFIED -> TODO()
                    DocumentChange.Type.REMOVED -> {
                        val removedMessage = dc.document.toObject(Message::class.java)
                        messages.remove(removedMessage)
                    }
                }

            }
            chatAdapter = ChatAdapter(this, messages.sortedBy{it.sentTime})
            rv_chat.adapter = chatAdapter



        }

        db.collection("messages").whereEqualTo("from", auth?.email).whereEqualTo("to", chatEmail).addSnapshotListener{
                value, e ->
            if (e != null){
                Log.w("ERROR_LISTENER", "listen:error", e)
                return@addSnapshotListener
            }


            for (dc in value!!.documentChanges){
                when(dc.type){
                    DocumentChange.Type.ADDED-> {
                        val message = dc.document.toObject(Message::class.java)
                        messages.add(message)
                    }
                    DocumentChange.Type.MODIFIED -> TODO()
                    DocumentChange.Type.REMOVED -> TODO()
                }
            }
            chatAdapter = ChatAdapter(this, messages.sortedBy{it.sentTime})
            rv_chat.adapter = chatAdapter


        }


    }

    private fun sendChatMessage(message: String){
        val sentMessage = Message(auth?.email, message, chatEmail, Date())

        db.collection("messages").add(sentMessage).addOnSuccessListener {
            messages.add(sentMessage)
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            androidx.appcompat.R.id.home -> {
                Log.d("Clicked", "Clickerss")
                this.finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}