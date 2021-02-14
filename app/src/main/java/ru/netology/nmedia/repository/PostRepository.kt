package ru.netology.nmedia.repository

import ru.netology.nmedia.dto.Post


interface PostRepository {
    fun getAllAsync(callback: Callback<List<Post>>)
    fun getPostByIdAsync(id: Long, callback: Callback<Post>)
    fun likeUnlikeAsync(post: Post, callback: Callback<Post>)
    fun saveAsync(post: Post, callback: Callback<Post>)
    fun removeByIdAsync(id: Long, callback: Callback<Long>)

    interface Callback<T> {
        fun onSuccess(arg: T) {}
        fun onError(e: Exception) {}
    }
}
