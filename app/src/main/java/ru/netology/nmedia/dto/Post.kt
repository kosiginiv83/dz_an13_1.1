package ru.netology.nmedia.dto


data class Post (
    val id: Long,
    val author: String,
    val content: String,
    val published: String,
    val likedByMe: Boolean = false,
    val likesCount: Int = 0 ,
    val sharesCount: Int = 0,
    val viewsCount: Int = 0,
    val imgLink: Int? = null,
    val videoPreviewLink: Int? = null,
    val videoLink: String? = null,
)
