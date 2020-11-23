package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.R
import ru.netology.nmedia.dto.Post


interface PostRepository {
    fun getAll(): LiveData<List<Post>>
    fun likeById(id: Long)
    fun shareById(id: Long)
    fun removeById(id: Long)
    fun savePost(post: Post)
}


class PostRepositoryInMemoryImpl private constructor(): PostRepository {
    private object HOLDER {
        val INSTANCE = PostRepositoryInMemoryImpl()
    }
    companion object {
        val instance: PostRepositoryInMemoryImpl by lazy { HOLDER.INSTANCE }
    }

    private var posts = listOf(
        Post(
            id = 3,
            author = "Нетология. Меняем карьеру через образование",
            content = "учитесь и начните",
            published = "вчера в 10:48",
            likedByMe = false,
            likesCount = 9_999,
            sharesCount = 995,
            viewsCount = 1_200_000,
            imgLink = null
        ),
        Post(
            id = 2,
            author = "Нетология. Меняем карьеру через образование",
            content = "Работой на удалёнке уже никого не удивить: мифы о ноутбуке под пальмой и большом количестве свободного времени давно развеяны, ведь удалённая работа требует высокого уровня самоорганизованности и ответственности.\n" +
                    "\n" +
                    "Собрали подборку статей об удалёнке и фрилансе \uD83D\uDC47\n" +
                    "\n" +
                    "▪5 книг для тех, кто переходит на удалёнку → http://netolo.gy/fWN\n" +
                    "▪Честно об удалённой работе: личный опыт → http://netolo.gy/fWM\n" +
                    "▪Популярные ошибки фрилансеров, которые мешают получать заказы, и как их избежать → http://netolo.gy/fWO\n" +
                    "▪О сложностях и проблемах, с которыми можно столкнуться на фрилансе → http://netolo.gy/fWP\n" +
                    "▪Большая подборка статей об удалённой работе и цифровых профессиях → http://netolo.gy/fWQ",
            published = "сегодня в 10:49",
            likedByMe = false,
            likesCount = 99,
            sharesCount = 95,
            viewsCount = 1_000,
            imgLink = R.drawable.post_image_2
        ),
        Post(
            id = 1,
            author = "Нетология. Меняем карьеру через образование",
            content = "13 октября стартует бесплатный марафон «Три навыка, которые нужны каждому бизнесмену». Если ваш бизнес стабильно приносит прибыль, пора переходить к его масштабированию. Расскажем, как настроить процессы, эффективно управлять финансовым потоком и где найти деньги на развитие. Вас ждут три эксперта-предпринимателя и три темы, в которых они разбираются лучше всего: привлечение инвестиций, управленческий учёт, выстраивание бизнес-процессов. Регистрируйтесь и начните развивать своё дело прямо сейчас → http://netolo.gy/fUp",
            published = "вчера в 10:24",
            likedByMe = false,
            likesCount = 9_999,
            sharesCount = 995,
            viewsCount = 1_200_000,
            imgLink = R.drawable.post_image
        ),
    )

    private val data = MutableLiveData(posts)

    private var nextId = posts.size.toLong()

    override fun getAll(): LiveData<List<Post>> = data

    override fun savePost(post: Post) {
        if (post.id == 0L) {
            posts = listOf(
                post.copy(
                    id = ++nextId,
                    author = "Me",
                    published = "Now"
                )
            ) + posts
            data.value = posts
            return
        }

        posts = posts.map {
            if (it.id != post.id) it else it.copy(content = post.content)
        }
        data.value = posts
    }

    override fun likeById(id: Long) {
        posts = posts.map { post ->
            if (post.id != id) post else {
                val isLiked = !post.likedByMe
                val newLikesCount = if (isLiked) (post.likesCount + 1) else (post.likesCount - 1)
                post.copy(likedByMe = isLiked, likesCount = newLikesCount)
            }
        }
        data.value = posts
    }

    override fun shareById(id: Long) {
        posts = posts.map {
            if (it.id != id) it else it.copy(sharesCount = it.sharesCount + 1)
        }
        data.value = posts
    }

    override fun removeById(id: Long) {
        posts = posts.filter { it.id != id }
        data.value = posts
    }
}
