package ru.netology.nmedia.activity

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_new_post.view.*
import ru.netology.nmedia.databinding.FragmentNewPostBinding
import ru.netology.nmedia.databinding.SinglePostContainerBinding
import ru.netology.nmedia.utils.AndroidUtils
import ru.netology.nmedia.utils.BooleanArg
import ru.netology.nmedia.utils.StringArg
import ru.netology.nmedia.viewmodel.PostViewModel


class SinglePostFragment : Fragment() {
    // action_feedFragment_to_singlePostFragment
    // action_singlePostFragment_to_newPostFragment

    private val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = SinglePostContainerBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }
}
