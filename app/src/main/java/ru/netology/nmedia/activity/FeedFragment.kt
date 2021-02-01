package ru.netology.nmedia.activity

import android.os.Bundle
import android.util.Log
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


    private val postsAdapter by lazy {
        PostsAdapter(object : OnInteractionListener {

            override fun onEdit(post: Post) {
                if (!viewModel.isContentChangedPending.value?.contains(post.id)!!) {
                    viewModel.editPost(post)
                    findNavController().navigate(
                        R.id.action_feedFragment_to_newPostFragment,
                        Bundle().apply {
                            content = post.content
                            mode = NewPostFragment.MODE.EDIT
                        }
                    )
                }
            }

            override fun onPostOpen(post: Post) {
                findNavController().navigate(
                    R.id.action_feedFragment_to_singlePostFragment,
                    Bundle().apply {
                        postId = post.id
                    }
                )
            }

            override fun onLike(post: Post) {
                if (!viewModel.isLikeUnlikePending.value?.contains(post.id)!!) {
                    binding.progressBar.isVisible = true
                    viewModel.isLikeUnlikePending.value?.add(post.id)
                    viewModel.editPost(post)
                    viewModel.likeUnlike(post)
                }
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
            binding.progressBar.isVisible = state.refreshing || state.loading
            binding.addPostFab.isVisible = state.idle
            binding.errorGroup.isVisible = state.error
            binding.emptyText.isVisible = state.empty
        }

        binding.retryButton.setOnClickListener {
            viewModel.loadPosts()
        }

        binding.addPostFab.setOnClickListener {
            findNavController().navigate(
                R.id.action_feedFragment_to_newPostFragment,
                Bundle().apply {
                    mode = NewPostFragment.MODE.NEW
                }
            )
        }

        viewModel.postCreated.observe(viewLifecycleOwner) {
            viewModel.loadPosts()
        }

        binding.swipeRefreshWidget.apply {
            setProgressViewEndTarget(false, 0)
            isRefreshing = false
            setOnRefreshListener {
                binding.addPostFab.isVisible = false
                binding.progressBar.isVisible = true
                viewModel.loadPosts()
            }
        }

        return binding.root
    }


    override fun onResume() {
        super.onResume()
        binding.progressBar.isVisible = true
        viewModel.loadPosts()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
