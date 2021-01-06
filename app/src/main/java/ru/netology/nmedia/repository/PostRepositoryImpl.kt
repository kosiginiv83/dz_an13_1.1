package ru.netology.nmedia.repository

import androidx.lifecycle.Transformations
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity


class PostRepositoryImpl(
    private val dao: PostDao
) : PostRepository {

    override fun getPostById(id: Long) = Transformations.map(dao.getPostById(id)) { list ->
        list.map {
            Post(it.id, it.author, it.content, it.published, it.likedByMe, it.likesCount,
                it.sharesCount, it.viewsCount, it.imgLink, it.videoPreviewLink, it.videoLink)
        }
    }

    override fun getAll() = Transformations.map(dao.getAll()) { list ->
        list.map {
            Post(it.id, it.author, it.content, it.published, it.likedByMe, it.likesCount,
                it.sharesCount, it.viewsCount, it.imgLink, it.videoPreviewLink, it.videoLink)
        }
    }

    override fun likeById(id: Long) {
        dao.likeById(id)
    }

    override fun shareById(id: Long) {
        dao.shareById(id)
    }

    override fun removeById(id: Long) {
        dao.removeById(id)
    }

    override fun save(post: Post) {
        dao.save(PostEntity.fromDto(post))
    }

    override fun insertPost(post: Post) {
        dao.insert(PostEntity.fromDto(post))
    }
}