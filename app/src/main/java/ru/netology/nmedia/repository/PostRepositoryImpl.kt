package ru.netology.nmedia.repository

import androidx.lifecycle.Transformations
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity


class PostRepositoryImpl(
    private val dao: PostDao
) : PostRepository {
//    private var posts = emptyList<Post>()
//    private val data = MutableLiveData(posts)
//
//    init {
//        posts = dao.getAll()
//        data.value = posts
//    }

    override fun getAll() = Transformations.map(dao.getAll()) { list ->
        list.map {
            Post(it.id, it.author, it.content, it.published, it.likedByMe, it.likesCount,
                it.sharesCount, it.viewsCount, it.imgLink, it.videoPreviewLink, it.videoLink)
        }
    }

    override fun likeById(id: Long) {
        dao.likeById(id)
//        posts = posts.map {
//            if (it.id != id) it else it.copy(
//                likedByMe = !it.likedByMe,
//                likesCount = if (it.likedByMe) it.likesCount - 1 else it.likesCount + 1
//            )
//        }
//        data.value = posts
    }

    override fun shareById(id: Long) {
        dao.shareById(id)
//        posts = posts.map {
//            if (it.id != id) it else it.copy(
//                sharesCount = it.sharesCount + 1
//            )
//        }
//        data.value = posts
    }

    override fun removeById(id: Long) {
        dao.removeById(id)
//        posts = posts.filter { it.id != id }
//        data.value = posts
    }

    override fun save(post: Post) {
        dao.save(PostEntity.fromDto(post))
//        val id = post.id
//        val saved = dao.save(post)
//        posts = if (id == 0L) {
//            listOf(saved) + posts
//        } else {
//            posts.map {
//                if (it.id != id) it else saved
//            }
//        }
//        data.value = posts
    }
}