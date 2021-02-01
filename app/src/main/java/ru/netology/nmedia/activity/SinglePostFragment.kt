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
import ru.netology.nmedia.databinding.FragmentSinglePostBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.viewmodel.PostViewModel


class SinglePostFragment : Fragment() {
    private var _binding: FragmentSinglePostBinding? = null
    private val binding get() = _binding!!

    val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    private val singlePostAdapter by lazy {
        PostsAdapter(object : OnInteractionListener {
            override fun onEdit(post: Post) {
                if (!viewModel.isContentChangedPending.value?.contains(post.id)!!) {
                    viewModel.editPost(post)
                    findNavController().navigate(
                        R.id.action_singlePostFragment_to_newPostFragment,
                        Bundle().apply {
                            content = post.content
                            mode = NewPostFragment.MODE.EDIT
                        }
                    )
                }
            }

            override fun onPostOpen(post: Post) {
                return
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
                findNavController().navigateUp()
            }
        })
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSinglePostBinding.inflate(
            inflater,
            container,
            false
        )
        binding.singlePostList.adapter = singlePostAdapter
        binding.singlePostList.layoutManager = LinearLayoutManager(context)
        viewModel.setSinglePostToEmpty()

        var postId: Long = -1
        arguments?.postId?.let { postId = it } ?: findNavController().navigateUp()
        viewModel.getPostById(postId)

        viewModel.singlePost.observe(viewLifecycleOwner) {
            singlePostAdapter.submitList(it.posts)
            binding.progressBar.isVisible = it.refreshing
            binding.errorGroup.isVisible = it.error
        }

        binding.retryButton.setOnClickListener {
            viewModel.getPostById(postId)
        }

        binding.swipeRefreshWidget.apply {
            setProgressViewEndTarget(false, 0)
            isRefreshing = false
            setOnRefreshListener {
                binding.progressBar.isVisible = true
                viewModel.getPostById(postId)
            }
        }

        viewModel.singlePostUpdated.observe(viewLifecycleOwner) {
            viewModel.getPostById(postId)
        }

        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
