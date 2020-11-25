package ru.netology.nmedia.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewmodel.PostViewModel


class MainActivity : AppCompatActivity() {
    private val newPostRequestCode = 1
    private val postEditRequestCode = 2

    val viewModel: PostViewModel by viewModels()
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

//    override fun onStop() {
//        super.onStop()
//        Snackbar.make(binding.root, "MainActivity onStop event", Snackbar.LENGTH_LONG).show()
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val postsAdapter = PostsAdapter(object : OnInteractionListener {
            override fun onShare(post: Post) {
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
                            Intent.createChooser(it, "Show text").also(::startActivity)
                        }
                    }
                viewModel.shareById(post.id)
            }

            override fun onEdit(post: Post) {
                viewModel.editPost(post)
                val intent = Intent(this@MainActivity, NewPostActivity::class.java)
                    .putExtra(Intent.EXTRA_TEXT, post.content)
                    .setType("text/plain")
                startActivityForResult(intent, postEditRequestCode)
            }

            override fun onVideoOpen(post: Post) {
                Intent(Intent.ACTION_VIEW, Uri.parse(post.videoLink)).also(::startActivity)
            }

            override fun onLike(post: Post) {
                viewModel.likeById(post.id)
            }

            override fun onRemove(post: Post) {
                viewModel.removeById(post.id)
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

        intent?.apply {
            if (resolveActivity(packageManager) != null && action == Intent.ACTION_SEND) {
                onActivityResult(newPostRequestCode, Activity.RESULT_OK, this)
                finish()
            }
        }

        binding.addPostFab.setOnClickListener {
            val intent = Intent(this@MainActivity, NewPostActivity::class.java)
            startActivityForResult(intent, newPostRequestCode)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) {
//            Snackbar.make(binding.root, "Result not OK", Snackbar.LENGTH_LONG).show()
            return
        }

        when (requestCode) {
            newPostRequestCode -> {
                data?.getStringExtra(Intent.EXTRA_TEXT)?.let {
                    viewModel.changePostContent(it)
                    viewModel.savePost()
                }
//                Snackbar.make(binding.root, "newPostRequestCode", Snackbar.LENGTH_LONG).show()
            }
            postEditRequestCode -> {
                data?.getStringExtra(Intent.EXTRA_TEXT)?.let {
                    viewModel.changePostContent(it)
                    viewModel.savePost()
                }
//                Snackbar.make(binding.root, "postEditRequestCode", Snackbar.LENGTH_LONG).show()
                // На данный момент код для разных режимов идентичен.
                // Нужен лишь для того, чтобы показать, что я понял для чего RequestCode
            }
        }

        binding.postsList.apply {
            layoutManager?.smoothScrollToPosition(this, RecyclerView.State(), 0)
            // Перемотка идет до низа поста, а не до верха
        }
    }
}
