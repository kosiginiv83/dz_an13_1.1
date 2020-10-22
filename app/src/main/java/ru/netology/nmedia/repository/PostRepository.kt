package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.dto.Post


interface PostRepository {
    fun get(): LiveData<Post>
    fun like()
    fun share()
}


class PostRepositoryInMemoryImpl : PostRepository {
    private var post = Post(
        id = 1,
        author = "Нетология. Меняем карьеру через образование",
        content = "13 октября стартует бесплатный марафон «Три навыка, которые нужны каждому бизнесмену». Если ваш бизнес стабильно приносит прибыль, пора переходить к его масштабированию. Расскажем, как настроить процессы, эффективно управлять финансовым потоком и где найти деньги на развитие. Вас ждут три эксперта-предпринимателя и три темы, в которых они разбираются лучше всего: привлечение инвестиций, управленческий учёт, выстраивание бизнес-процессов. Регистрируйтесь и начните развивать своё дело прямо сейчас → http://netolo.gy/fUp",
        published = "вчера в 10:24",
        likedByMe = false,
        likesCount = 9_999,
        sharesCount = 995,
        viewsCount = 1_200_000
    )

    private val data = MutableLiveData(post)

    override fun get(): LiveData<Post> = data

    override fun like() {
        val isLiked = !post.likedByMe
        val newLikesCount = if (isLiked) (post.likesCount + 1) else (post.likesCount - 1)
        post = post.copy(likedByMe = isLiked, likesCount = newLikesCount)
        data.value = post
    }

    override fun share() {
        post = post.copy(sharesCount = post.sharesCount + 1)
        data.value = post
    }
}