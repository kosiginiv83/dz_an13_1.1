package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl


private val empty = Post(
    id = 0,
    content = "",
    author = "Me",
    likedByMe = false,
    published = "Now",
    imgLink = null,
    likesCount = 0,
    sharesCount = 0,
    viewsCount = 0,
    videoLink = null,
    videoPreviewLink = null,
)


class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PostRepository = PostRepositoryImpl(
        AppDb.getInstance(context = application).postDao()
    )
    val data = repository.getAll()
    val edited = MutableLiveData(empty)

    fun getEmpty() = empty

    fun setEditedToEmpty() {
        edited.value = empty
    }

    fun getPostById(id: Long) = repository.getPostById(id)

    fun savePost() {
        edited.value?.let {
            repository.save(it)
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

    fun likeById(id: Long) = repository.likeById(id)
    fun shareById(id: Long) = repository.shareById(id)
    fun removeById(id: Long) = repository.removeById(id)
}