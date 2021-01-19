package ru.netology.nmedia.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.repository.*
import ru.netology.nmedia.utils.SingleLiveEvent
import java.io.IOException
import kotlin.concurrent.thread

private val POST_VIEW_MODEL_TAG = PostViewModel::class.java.simpleName


private val empty = Post(
    id = 0,
    author = "Me",
    content = "",
    published = "0",
    likedByMe = false,
    likes = 0,
//    sharesCount = 0,
)


class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PostRepository = PostRepositoryImpl()
    val edited = MutableLiveData(empty)

    private val _data = MutableLiveData(FeedModel())
    val data: LiveData<FeedModel> get() = _data

    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit> get() = _postCreated

    init {
        loadPosts()
    }

    fun loadPosts() {
        thread {
            _data.postValue(FeedModel(loading = true))
            try {
                val posts = repository.getAll()
                FeedModel(posts = posts, empty = posts.isEmpty())
            } catch (e: IOException) {
                FeedModel(error = true)
            }.also(_data::postValue)
        }
    }


    fun getEmpty() = empty

    fun setEditedToEmpty() {
        edited.value = empty
    }

    fun getPostById(id: Long) = repository.getPostById(id)

//    fun insertPost(post: Post) {
//        repository.insertPost(post)
//    }

    fun savePost() {
        edited.value?.let {
            thread {
                repository.save(it)
                _postCreated.postValue(Unit)
            }
        }
        edited.value = empty
    }

    fun editPost(post: Post) {
        edited.value = post
    }

    fun changePostContent(content: String) {
        val text = content.trim()
        if (edited.value?.content == text) {
            return
        }
        edited.value = edited.value?.copy(content = text)
    }

//    fun likeById(id: Long) = repository.likeById(id)
    fun likeById(id: Long) {
        thread { repository.likeById(id) }
    }

//    fun shareById(id: Long) = repository.shareById(id)

//    fun removeById(id: Long) = repository.removeById(id)
    fun removeById(id: Long) {
        thread {
            val old = _data.value?.posts.orEmpty()
            _data.postValue(
                _data.value?.copy(posts = _data.value?.posts.orEmpty()
                    .filter { it.id != id })
            )
            try {
                repository.removeById(id)
            } catch (e: IOException) {
                _data.postValue(_data.value?.copy(posts = old))
            }
        }
    }

//    fun getPostsFromAsset(context: Context) {
//        val gson = Gson()
//        val type = TypeToken.getParameterized(List::class.java, Post::class.java).type
//        val filenameAssets = "posts.json"
//        try {
//            var posts: List<Post>
//            context.assets.open(filenameAssets).bufferedReader().use {
//                posts = gson.fromJson(it, type)
//            }
//            posts.map { insertPost(it) }
//        } catch (error: Exception) {
//            Log.e(POST_VIEW_MODEL_TAG, "getPostsFromAsset Exception", error)
//            return
//        }
//    }
}