package ru.netology.nmedia.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import ru.netology.nmedia.dto.Post
import java.io.IOException
import java.util.concurrent.TimeUnit


class PostRepositoryImpl : PostRepository {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()
    private val gson = Gson()
    private val typeTokenList = object : TypeToken<List<Post>>() {}
    private val typeTokenPost = object : TypeToken<Post>() {}

    companion object {
//        private const val BASE_URL = "http://10.0.2.2:9999" // AVD
        private const val BASE_URL = "http://10.0.3.2:9999" // Genymotion
        private val jsonType = "application/json".toMediaType()
    }


    override fun getAllAsync(callback: PostRepository.GetAllCallback) {
        val request: Request = Request.Builder()
            .url("${BASE_URL}/api/slow/posts")
            .build()

        client.newCall(request)
            .enqueue(object: Callback {
                override fun onResponse(call: Call, response: Response) {
                    val body = response.body?.string() ?: throw RuntimeException("Body is null")
                    try {
                        callback.onSuccess(gson.fromJson(body, typeTokenList.type))
                    } catch (e: Exception) {
                        callback.onError(e)
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }
            })
    }


    override fun getPostByIdAsync(id: Long, callback: PostRepository.GetPostByIdCallback) {
        val request: Request = Request.Builder()
            .url("${BASE_URL}/api/slow/posts/$id")
            .build()

        client.newCall(request).enqueue(object: Callback {
            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string() ?: throw RuntimeException("Body is null")
                try {
                    callback.onSuccess(gson.fromJson(body, typeTokenPost.type))
                } catch (e: Exception) {
                    callback.onError(e)
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                callback.onError(e)
            }
        })
    }


    override fun likeUnlikeAsync(post: Post, callback: PostRepository.LikeUnlikeCallback) {
        val requestLike: Request = Request.Builder()
            .post("".toRequestBody())
            .url("${BASE_URL}/api/slow/posts/${post.id}/likes")
            .build()
        val requestUnlike: Request = Request.Builder()
            .delete()
            .url("${BASE_URL}/api/slow/posts/${post.id}/likes")
            .build()

        client.newCall( if (post.likedByMe) requestUnlike else requestLike )
            .enqueue (object: Callback {
                override fun onResponse(call: Call, response: Response) {
                    val body = response.body?.string() ?: throw RuntimeException("Body is null")
                    try {
                        callback.onSuccess(gson.fromJson(body, typeTokenPost.type))
                    } catch (e: Exception) {
                        callback.onError(e)
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }
            })
    }


    override fun removeByIdAsync(id: Long, callback: PostRepository.RemoveByIdCallback) {
        val request: Request = Request.Builder()
            .delete()
            .url("${BASE_URL}/api/slow/posts/$id")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (!response.body?.string().isNullOrEmpty()) {
                    callback.onError(RuntimeException("Body is not empty"))
                } else {
                    callback.onSuccess(id)
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                callback.onError(e)
            }
        })
    }


    override fun saveAsync(post: Post, callback: PostRepository.SaveCallback) {
        val request: Request = Request.Builder()
            .post(gson.toJson(post).toRequestBody(jsonType))
            .url("${BASE_URL}/api/slow/posts")
            .build()

        client.newCall(request).enqueue(object: Callback {
            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string() ?: throw RuntimeException("Body is null")
                try {
                    callback.onSuccess(gson.fromJson(body, typeTokenPost.type))
                } catch (e: Exception) {
                    callback.onError(e)
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                callback.onError(e)
            }
        })
    }
}