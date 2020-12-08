package ru.netology.nmedia.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.Post


fun getFormattedNum(num: Int) : String = when(num) {
    in 0..999 -> num.toString()
    in 1_000..9_999 -> "${num / 1_000}.${num % 1_000 / 100}K"
    in 10_000..999_999 -> (num / 1_000).toString() + "K"
    in 1_000_000..Int.MAX_VALUE -> "${num / 1_000_000}.${num % 1_000_000 / 100_000}M"
    else -> throw IllegalArgumentException("Некорректное число")
}


interface OnInteractionListener {
    fun onLike(post: Post) {}
    fun onShare(post: Post) {}
    fun onEdit(post: Post) {}
    fun onRemove(post: Post) {}
    fun onVideoOpen(post: Post) {}
    fun onPostOpen(post: Post) {}
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
                PostDiffCallback.LIKED_BY_ME ->
                    holder.binding.btnLike.isChecked = post.likedByMe
                PostDiffCallback.LIKES_COUNT ->
                    holder.binding.btnLike.text = getFormattedNum(post.likesCount)
                PostDiffCallback.SHARES_COUNT ->
                    holder.binding.btnShare.text = getFormattedNum(post.sharesCount)
                PostDiffCallback.VIEWS_COUNT ->
                    holder.binding.viewsCount.text = getFormattedNum(post.viewsCount)
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
            author.text = post.author
            published.text = post.published
            content.text = post.content
            btnLike.isChecked = post.likedByMe
            btnLike.text = getFormattedNum(post.likesCount)
            btnShare.text = getFormattedNum(post.sharesCount)
            viewsCount.text = getFormattedNum(post.viewsCount)
            btnLike.setOnClickListener { onInteractionListener.onLike(post) }
            btnShare.setOnClickListener { onInteractionListener.onShare(post) }
            cardPost.setOnClickListener { onInteractionListener.onPostOpen(post) }
            postImg.setImageResource(post.imgLink ?: 0)

            if (post.videoLink != null) {
                videoBtn.setImageResource(post.videoPreviewLink ?: R.drawable.youtube_img)
                playBtn.visibility = if (post.videoPreviewLink != 0) View.VISIBLE else View.GONE
                playBtn.setOnClickListener { onInteractionListener.onVideoOpen(post) }
                videoBtn.setOnClickListener { onInteractionListener.onVideoOpen(post) }
            }

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
        const val SHARES_COUNT = "sharesCount"
        const val VIEWS_COUNT = "viewsCount"
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
        if (newItem.likesCount != oldItem.likesCount) set.add(LIKES_COUNT)
        if (newItem.sharesCount != oldItem.sharesCount) set.add(SHARES_COUNT)
        if (newItem.viewsCount != oldItem.viewsCount) set.add(VIEWS_COUNT)
        if (newItem.published != oldItem.published) set.add(PUBLISHED)
        if (newItem.content != oldItem.content) set.add(CONTENT)
        return set
    }
}
