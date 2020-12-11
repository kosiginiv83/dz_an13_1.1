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
import ru.netology.nmedia.databinding.FragmentNewPostBinding
import ru.netology.nmedia.utils.AndroidUtils
import ru.netology.nmedia.utils.BooleanArg
import ru.netology.nmedia.utils.LongArg
import ru.netology.nmedia.utils.StringArg
import ru.netology.nmedia.viewmodel.PostViewModel


class NewPostFragment : Fragment() {
    private var _binding: FragmentNewPostBinding? = null
    private val binding get() = _binding!!

    companion object {
        var Bundle.textArg: String? by StringArg
        var Bundle.isShared: Boolean? by BooleanArg
        var Bundle.postId: Long? by LongArg
    }

    private val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewPostBinding.inflate(
            inflater,
            container,
            false
        )

        arguments?.textArg?.let(binding.postEditText::setText)

        if (arguments?.isShared == true) {
            binding.postEditText.inputType = InputType.TYPE_NULL
        }

        fun setEditableMode() {
            binding.postEditText.inputType = InputType.TYPE_TEXT_FLAG_MULTI_LINE
            AndroidUtils.hideKeyboard(requireView())
        }

        binding.okBtn.setOnClickListener {
            if (binding.postEditText.text.isNullOrBlank()) {
                Snackbar.make(
                    binding.root, "Empty text field is not allowed",
                    Snackbar.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            viewModel.changePostContent(binding.postEditText.text.toString())
            viewModel.savePost()
            setEditableMode()
            findNavController().navigateUp()
        }

        binding.cancelBtn.setOnClickListener {
            setEditableMode()
            findNavController().navigateUp()
        }

        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
