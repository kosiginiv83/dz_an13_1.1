package ru.netology.nmedia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewmodel.PostViewModel


class MainActivity : AppCompatActivity() {
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
        })

        binding.postsList.adapter = postsAdapter
        binding.postsList.layoutManager = LinearLayoutManager(this)

//        val footerAdapter = FooterAdapter()
//        val concatAdapter = ConcatAdapter(postsAdapter, footerAdapter)
//
//        binding.mainRecyclView.adapter = concatAdapter

        viewModel.data.observe(this) { posts ->
            postsAdapter.submitList(posts)
        }
    }
}
