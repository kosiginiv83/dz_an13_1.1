package ru.netology.nmedia.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
//import androidx.lifecycle.Transformations
//import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Post
//import ru.netology.nmedia.entity.PostEntity
import java.util.concurrent.TimeUnit


class PostRepositoryImpl : PostRepository {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()
    private val gson = Gson()
    private val typeToken = object : TypeToken<List<Post>>() {}

    companion object {
        private const val BASE_URL = "http://10.0.2.2:9999" // AVD
//        private const val BASE_URL = "http://10.0.3.2:9999" // Genymotion
        private val jsonType = "application/json".toMediaType()
    }


    override fun getAll(): List<Post> {
        val request: Request = Request.Builder()
            .url("${BASE_URL}/api/slow/posts")
            .build()

        return client.newCall(request)
            .execute()
            .use { it.body?.string() }
            .let { gson.fromJson(it, typeToken.type) }
    }


    override fun getPostById(id: Long): List<Post> {
        val request: Request = Request.Builder()
            .url("${BASE_URL}/api/slow/posts/$id")
            .build()

        return client.newCall(request)
            .execute()
            .use { it.body?.string() }
            .let { gson.fromJson(it, typeToken.type) }
    }


    override fun likeById(id: Long) {
//        dao.likeById(id)

    }


//    override fun shareById(id: Long) {
//        dao.shareById(id)
//    }


    override fun removeById(id: Long) {
//        dao.removeById(id)
        val request: Request = Request.Builder()
            .delete()
            .url("${BASE_URL}/api/slow/posts/$id")
            .build()

        client.newCall(request)
            .execute()
    }


    override fun save(post: Post) {
//        dao.save(PostEntity.fromDto(post))
        val request: Request = Request.Builder()
            .post(gson.toJson(post).toRequestBody(jsonType))
            .url("${BASE_URL}/api/slow/posts")
            .build()

        client.newCall(request)
            .execute()
    }


//    override fun insertPost(post: Post) {
//        dao.insert(PostEntity.fromDto(post))
//    }
}