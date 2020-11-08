package ru.netology.nmedia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.posts_list.*
import ru.netology.nmedia.adapter.FooterAdapter
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

//        binding.postsList.adapter = postsAdapter

        val footerAdapter = FooterAdapter()
        val concatAdapter = ConcatAdapter(postsAdapter, footerAdapter)
        binding.mainRecyclView.layoutManager = LinearLayoutManager(this)

        binding.mainRecyclView.adapter = concatAdapter
        
        viewModel.data.observe(this) { posts ->
            postsAdapter.submitList(posts)
        }
    }
}
