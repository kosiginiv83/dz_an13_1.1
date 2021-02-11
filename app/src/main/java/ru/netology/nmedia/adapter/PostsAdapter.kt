package ru.netology.nmedia.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.Post
import java.util.Date
import com.bumptech.glide.Glide
import ru.netology.nmedia.activity.TAG
import ru.netology.nmedia.repository.PostRepositoryImpl


fun getFormattedNum(num: Int) : String = when(num) {
    in 0..999 -> num.toString()
    in 1_000..9_999 -> "${num / 1_000}.${num % 1_000 / 100}K"
    in 10_000..999_999 -> (num / 1_000).toString() + "K"
    in 1_000_000..Int.MAX_VALUE -> "${num / 1_000_000}.${num % 1_000_000 / 100_000}M"
    else -> "0"
}

fun getFormattedDate(epoch: String) : String = try {
    Date(epoch.toLong() * 1000).toString()
//    Instant.ofEpochSecond(epoch.toLong()).atZone(ZoneId.systemDefault()).toLocalDateTime().toString()
} catch (e: Exception) {
    ""
}

fun setAvatar(view: ImageView, fileName: String) {
    val url = PostRepositoryImpl.BASE_URL + "/avatars/" + fileName
    Glide.with(view)
        .load(url)
        .circleCrop()
        .placeholder(R.drawable.ic_loading_100dp)
        .error(R.drawable.ic_error_100dp)
        .timeout(10_000)
        .into(view)
}

fun setImage(view: ImageView, fileName: String?) {
    val url = PostRepositoryImpl.BASE_URL + "/images/" + fileName
    Glide.with(view)
        .load(url)
        .timeout(10_000)
        .into(view)
}


class PostsAdapter(
    private val onInteractionListener: OnInteractionListener
) : ListAdapter<Post, PostViewHolder>(PostDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding, onInteractionListener)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) this.onBindViewHolder(holder, position)
        val post = getItem(position)
        payloads.forEach {
            when (it) {
                PostDiffCallback.LIKED_BY_ME -> {
                    holder.binding.btnLike.isChecked = post.likedByMe
                }
                PostDiffCallback.LIKES_COUNT -> {
                    holder.binding.btnLike.text = getFormattedNum(post.likes)
                }
                PostDiffCallback.CONTENT -> {
                    holder.binding.content.text = post.content
                }
                PostDiffCallback.PUBLISHED ->
                    holder.binding.published.text = post.published
                else -> this.onBindViewHolder(holder, position)
            }
        }
    }
}


class PostViewHolder(
    val binding: CardPostBinding,
    private val onInteractionListener: OnInteractionListener
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(post: Post) {
        binding.apply {
            setAvatar(avatar, post.authorAvatar)
            if (post.attachment != null) setImage(postImg, post.attachment.url)
            author.text = post.author
            published.text = getFormattedDate(post.published)
            content.text = post.content
            btnLike.isChecked = post.likedByMe
            btnLike.setIconResource (
                if (post.likedByMe) R.drawable.ic_heart_red else R.drawable.ic_heart2
            )
            btnLike.setIconTintResource(
                if (post.likedByMe) R.color.red else R.color.grey
            )
            btnLike.text = getFormattedNum(post.likes)
            btnLike.setOnClickListener { onInteractionListener.onLike(post) }
            btnShare.setOnClickListener { onInteractionListener.onShare(post) }
            cardPost.setOnClickListener { onInteractionListener.onPostOpen(post) }
            content.setOnClickListener { onInteractionListener.onPostOpen(post) }
            postImg.setOnClickListener { onInteractionListener.onPostOpen(post) }

            menuButton.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.popup_menu)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.removePost -> {
                                onInteractionListener.onRemove(post)
                                true
                            }
                            R.id.editPost -> {
                                onInteractionListener.onEdit(post)
                                true
                            }
                            else -> false
                        }
                    }
                }.show()
            }
        }
    }
}


class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
    companion object {
        const val LIKED_BY_ME = "likedByMe"
        const val LIKES_COUNT = "likesCount"
        const val PUBLISHED = "published"
        const val CONTENT = "content"
    }

    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem == newItem
    }

    override fun getChangePayload(oldItem: Post, newItem: Post): Any? {
        val set = mutableSetOf<String>()
        if (newItem.likedByMe != oldItem.likedByMe) set.add(LIKED_BY_ME)
        if (newItem.likes != oldItem.likes) set.add(LIKES_COUNT)
        if (newItem.published != oldItem.published) set.add(PUBLISHED)
        if (newItem.content != oldItem.content) set.add(CONTENT)
        return set
    }
}
