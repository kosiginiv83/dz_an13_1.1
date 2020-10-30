package ru.netology.nmedia.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.Post


interface OnInteractionListener {
    fun onLike(post: Post) {}
    fun onShare(post: Post) {}
}

class PostsAdapter(val onInteractionListener: OnInteractionListener)
            : RecyclerView.Adapter<PostViewHolder>() {
    var postsList = emptyList<Post>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding, onInteractionListener)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = postsList[position]
        holder.bind(post)
    }

    override fun getItemCount(): Int = postsList.size
}


class PostViewHolder(
    private val binding: CardPostBinding,
    private val onInteractionListener: OnInteractionListener
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(post: Post) {
        binding.apply {
            author.text = post.author
            published.text = post.published
            content.text = post.content
            btnLike.setImageResource(
                if (post.likedByMe) R.drawable.ic_heart_red else R.drawable.ic_heart2
            )
            likesCount.text = getFormattedNum(post.likesCount)
            sharesCount.text = getFormattedNum(post.sharesCount)
            viewsCount.text = getFormattedNum(post.viewsCount)
            btnLike.setOnClickListener { onInteractionListener.onLike(post) }
            btnShare.setOnClickListener { onInteractionListener.onShare(post) }
            postImg.setImageResource(post.imgLink)
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