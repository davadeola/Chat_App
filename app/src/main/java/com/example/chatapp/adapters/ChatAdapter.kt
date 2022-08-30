package com.example.chatapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.Constants
import com.example.chatapp.R
import com.example.chatapp.models.Message
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_list_item.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*


class ChatAdapter(private val context: Context, private val messageList: List<Message>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val auth by lazy { FirebaseAuth.getInstance().currentUser }

    class FromViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun setData(message: Message?){
            //itemView.txvTime.text = message?.sentTime.toString()
            itemView.txtToRow.text = message?.value
        }

    }

    class ToViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun setData(message: Message?){
            //itemView.txvTime.text = message?.sentTime.toString()
            itemView.txtFromRow.text = message?.value
        }

    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return if (viewType == Constants.FROM_MESSAGE){
            val view = LayoutInflater.from(context).inflate(R.layout.chat_to_row,parent, false )
            FromViewHolder(view)
        }else{
            val view  = LayoutInflater.from(context).inflate(R.layout.chat_from_row,parent, false )
            ToViewHolder(view)
        }


    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val current = messageList[position]
        if (getItemViewType(position) == Constants.FROM_MESSAGE){
            (holder as FromViewHolder).setData(current)
        }else{
            (holder as ToViewHolder).setData(current)
        }
    }

    override fun getItemCount(): Int = messageList.size

    override fun getItemViewType(position: Int): Int {

        return if(messageList[position].from == auth?.email){
            Constants.FROM_MESSAGE
        }else{
            Constants.TO_MESSAGE
        }


    }


}