package ru.netology.nmedia.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.dto.Post


@Entity
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val author: String,
    val content: String,
    val published: String,
    val likedByMe: Boolean = false,
    val likesCount: Int = 0,
    val sharesCount: Int = 0,
    val viewsCount: Int = 0,
    val imgLink: Int? = null,
    val videoPreviewLink: Int? = null,
    val videoLink: String? = null,
) {
    fun toDto() = Post(id, author, content, published, likedByMe, likesCount, sharesCount,
        viewsCount, imgLink, videoPreviewLink, videoLink)

    companion object {
        fun fromDto(dto: Post) =
            PostEntity(dto.id, dto.author, dto.content, dto.published, dto.likedByMe,
            dto.likesCount, dto.sharesCount, dto.viewsCount, dto.imgLink, dto.videoPreviewLink,
            dto.videoLink)
    }
}
