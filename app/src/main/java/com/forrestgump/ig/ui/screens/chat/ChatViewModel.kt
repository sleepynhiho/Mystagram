package com.forrestgump.ig.ui.screens.chat

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.forrestgump.ig.data.models.Chat
import com.forrestgump.ig.data.models.Message
import com.forrestgump.ig.data.models.User
import com.forrestgump.ig.data.repositories.ChatRepository
import com.forrestgump.ig.utils.constants.EncryptionUtils
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val chatRepository: ChatRepository,
    private val encryptionUtils: EncryptionUtils // Inject the encryption utils
) : ViewModel() {
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    private val _users = MutableLiveData<List<User>>()
    val users: LiveData<List<User>> = _users

    private val _chatsState = MutableStateFlow<List<Chat>>(emptyList())
    val chatsState: StateFlow<List<Chat>> = _chatsState

    private val _chat = MutableLiveData<Chat?>()
    val chat: LiveData<Chat?> = _chat

    fun loadUsers() {
        firestore.collection("users")
            .get()
            .addOnSuccessListener { result ->
                val userList = result.map { document ->
                    User(
                        userId = document.id,
                        username = document.getString("username") ?: "",
                        profileImage = document.getString("profileImage") ?: ""
                    )
                }
                _users.postValue(userList) // Use postValue to ensure updates on the main thread
            }
            .addOnFailureListener { exception ->
                Log.e("ChatViewModel", "Error fetching users: ", exception)
            }
    }

    fun getChatsForUser(userId1: String) {
        viewModelScope.launch {
            try {
                val chats = chatRepository.getAllChatsForUser(userId1)
                _chatsState.value = chats
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Error fetching chats: ", e)
            }
        }
    }

    fun createChatIfNotExists(user1: User, user2: User, onChatCreated: (Chat) -> Unit) {
        viewModelScope.launch {
            try {
                // Call repository to create or fetch the chat
                val chat = chatRepository.createChatIfNotExists(
                    user1.userId, user2.userId, user1.username, user2.username,
                    user1.profileImage, user2.profileImage
                )

                onChatCreated(chat)
                _chatsState.value += chat
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Error creating chat: ", e)
            }
        }
    }

    fun loadChatAndMessages(chatId: String) {
        viewModelScope.launch {
            try {
                val (chatData, encryptedMessages) = chatRepository.loadChatAndMessages(chatId)
                _chat.postValue(chatData)

                val decryptedMessages = encryptedMessages.map { message ->
                    if (message.content?.isNotEmpty() == true) {
                        message.copy(content = encryptionUtils.decrypt(message.content))
                    } else {
                        message
                    }
                }

                _messages.value = decryptedMessages
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Error loading chat and messages: ", e)
            }
        }
    }

    fun listenForMessages(chatId: String) {
        chatRepository.listenForMessages(chatId) { updatedEncryptedMessages ->
            val decryptedMessages = updatedEncryptedMessages.map { message ->
                if (message.content?.isNotEmpty() == true) {
                    message.copy(content = encryptionUtils.decrypt(message.content))
                } else {
                    message
                }
            }

            _messages.value = decryptedMessages
        }
    }

    fun saveMessageToFirestore(chat: Chat, message: Message, currentUserId: String) {
        val messageId = message.messageID.ifEmpty {
            UUID.randomUUID().toString()
        }

        try {
            if (message.content.isNullOrEmpty()) {
                Log.w("ChatViewModel", "Message content is empty, not encrypting")

                firestore.collection("chats")
                    .document(chat.chatId)
                    .collection("messages")
                    .document(messageId)
                    .set(message.copy(messageID = messageId))
                    .addOnSuccessListener {
                        Log.d("ChatViewModel", "Empty message saved successfully")
                    }
                    .addOnFailureListener { e ->
                        Log.e("ChatViewModel", "Error saving empty message: $e")
                    }

                return
            }

            val encryptedContent = encryptionUtils.encrypt(message.content)

            if (encryptedContent == "ENCRYPTION_FAILED") {
                firestore.collection("chats")
                    .document(chat.chatId)
                    .collection("messages")
                    .document(messageId)
                    .set(message.copy(messageID = messageId))
                return
            }

            val encryptedMessage = message.copy(
                messageID = messageId,
                content = encryptedContent
            )

            firestore.collection("chats")
                .document(chat.chatId)
                .collection("messages")
                .document(messageId)
                .set(encryptedMessage)
                .addOnSuccessListener {
                    Log.d("ChatViewModel", "Encrypted message saved successfully!")
                }
                .addOnFailureListener { e ->
                    Log.e("ChatViewModel", "Error saving encrypted message: $e")
                }

            // Update last message in chat
            firestore.collection("chats")
                .document(chat.chatId)
                .update(
                    mapOf(
                        "lastMessage" to encryptedContent,
                        "lastMessageTime" to message.timestamp,
                        "user1Read" to (chat.user1Id == currentUserId),
                        "user2Read" to (chat.user2Id == currentUserId)
                    )
                )
                .addOnSuccessListener {
                    Log.d("ChatViewModel", "Chat last message updated successfully")
                }
                .addOnFailureListener { e ->
                    Log.e("ChatViewModel", "Error updating chat last message: $e")
                }
        } catch (e: Exception) {
            Log.e("ChatViewModel", "Unexpected error in saveMessageToFirestore", e)

            // Save original message as fallback
            firestore.collection("chats")
                .document(chat.chatId)
                .collection("messages")
                .document(messageId)
                .set(message.copy(messageID = messageId))
        }
    }
}