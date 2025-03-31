package com.forrestgump.ig.data.repositories

import com.forrestgump.ig.data.models.Post
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import javax.inject.Inject
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Singleton
class PostRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    // Lưu lại document cuối của trang hiện tại
    private var lastVisible: DocumentSnapshot? = null

    suspend fun getPosts(pageSize: Int): List<Post> = suspendCancellableCoroutine { cont ->
        var query: Query = firestore.collection("posts")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(pageSize.toLong())
        // Nếu đã có trang trước thì bắt đầu sau document cuối của trang trước
        if (lastVisible != null) {
            query = query.startAfter(lastVisible!!)
        }
        query.get()
            .addOnSuccessListener { snapshot ->
                if (snapshot != null && !snapshot.isEmpty) {
                    // Lưu lại document cuối cùng của trang hiện tại
                    lastVisible = snapshot.documents.last()
                    val posts = snapshot.toObjects(Post::class.java)
                    cont.resume(posts)
                } else {
                    cont.resume(emptyList())
                }
            }
            .addOnFailureListener { exception ->
                cont.resumeWithException(exception)
            }
    }
}