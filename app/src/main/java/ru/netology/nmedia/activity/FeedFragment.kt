package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.NewPostFragment.Companion.content
import ru.netology.nmedia.activity.NewPostFragment.Companion.mode
import ru.netology.nmedia.activity.NewPostFragment.Companion.postId
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewmodel.PostViewModel


class FeedFragment : Fragment() {
    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!

    val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

//    private val APP_PREFS_FIRST_LAUNCH = "isFirstLaunch"
//    private val prefs by lazy {
//        requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE)
//    }


    private val postsAdapter by lazy {
        PostsAdapter(object : OnInteractionListener {
//            override fun onShare(post: Post) {
//                viewModel.shareById(post.id)
//                Bundle().apply { mode = NewPostFragment.MODE.SHARE }
//                val intent = Intent().apply {
//                    action = Intent.ACTION_SEND
//                    putExtra(Intent.EXTRA_TEXT, post.content)
//                    type = "text/plain"
//                }
//                val shareIntent =
//                    Intent.createChooser(intent, getString(R.string.chooser_share_post))
//                startActivity(shareIntent)
//            }

            override fun onEdit(post: Post) {
                viewModel.editPost(post)
                findNavController().navigate(
                    R.id.action_feedFragment_to_newPostFragment,
                    Bundle().apply {
                        content = post.content
                        mode = NewPostFragment.MODE.EDIT
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

//            override fun onVideoOpen(post: Post) {
//                Intent(Intent.ACTION_VIEW, Uri.parse(post.videoLink)).also(::startActivity)
//            }

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
        _binding = FragmentFeedBinding.inflate(
            inflater,
            container,
            false
        )

        binding.postsList.adapter = postsAdapter
        binding.postsList.layoutManager = LinearLayoutManager(context)

        viewModel.data.observe(viewLifecycleOwner) { state ->
            postsAdapter.submitList(state.posts)
            binding.progressBar.isVisible = state.loading
            binding.errorGroup.isVisible = state.error
            binding.emptyText.isVisible = state.empty
        }

        binding.retryButton.setOnClickListener {
            viewModel.loadPosts()
        }

//        prefs.getBoolean(APP_PREFS_FIRST_LAUNCH, true).let { isFirstLaunch ->
//            if (isFirstLaunch) {
//                viewModel.getPostsFromAsset(requireContext())
//                with (prefs.edit()) {
//                    putBoolean(APP_PREFS_FIRST_LAUNCH, false)
//                    apply()
//                }
//            }
//        }

        binding.addPostFab.setOnClickListener {
            findNavController().navigate(
                R.id.action_feedFragment_to_newPostFragment,
                Bundle().apply {
                    mode = NewPostFragment.MODE.NEW
                }
            )
        }

        binding.swipeRefreshWidget.apply {
//            setProgressViewEndTarget(false, 0) // Убирает спиннер
            setOnRefreshListener {
                viewModel.loadPosts()
                isRefreshing = false
            }
        }

        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
