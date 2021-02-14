package ru.netology.nmedia.repository

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.netology.nmedia.activity.TAG
import ru.netology.nmedia.api.PostsApi
import ru.netology.nmedia.dto.Post
import java.io.EOFException


class PostRepositoryImpl : PostRepository {

    companion object {
        const val BASE_URL = "http://10.0.2.2:9999" // AVD
//        const val BASE_URL = "http://10.0.3.2:9999" // Genymotion
    }


    private fun <T> getCallback(callback: PostRepository.Callback<T>) =
        object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                if (!response.isSuccessful) {
                    callback.onError(RuntimeException(response.message()))
                    return
                }
                callback.onSuccess(response.body() ?: throw RuntimeException("Body is null"))
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                callback.onError(RuntimeException(t.message))
            }
        }


    override fun getAllAsync(callback: PostRepository.Callback<List<Post>>) {
        PostsApi.retrofitService.getAll().enqueue(getCallback(callback))
    }


    override fun getPostByIdAsync(id: Long, callback: PostRepository.Callback<Post>) {
        PostsApi.retrofitService.getById(id).enqueue(getCallback(callback))
    }


    override fun likeUnlikeAsync(post: Post, callback: PostRepository.Callback<Post>) {
        if (post.likedByMe) {
            PostsApi.retrofitService.dislikeById(post.id).enqueue(getCallback(callback))
        } else {
            PostsApi.retrofitService.likeById(post.id).enqueue(getCallback(callback))
        }
    }


    override fun saveAsync(post: Post, callback: PostRepository.Callback<Post>) {
        PostsApi.retrofitService.save(post).enqueue(getCallback(callback))
    }


    override fun removeByIdAsync(id: Long, callback: PostRepository.Callback<Long>) {
        PostsApi.retrofitService.removeById(id).enqueue(object : Callback<Long> {
            override fun onResponse(call: Call<Long>, response: Response<Long>) {
                if (!response.isSuccessful) {
                    callback.onError(RuntimeException(response.message()))
                    return
                }
                if (response.code() != 200) {
                    callback.onError(RuntimeException("Body is not null"))
                } else {
                    callback.onSuccess(id)
                }
            }

            override fun onFailure(call: Call<Long>, t: Throwable) {
                if (t is EOFException) {
                    callback.onSuccess(0L)
                } else {
                    callback.onError(RuntimeException(t.message))
                }
            }
        })
    }
}