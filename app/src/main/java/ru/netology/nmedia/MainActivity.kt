package ru.netology.nmedia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.dto.Post


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val post = Post(
            id = 1,
            author = "Нетология. Меняем карьеру через образование",
            content = "13 октября стартует бесплатный марафон «Три навыка, которые нужны каждому бизнесмену». Если ваш бизнес стабильно приносит прибыль, пора переходить к его масштабированию. Расскажем, как настроить процессы, эффективно управлять финансовым потоком и где найти деньги на развитие. Вас ждут три эксперта-предпринимателя и три темы, в которых они разбираются лучше всего: привлечение инвестиций, управленческий учёт, выстраивание бизнес-процессов. Регистрируйтесь и начните развивать своё дело прямо сейчас → http://netolo.gy/fUp",
            published = "вчера в 10:24",
            likesCount = 9_999,
            sharesCount = 995,
            viewsCount = 1_200_000
        )

        with(binding) {
            author.text = post.author
            published.text = post.published
            content.text = post.content
            if (post.likedByMe) btnLike.setImageResource(R.drawable.ic_heart_red)
            likesCount.text = getFormatNum(post.likesCount)
            sharesCount.text = getFormatNum(post.sharesCount)
            viewsCount.text = getFormatNum(post.viewsCount)

            btnLike.setOnClickListener {
                post.likedByMe = !post.likedByMe
                btnLike.setImageResource(
                    if (post.likedByMe) {
                        post.likesCount++
                        R.drawable.ic_heart_red
                    } else {
                        post.likesCount--
                        R.drawable.ic_heart2
                    }
                )
                likesCount.text = getFormatNum(post.likesCount)
            }

            btnShare.setOnClickListener {
                post.sharesCount++
                sharesCount.text = getFormatNum(post.sharesCount)
            }
        }
    }

    fun getFormatNum(num: Int) : String {
        return when(num) {
            in 0..999 -> num.toString()
//            in 1_000..9_999 -> String.format("%.1f", num / 1_000.0) + "K" // format округляет
            in 1_000..9_999 -> "${num / 1_000}.${num % 1_000 / 100}K"
            in 10_000..999_999 -> (num / 1_000).toString() + "K"
            in 1_000_000..Int.MAX_VALUE -> "${num / 1_000_000}.${num % 1_000_000 / 100_000}M"
            else -> throw IllegalArgumentException("Некорректное число")
        }
    }
}
