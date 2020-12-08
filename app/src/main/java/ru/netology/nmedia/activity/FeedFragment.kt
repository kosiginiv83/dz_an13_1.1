package ru.netology.nmedia.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
//import androidx.activity.viewModels
//import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.NewPostFragment.Companion.isShared
import ru.netology.nmedia.activity.NewPostFragment.Companion.postId
import ru.netology.nmedia.activity.NewPostFragment.Companion.textArg
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.CardPostBinding
//import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewmodel.PostViewModel


open class FeedFragment : Fragment() {

    val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    val postsAdapter by lazy {
        PostsAdapter(object : OnInteractionListener {
            override fun onShare(post: Post) {
                viewModel.shareById(post.id)
                Bundle().apply { isShared = true }
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, post.content)
                    type = "text/plain"
                }
                val shareIntent =
                    Intent.createChooser(intent, getString(R.string.chooser_share_post))
                startActivity(shareIntent)
            }

            override fun onEdit(post: Post) {
                viewModel.editPost(post)
                findNavController().navigate(
                    R.id.action_feedFragment_to_newPostFragment,
                    Bundle().apply {
                        textArg = post.content
                        isShared = false
                    }
                )
            }

            override fun onPostOpen(post: Post) {
                findNavController().navigate(
                    R.id.action_feedFragment_to_singlePostFragment,
                    Bundle().apply {
                        postId = post.id
                    }
                )
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
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentFeedBinding.inflate(
            inflater,
            container,
            false
        )

        binding.postsList.adapter = postsAdapter
        binding.postsList.layoutManager = LinearLayoutManager(context)

        viewModel.data.observe(viewLifecycleOwner) { posts ->
            postsAdapter.submitList(posts)
        }

        viewModel.edited.observe(viewLifecycleOwner) { post ->
            if (post.id == 0L) {
                return@observe
            }
        }

        binding.addPostFab.setOnClickListener {
            findNavController().navigate(
                R.id.action_feedFragment_to_newPostFragment,
                Bundle().apply {
                    isShared = false
                }
            )
        }

//        postsAdapter.currentList[]
//        val cardBinding = activity?.let { CardPostBinding.inflate(it.layoutInflater) }
//
//        cardBinding?.cardPost?.setOnClickListener() {
//            Snackbar.make(binding.root, "Post click event",
//                Snackbar.LENGTH_SHORT).show()
////            findNavController().navigate(
////                R.id.action_feedFragment_to_singlePostFragment,
////                Bundle().apply {
////                    postId = cardBinding.cardPost.id.toLong()
////                }
////            )
//        } ?: Snackbar.make(binding.root, "No activity",
//            Snackbar.LENGTH_LONG).show()

        return binding.root
    }
}
