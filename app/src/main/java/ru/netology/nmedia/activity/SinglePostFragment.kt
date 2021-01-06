package ru.netology.nmedia.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.card_post.*
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.NewPostFragment.Companion.postId
import ru.netology.nmedia.activity.NewPostFragment.Companion.content
import ru.netology.nmedia.activity.NewPostFragment.Companion.mode
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.adapter.getFormattedNum
import ru.netology.nmedia.databinding.FragmentSinglePostBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewmodel.PostViewModel


class SinglePostFragment : Fragment() {
    private var _binding: FragmentSinglePostBinding? = null
    private val binding get() = _binding!!

    val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    private val singlePostAdapter by lazy {
        PostsAdapter(object : OnInteractionListener {
            override fun onShare(post: Post) {
                viewModel.shareById(post.id)
                Bundle().apply { mode = NewPostFragment.MODE.SHARE }
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
                    R.id.action_singlePostFragment_to_newPostFragment,
                    Bundle().apply {
                        content = post.content
                        mode = NewPostFragment.MODE.EDIT
                    }
                )
            }

            override fun onPostOpen(post: Post) {
                return
            }

            override fun onVideoOpen(post: Post) {
                Intent(Intent.ACTION_VIEW, Uri.parse(post.videoLink)).also(::startActivity)
            }

            override fun onLike(post: Post) {
                viewModel.likeById(post.id)
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

        arguments?.postId?.run {
            viewModel.getPostById(this).observe(viewLifecycleOwner) { posts ->
                singlePostAdapter.submitList(posts)
            }
        } ?: findNavController().navigateUp()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
