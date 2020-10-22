package ru.netology.nmedia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.viewmodel.PostViewModel


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModel: PostViewModel by viewModels()
        viewModel.data.observe(this, { post ->
            with(binding) {
                author.text = post.author
                published.text = post.published
                content.text = post.content
                btnLike.setImageResource(
                    if (post.likedByMe) R.drawable.ic_heart_red else R.drawable.ic_heart2
                )
                likesCount.text = getFormattedNum(post.likesCount)
                sharesCount.text = getFormattedNum(post.sharesCount)
                viewsCount.text = getFormattedNum(post.viewsCount)
            }
        })

        binding.btnLike.setOnClickListener {
            viewModel.like()
        }

        binding.btnShare.setOnClickListener {
            viewModel.share()
        }
    }

    fun getFormattedNum(num: Int) : String = when(num) {
        in 0..999 -> num.toString()
        in 1_000..9_999 -> "${num / 1_000}.${num % 1_000 / 100}K"
        in 10_000..999_999 -> (num / 1_000).toString() + "K"
        in 1_000_000..Int.MAX_VALUE -> "${num / 1_000_000}.${num % 1_000_000 / 100_000}M"
        else -> throw IllegalArgumentException("Некорректное число")
    }
}
