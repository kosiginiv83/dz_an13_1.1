package ru.netology.nmedia.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.activity.TAG
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
)


class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PostRepository = PostRepositoryImpl()
    val edited = MutableLiveData(empty)

    private val _data = MutableLiveData(FeedModel())
    val data: LiveData<FeedModel> get() = _data

    private val _singlePost = MutableLiveData(FeedModel())
    val singlePost: LiveData<FeedModel> get() = _singlePost

    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit> get() = _postCreated

    private val _singlePostUpdated = SingleLiveEvent<Unit>()
    val singlePostUpdated: LiveData<Unit> get() = _singlePostUpdated

    val isLikeUnlikePending: MutableLiveData<MutableList<Long>> by lazy { MutableLiveData() }
    val isContentChangedPending: MutableLiveData<MutableList<Long>> by lazy { MutableLiveData() }

    init {
        isLikeUnlikePending.value = mutableListOf()
        isContentChangedPending.value = mutableListOf()
        loadPosts()
    }


    fun editPost(post: Post) {
        edited.value = post
    }

    fun getEmpty() = empty

    fun setEditedToEmpty() {
        edited.value = empty
    }

    fun setSinglePostToEmpty() {
        _singlePost.value = FeedModel(emptyList(), refreshing = true)
    }


    private fun removeIdFromList() {
        if ( isLikeUnlikePending.value?.isNotEmpty()!! ) {
            val likedUnlikedPostIds = isLikeUnlikePending.value
                ?.takeWhile { it != edited.value?.id }
            isLikeUnlikePending.postValue(likedUnlikedPostIds?.toMutableList())
        }

        if ( isContentChangedPending.value?.isNotEmpty()!! ) {
            val contentChangedPostIds = isContentChangedPending.value
                ?.takeWhile { it != edited.value?.id }
            isContentChangedPending.postValue(contentChangedPostIds?.toMutableList())
        }
    }


    fun loadPosts() {
        thread {
            try {
                val posts = repository.getAll()
                removeIdFromList()
                FeedModel(posts = posts, empty = posts.isEmpty(), idle = true)
            } catch (e: IOException) {
                FeedModel(error = true)
            }.also(_data::postValue)
        }
    }


    fun getPostById(id: Long) {
        thread {
            try {
                val post = repository.getPostById(id)
                removeIdFromList()
                FeedModel(posts = listOf(post))
            } catch (e: IOException) {
                FeedModel(error = true)
            }.also(_singlePost::postValue)
        }
    }


    fun savePost() {
        edited.value?.let {
            thread {
                try {
                    repository.save(it)
                    _postCreated.postValue(Unit)
                    _singlePostUpdated.postValue(Unit)
                } catch (e: IOException) {
                    FeedModel(error = true)
                } finally {
                    isContentChangedPending.value?.remove(it.id)
                }
            }
        }
        edited.value = empty
    }


    fun changePostContent(content: String) {
        val text = content.trim()
        if (edited.value?.content == text) {
            return
        }
        edited.value = edited.value?.copy(content = text)
    }


    fun likeUnlike(post: Post) {
        edited.value = edited.value?.copy(likedByMe = !post.likedByMe)
        thread {
            try {
                repository.likeUnlike(post)
                _postCreated.postValue(Unit)
                _singlePostUpdated.postValue(Unit)
            } catch (e: IOException) {
                FeedModel(error = true)
            } finally {
                isLikeUnlikePending.value?.remove(post.id)
            }
        }.also { edited.value = empty }
    }


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

}