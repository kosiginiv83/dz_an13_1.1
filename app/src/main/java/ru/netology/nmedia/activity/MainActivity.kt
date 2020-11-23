package ru.netology.nmedia.activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.utils.AndroidUtils
import ru.netology.nmedia.viewmodel.PostViewModel


class MainActivity : AppCompatActivity() {
    private val newPostRequestCode = 1
    val viewModel: PostViewModel by viewModels()
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

//    companion object Mode {
//        const val EDIT = "editPost"
//        const val NEW = "newPost"
//    }
//    private var mode = NEW

    override fun onStop() {
        super.onStop()
//        Snackbar.make(binding.root, "Main Activity onStop event", Snackbar.LENGTH_LONG).show()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val postsAdapter = PostsAdapter(object : OnInteractionListener{
            override fun onLike(post: Post) {
                viewModel.likeById(post.id)
            }
            override fun onShare(post: Post) {
//                val intent = Intent().apply {
//                    action = Intent.ACTION_SEND
//                    putExtra(Intent.EXTRA_TEXT, post.content)
//                    type = "text/plain"
//                }
//                val shareIntent = Intent.createChooser(intent, getString(R.string.chooser_share_post))
//                startActivity(shareIntent)
                Intent(Intent.ACTION_SEND)
                    .putExtra(Intent.EXTRA_TEXT, post.content)
                    .setType("text/plain")
                    .also {
                        if (it.resolveActivity(packageManager) == null) {
                            Toast.makeText(
                                this@MainActivity,
                                "App not found",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
//                            startActivity(it)
                            Intent.createChooser(it, "Show text").also(::startActivity)
                        }
                    }
                viewModel.shareById(post.id)
            }
            override fun onRemove(post: Post) {
                viewModel.removeById(post.id)
            }
            override fun onEdit(post: Post) {
//                mode = EDIT
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
        }

//        fun exitEditMode() {
//            mode = NEW
//            binding.editHint.visibility = View.GONE
//            binding.cancelButton.visibility = View.GONE
//            with (binding.editContent) {
//                setText("")
//                clearFocus()
//                AndroidUtils.hideKeyboard(this)
//            }
//        }


//        intent?.let {
//            if (it.action != Intent.ACTION_SEND) {
//                Snackbar.make(binding.root, "Not Send Action", Snackbar.LENGTH_LONG).show()
//                return@let
//            }
//
//            val text = it.getStringExtra(Intent.EXTRA_TEXT)
//            if (text.isNullOrBlank()) {
//                Snackbar.make(binding.root, R.string.no_message,
//                    BaseTransientBottomBar.LENGTH_INDEFINITE
//                )
//                    .setAction(android.R.string.ok) {
//                        finish()
//                    }
//                    .show()
//                return@let
//            }
//
//            viewModel.changePostContent(text, false)
//            viewModel.savePost()
//            binding.postsList.smoothScrollToPosition(0)
//        }

        intent?.apply {
            if (resolveActivity(packageManager) != null && action == Intent.ACTION_SEND) {
                onActivityResult(1, Activity.RESULT_OK, this)
                finish()
            }
        }


        binding.addPostFab.setOnClickListener {
            val intent = Intent(this@MainActivity, NewPostActivity::class.java)
            startActivityForResult(intent, newPostRequestCode)
        }

//        binding.confirmButton.setOnClickListener {
//            with(binding.editContent) {
//                if (text.isNullOrBlank()) {
//                    Toast.makeText(
//                        this@MainActivity,
//                        context.getString(R.string.error_empty_content),
//                        Toast.LENGTH_SHORT
//                    ).show()
//                    return@setOnClickListener
//                }
//
//                viewModel.changePostContent(text.toString(), false)
//                viewModel.savePost()
//
//                if (mode == NEW) {
//                    binding.postsList.apply {
//                        smoothScrollToPosition(0)
//                        smoothScrollBy(0, 800) // smoothScrollToPosition(0) перематывает
//                        // до низа поста, а не до верха (а иногда перематывает, рандом)
//                    }
//                }
//
//                exitEditMode()
//            }
//        }
//
//        binding.cancelButton.setOnClickListener {
//            viewModel.setEditedPostToEmpty()
//            exitEditMode()
//        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            newPostRequestCode -> {
                if (resultCode != Activity.RESULT_OK) {
                    Snackbar.make(binding.root, "Result not OK", Snackbar.LENGTH_LONG).show()
                    return
                }
                data?.getStringExtra(Intent.EXTRA_TEXT)?.let {
                    viewModel.changePostContent(it, false)
                    viewModel.savePost()
                    binding.postsList.smoothScrollToPosition(0)
                }
            }
        }
    }
}
