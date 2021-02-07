package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.*
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.repository.*
import ru.netology.nmedia.utils.SingleLiveEvent

private val POST_VIEW_MODEL_TAG = PostViewModel::class.java.simpleName


private val empty = Post(
    id = 0,
    author = "",
    authorAvatar = "",
    content = "",
    published = "",
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
            repository.getAllAsync(object : PostRepository.GetAllCallback {
                override fun onSuccess(posts: List<Post>) {
                    removeIdFromList()
                    _data.postValue(FeedModel(posts = posts, empty = posts.isEmpty(), idle = true))
                }

                override fun onError(e: Exception) {
                    _data.postValue(FeedModel(error = true))
                }
            })
    }


    fun getPostById(id: Long) {
        repository.getPostByIdAsync(id, object : PostRepository.GetPostByIdCallback {
            override fun onSuccess(post: Post) {
                removeIdFromList()
                _singlePost.postValue(FeedModel(posts = listOf(post)))
            }

            override fun onError(e: Exception) {
                _singlePost.postValue(FeedModel(error = true))
            }
        })
    }


    fun savePost() {
        edited.value?.let {
            repository.saveAsync(it, object : PostRepository.SaveCallback {
                override fun onSuccess(post: Post) {
                    _postCreated.postValue(Unit)
                    _singlePostUpdated.postValue(Unit)
                }

                override fun onError(e: Exception) {
                    _data.postValue(FeedModel(error = true))
                }
            })

            isContentChangedPending.value?.remove(it.id)
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

        repository.likeUnlikeAsync(post, object : PostRepository.LikeUnlikeCallback {
            override fun onSuccess(post: Post) {
                isLikeUnlikePending.value?.remove(post.id)
                edited.postValue(empty)
                _postCreated.postValue(Unit)
                _singlePostUpdated.postValue(Unit)
            }

            override fun onError(e: Exception) {
                isLikeUnlikePending.value?.remove(post.id)
                edited.postValue(empty)
                _data.postValue(FeedModel(error = true))
            }
        })
    }


    fun removeById(id: Long) {
        val old = _data.value?.posts.orEmpty()
        _data.postValue(
            _data.value?.copy(posts = _data.value?.posts.orEmpty()
                .filter { it.id != id })
        )

        repository.removeByIdAsync(id, object : PostRepository.RemoveByIdCallback {
            override fun onSuccess(id: Long) {}

            override fun onError(e: Exception) {
                _data.postValue(_data.value?.copy(posts = old))
            }
        })
    }

}