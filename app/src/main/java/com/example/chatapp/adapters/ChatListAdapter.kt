package com.example.chatapp.adapters

import android.content.Context
import android.content.Intent
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.ChatActivity
import com.example.chatapp.R
import com.example.chatapp.models.Person
import kotlinx.android.synthetic.main.chat_list_item.view.*
import java.util.*


class ChatListAdapter(private val context: Context, var list: List<Person?>): RecyclerView.Adapter<ChatListAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.chat_list_item, parent, false)
        return  MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val current = list[position]
        holder.setData(current)

    }

    override fun getItemCount(): Int = list.size




    inner class MyViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {

        private var current:Person? = null

        init {
            itemView.setOnClickListener{
                val intent = Intent(context, ChatActivity::class.java)
                intent.putExtra("CHAT_NAME", current?.name)
                intent.putExtra("CHAT_EMAIL", current?.emailAddress)
                context.startActivity(intent)
            }
        }

        fun setData(current: Person?){
            current?.let {
                val (name, _, recentTime)= current
                itemView.txvCurrentChatName.text = name
                itemView.chatAvatar.text = name?.substring(0, 1)?.uppercase(Locale.ROOT)

                itemView.txvTime.text = recentTime?.let { it1 ->
                    DateUtils.getRelativeTimeSpanString(
                        it1.time)
                }

                this.current = current

            }

        }

    }
}

