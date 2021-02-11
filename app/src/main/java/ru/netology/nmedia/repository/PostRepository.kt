package ru.netology.nmedia.repository

import ru.netology.nmedia.dto.Post


interface PostRepository {
    fun getAllAsync(callback: GetAllCallback)
    fun getPostByIdAsync(id: Long, callback: GetPostByIdCallback)
    fun likeUnlikeAsync(post: Post, callback: LikeUnlikeCallback)
    fun saveAsync(post: Post, callback: SaveCallback)
    fun removeByIdAsync(id: Long, callback: RemoveByIdCallback)

    interface GetAllCallback {
        fun onSuccess(posts: List<Post>) {}
        fun onError(e: Exception) {}
    }

    interface GetPostByIdCallback {
        fun onSuccess(post: Post) {}
        fun onError(e: Exception) {}
    }

    interface LikeUnlikeCallback {
        fun onSuccess(post: Post) {}
        fun onError(e: Exception) {}
    }

    interface SaveCallback {
        fun onSuccess(post: Post) {}
        fun onError(e: Exception) {}
    }

    interface RemoveByIdCallback {
        fun onSuccess(id: Long) {}
        fun onError(e: Exception) {}
    }
}
