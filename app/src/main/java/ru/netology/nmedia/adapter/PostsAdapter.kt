package ru.netology.nmedia.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.AttachmentType
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.PostRepositoryImpl
import ru.netology.nmedia.view.load
import ru.netology.nmedia.view.loadCircleCrop
import java.util.*


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
                PostDiffCallback.IMAGE ->
                    post.attachment?.url?.let { url -> holder.binding.postImg.load(url) }
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
            avatar.loadCircleCrop("${PostRepositoryImpl.BASE_URL}/avatars/${post.authorAvatar}")
            postImg.setImageDrawable(null)
            if (post.attachment != null && post.attachment.type == AttachmentType.IMAGE) {
                postImg.load("${PostRepositoryImpl.BASE_URL}/images/${post.attachment.url}")
            }
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
        const val IMAGE = "image"
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
        if (newItem.attachment?.url != oldItem.attachment?.url) set.add(IMAGE)
        return set
    }
}
