package ru.netology.nmedia.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.netology.nmedia.dto.Post
import java.io.IOException


interface PostRepository {
    fun getAll(): LiveData<List<Post>>
    fun likeById(id: Long)
    fun shareById(id: Long)
    fun removeById(id: Long)
    fun savePost(post: Post)
}


class PostRepositoryFileImpl(
    private val context: Context,
): PostRepository {
    companion object {
        @Volatile private var INSTANCE: PostRepositoryFileImpl? = null
        fun getInstance(context: Context): PostRepositoryFileImpl =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: PostRepositoryFileImpl(context).also { INSTANCE = it }
            }
    }

    private val gson = Gson()
    private val type = TypeToken.getParameterized(List::class.java, Post::class.java).type
    private val filename = "posts.json"
    private val filenameAssets = "posts.json"
    private var posts = emptyList<Post>()
    private val data = MutableLiveData(posts)

    private val prefs = context.getSharedPreferences("repo", Context.MODE_PRIVATE)
    private val key = "nextId"
    private var nextId = 1L

    init {
        if (context.filesDir.resolve(filename).exists()) {
            context.openFileInput(filename).bufferedReader().use {
                posts = gson.fromJson(it, type)
                data.value = posts
            }
        } else {
            try {
                context.assets.open(filenameAssets).bufferedReader().use {
                    posts = gson.fromJson(it, type)
                    data.value = posts
                }
            } catch (error: IOException) {
                sync()
            }
        }
        prefs.getLong(key, posts.maxOfOrNull{ it.id } ?: nextId).let {
            nextId = it
        }
    }

    private fun sync() {
        context.openFileOutput(filename, Context.MODE_PRIVATE).bufferedWriter().use {
            it.write(gson.toJson(posts))
        }
        with (prefs.edit()) {
            putLong(key, nextId)
            apply()
        }
    }

    override fun getAll(): LiveData<List<Post>> = data

    override fun savePost(post: Post) {
        if (post.id == 0L) {
            posts = listOf(
                post.copy(
                    id = ++nextId,
                    author = "Me",
                    published = "Now"
                )
            ) + posts
            data.value = posts
            sync()
            return
        }

        posts = posts.map {
            if (it.id != post.id) it else it.copy(content = post.content)
        }
        data.value = posts
        sync()
    }

    override fun likeById(id: Long) {
        posts = posts.map { post ->
            if (post.id != id) post else {
                val isLiked = !post.likedByMe
                val newLikesCount = if (isLiked) (post.likesCount + 1) else (post.likesCount - 1)
                post.copy(likedByMe = isLiked, likesCount = newLikesCount)
            }
        }
        data.value = posts
        sync()
    }

    override fun shareById(id: Long) {
        posts = posts.map {
            if (it.id != id) it else it.copy(sharesCount = it.sharesCount + 1)
        }
        data.value = posts
        sync()
    }

    override fun removeById(id: Long) {
        posts = posts.filter { it.id != id }
        data.value = posts
        sync()
    }
}
