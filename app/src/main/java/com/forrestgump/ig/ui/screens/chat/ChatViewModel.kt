package com.forrestgump.ig.ui.screens.chat

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.forrestgump.ig.data.models.Chat
import com.forrestgump.ig.data.models.Message
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {
    private val _messages = mutableStateListOf<Message>()
    val messages: List<Message> get() = _messages

    fun saveMessageToFirestore(chat: Chat, message: Message, currentUserId: String) {
        firestore.collection("chats")
            .document(chat.chatId)
            .collection("messages")
            .document(message.messageID)
            .set(message)
            .addOnSuccessListener {
                println("Message saved successfully!")
            }
            .addOnFailureListener { e ->
                println("Error saving message: $e")
            }

        // Update last message in chats
        firestore.collection("chats")
            .document(chat.chatId)
            .update(
                mapOf(
                    "lastMessage" to message.content,
                    "lastMessageTime" to message.timestamp,
                    "user1Read" to (chat.user1Id == currentUserId),
                    "user2Read" to (chat.user2Id == currentUserId)
                )
            )

    }
}