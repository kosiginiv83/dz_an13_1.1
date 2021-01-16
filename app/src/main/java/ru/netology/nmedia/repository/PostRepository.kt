package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import ru.netology.nmedia.dto.Post


interface PostRepository {
    fun getAll(): List<Post>
//    fun getPostById(id: Long): List<Post>
    fun likeById(id: Long)
    fun save(post: Post)
    //    fun shareById(id: Long)
    fun removeById(id: Long)
//    fun insertPost(post: Post)
}
