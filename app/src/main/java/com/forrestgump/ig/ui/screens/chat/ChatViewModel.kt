package com.forrestgump.ig.ui.screens.chat

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.forrestgump.ig.data.models.Chat
import com.forrestgump.ig.data.models.Message
import com.forrestgump.ig.data.models.User
import com.forrestgump.ig.data.repositories.ChatRepository
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val chatRepository: ChatRepository
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
        // Fetch the users from Firestore
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
                Log.d("NHII", userList.toString())
            }
            .addOnFailureListener { exception ->
                // Handle error here
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

                // Update the chat list state after creating the new chat
                _chatsState.value += chat
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Error creating chat: ", e)
            }
        }
    }

    fun loadChatAndMessages(chatId: String) {
        viewModelScope.launch {
            try {
                val (chatData, messagesList) = chatRepository.loadChatAndMessages(chatId)
                _chat.postValue(chatData) // Use postValue for LiveData
                _messages.value = messagesList // Update StateFlow directly
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Error loading chat and messages: ", e)
            }
        }
    }

    fun listenForMessages(chatId: String) {
        chatRepository.listenForMessages(chatId) { updatedMessages ->
            _messages.value = updatedMessages
            Log.d("ChatViewModel", "Messages updated: $updatedMessages")
        }
    }

    fun saveMessageToFirestore(chat: Chat, message: Message, currentUserId: String) {
        val messageId = message.messageID.ifEmpty {
            UUID.randomUUID().toString()  // Tạo ID nếu bị null hoặc trống
        }

        firestore.collection("chats")
            .document(chat.chatId)  // 🟢 Đây là Document (số chẵn segment)
            .collection("messages") // 🟢 Đây là Collection (số lẻ segment)
            .document(messageId)    // 🟢 Thêm document ID vào (để có số chẵn segment)
            .set(message.copy(messageID = messageId)) // Gán lại message với ID hợp lệ
            .addOnSuccessListener {
                Log.d("ChatViewModel", "Message saved successfully!")
            }
            .addOnFailureListener { e ->
                Log.e("ChatViewModel", "Error saving message: $e")
            }

        // Update last message in chat
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
