package ru.netology.nmedia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.utils.AndroidUtils
import ru.netology.nmedia.viewmodel.PostViewModel


class MainActivity : AppCompatActivity() {
    companion object Mode {
        const val EDIT = "editPost"
        const val NEW = "newPost"
    }
    private var mode = NEW

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModel: PostViewModel by viewModels()

        val postsAdapter = PostsAdapter(object : OnInteractionListener{
            override fun onLike(post: Post) {
                viewModel.likeById(post.id)
            }
            override fun onShare(post: Post) {
                viewModel.shareById(post.id)
            }
            override fun onRemove(post: Post) {
                viewModel.removeById(post.id)
            }
            override fun onEdit(post: Post) {
                mode = EDIT
                binding.editHint.visibility = View.VISIBLE
                binding.cancelButton.visibility = View.VISIBLE
                viewModel.editPost(post)
            }
        })

        binding.postsList.adapter = postsAdapter
        binding.postsList.layoutManager = LinearLayoutManager(this)
        viewModel.data.observe(this) { posts ->
            postsAdapter.submitList(posts)
        }

        viewModel.edited.observe(this) { post ->
            if (post.id == 0L) {
                return@observe
            }
            with (binding.editContent) {
                requestFocus()
                setText(post.content)
            }
        }

        fun exitEditMode() {
            mode = NEW
            binding.editHint.visibility = View.GONE
            binding.cancelButton.visibility = View.GONE
            with (binding.editContent) {
                setText("")
                clearFocus()
                AndroidUtils.hideKeyboard(this)
            }
        }

        binding.confirmButton.setOnClickListener {
            with(binding.editContent) {
                if (text.isNullOrBlank()) {
                    Toast.makeText(
                        this@MainActivity,
                        context.getString(R.string.error_empty_content),
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                viewModel.changePostContent(text.toString())
                viewModel.savePost()

                if (mode == NEW) {
                    binding.postsList.apply {
                        smoothScrollToPosition(0)
                        smoothScrollBy(0, 800) // smoothScrollToPosition(0) перематывает
                        // до низа поста, а не до верха
                    }
                }

                exitEditMode()
            }
        }

        binding.cancelButton.setOnClickListener {
            viewModel.setEditedPostToEmpty()
            exitEditMode()
        }
    }
}
