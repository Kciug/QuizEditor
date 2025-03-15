package com.rafalskrzypczyk.chat.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rafalskrzypczyk.chat.R
import com.rafalskrzypczyk.chat.domain.Message
import com.rafalskrzypczyk.core.extensions.formatDate
import com.rafalskrzypczyk.core.generic.GenericDiffCallback

class MessagesAdapter(
    private val currentUserId: String
) : ListAdapter<Message, RecyclerView.ViewHolder>(GenericDiffCallback(
    itemsTheSame = { oldItem, newItem -> oldItem.id == newItem.id },
    contentsTheSame = { oldItem, newItem -> oldItem == newItem }
)) {
    private val typeSent = 1
    private val typeReceived = 2

    class SentMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageText: TextView = itemView.findViewById(R.id.tv_message)
        private val timestamp: TextView = itemView.findViewById(R.id.tv_date_sent)

        private var isDateVisible = false

        fun bind(message: Message) {
            timestamp.visibility = View.GONE

            messageText.text = message.message
            timestamp.text = String.formatDate(message.timestamp)

            itemView.setOnClickListener {
                if(isDateVisible) timestamp.visibility = View.GONE
                else timestamp.visibility = View.VISIBLE

                isDateVisible = !isDateVisible
            }
        }
    }

    class ReceivedMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageText: TextView = itemView.findViewById(R.id.tv_message)
        private val timestamp: TextView = itemView.findViewById(R.id.tv_date_sent)
        private val senderName: TextView = itemView.findViewById(R.id.tv_sender_name)

        private var isDateVisible = false

        fun bind(message: Message, showSender: Boolean) {
            timestamp.visibility = View.GONE

            messageText.text = message.message
            timestamp.text = String.formatDate(message.timestamp)

            if (showSender) {
                senderName.text = message.senderName
                senderName.visibility = View.VISIBLE
            } else {
                senderName.visibility = View.GONE
            }

            itemView.setOnClickListener {
                if(isDateVisible) timestamp.visibility = View.GONE
                else timestamp.visibility = View.VISIBLE

                isDateVisible = !isDateVisible
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).senderId == currentUserId) typeSent else typeReceived
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == typeSent) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.card_message_sent, parent, false)
            SentMessageViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.card_message_received, parent, false)
            ReceivedMessageViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = getItem(position)

        if (holder is SentMessageViewHolder) {
            holder.bind(message)
        } else if (holder is ReceivedMessageViewHolder) {
            val showSender =  position == itemCount - 1 || getItem(position + 1).senderId != message.senderId
            holder.bind(message, showSender)
        }
    }
}