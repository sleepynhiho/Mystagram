package com.forrestgump.ig.data.repositories

import androidx.annotation.OptIn
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import com.forrestgump.ig.data.models.Chat
import com.forrestgump.ig.data.models.Message
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class ChatRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    // Function to get all chats where userId1 is the specified userId
    suspend fun getAllChatsForUser(userId: String): List<Chat> {
        return try {
            // Fetch chats where userId is either user1Id or user2Id
            val chatSnapshot1 = firestore.collection("chats")
                .whereEqualTo("user1Id", userId)
                .get()
                .await()  // Await the result of the query

            val chatSnapshot2 = firestore.collection("chats")
                .whereEqualTo("user2Id", userId)
                .get()
                .await()  // Await the result of the query

            // Combine the results from both queries
            val chatList = mutableListOf<Chat>()
            chatList.addAll(chatSnapshot1.documents.mapNotNull { it.toObject(Chat::class.java) })
            chatList.addAll(chatSnapshot2.documents.mapNotNull { it.toObject(Chat::class.java) })

            chatList
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()  // Return an empty list in case of failure
        }
    }

    // Function to create a new chat if it does not exist
    suspend fun createChatIfNotExists(user1Id: String, user2Id: String, user1Username: String, user2Username: String, user1ProfileImage: String, user2ProfileImage: String): Chat {
        val chatId = if (user1Id < user2Id) "${user1Id}_${user2Id}" else "${user2Id}_${user1Id}"

        return try {
            val chatRef = firestore.collection("chats").document(chatId)
            val chatSnapshot = chatRef.get().await()

            if (chatSnapshot.exists()) {
                chatSnapshot.toObject(Chat::class.java)!!
            } else {
                val newChat = Chat(
                    chatId = chatId,
                    user1Id = user1Id,
                    user2Id = user2Id,
                    user1Username = user1Username,
                    user2Username = user2Username,
                    user1ProfileImage = user1ProfileImage,
                    user2ProfileImage = user2ProfileImage
                )
                chatRef.set(newChat).await()
                newChat
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    suspend fun loadChatAndMessages(chatId: String): Pair<Chat?, List<Message>> {
        try {
            // Lấy thông tin chat
            val chatRef = firestore.collection("chats").document(chatId)
            val chatSnapshot = chatRef.get().await()

            var chatData: Chat? = null
            if (chatSnapshot.exists()) {
                chatData = chatSnapshot.toObject(Chat::class.java)
            }

            // Lấy tất cả các tin nhắn trong chat
            val messagesSnapshot = chatRef.collection("messages").get().await()
            val messagesList = messagesSnapshot.documents.map { doc ->
                doc.toObject(Message::class.java)!!
            }

            return chatData to messagesList

        } catch (e: Exception) {
            e.printStackTrace()
            return null to emptyList()
        }
    }

    @OptIn(UnstableApi::class)
    fun listenForMessages(chatId: String, onMessagesUpdated: (List<Message>) -> Unit) {
        firestore.collection("chats")
            .document(chatId)
            .collection("messages")
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e("ChatRepository", "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val updatedMessages = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(Message::class.java)
                    }
                    onMessagesUpdated(updatedMessages)
                }
            }
    }



}
