package ru.netology.nmedia.activity

import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.netology.nmedia.databinding.FragmentNewPostBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.utils.*
import ru.netology.nmedia.viewmodel.PostViewModel
import java.io.IOException


class NewPostFragment : Fragment() {
    private var _binding: FragmentNewPostBinding? = null
    private val binding get() = _binding!!

    val filename = "contentDraft.json"
    val gson = Gson()

    object MODE {
        const val NEW = "new"
        const val SHARE = "share"
        const val EDIT = "edit"
    }

    companion object {
        var Bundle.content: String? by StringArg
        var Bundle.postId: Long? by LongArg
        var Bundle.mode: String? by ModeArg
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

        when (arguments?.mode) {
            MODE.SHARE -> {
                binding.postEditText.inputType = InputType.TYPE_NULL
                arguments?.content?.let(binding.postEditText::setText)
            }
            MODE.NEW -> {
                viewModel.setEditedToEmpty()
                try {
                    requireContext().openFileInput(filename).bufferedReader().use {
                        val post = gson.fromJson(it, Post::class.java)
                        binding.postEditText.setText(post.content)
                    }
                } catch (error: Exception) {
                    binding.postEditText.setText("")
                }
            }
            MODE.EDIT -> arguments?.content?.let(binding.postEditText::setText)
        }

        fun setEditableMode() {
            binding.postEditText.inputType = InputType.TYPE_TEXT_FLAG_MULTI_LINE
            if (arguments?.mode == MODE.NEW) {
                requireContext().openFileOutput(filename, Context.MODE_PRIVATE).bufferedWriter()
                    .use {
                        it.write(gson.toJson(""))
                    }
            }
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

        viewModel.postCreated.observe(viewLifecycleOwner) {
            viewModel.loadPosts()
            findNavController().navigateUp()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireContext().openFileOutput(filename, Context.MODE_PRIVATE).bufferedWriter().use {
                val post = viewModel.getEmpty().copy(content = binding.postEditText.text.toString())
                it.write(gson.toJson(post))
            }
            findNavController().navigateUp()
        }

        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
