package ru.netology.nmedia.activity

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_new_post.view.*
import ru.netology.nmedia.activity.NewPostFragment.Companion.postId
import ru.netology.nmedia.databinding.FragmentNewPostBinding
import ru.netology.nmedia.databinding.FragmentSinglePostBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.utils.AndroidUtils
import ru.netology.nmedia.utils.BooleanArg
import ru.netology.nmedia.utils.StringArg
import ru.netology.nmedia.viewmodel.PostViewModel


class SinglePostFragment : Fragment() {

    private val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentSinglePostBinding.inflate(
            inflater,
            container,
            false
        )

//        binding.singlePostList.adapter = postsAdapter
//        binding.singlePostList.layoutManager = LinearLayoutManager(context)

        val postIdToView = arguments?.postId ?: -1L
        if (postIdToView == -1L) findNavController().navigateUp()
        val postToView = viewModel.data.value?.first { it.id == postIdToView }

        if (postToView != null) {
            viewModel.editPost(postToView)
        } else findNavController().navigateUp()
//        val singlePostList: List<Post> = listOf(postToView)

        val postsAdapter = FeedFragment().postsAdapter

        binding.singlePostList.adapter = postsAdapter
        binding.singlePostList.layoutManager = LinearLayoutManager(context)

        viewModel.edited.observe(viewLifecycleOwner) { post ->
            postsAdapter.submitList(listOf(post))
        }

        return binding.root
    }

    
}
