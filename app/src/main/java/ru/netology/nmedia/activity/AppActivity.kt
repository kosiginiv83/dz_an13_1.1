package ru.netology.nmedia.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.NewPostFragment.Companion.isShared
import ru.netology.nmedia.activity.NewPostFragment.Companion.textArg

//import ru.netology.nmedia.databinding.ActivityIntentHandlerBinding


class AppActivity : AppCompatActivity(R.layout.activity_app) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        val binding = ActivityIntentHandlerBinding.inflate(layoutInflater)
//        setContentView(binding.root)

//        var text: String? = ""

        intent?.let {
            if (it.action != Intent.ACTION_SEND) {
//                Snackbar.make(binding.root, "Not Send Action", Snackbar.LENGTH_LONG).show()
                return@let
            }
            val text = it.getStringExtra(Intent.EXTRA_TEXT)
            if (text.isNullOrBlank()) {
//                Snackbar.make(binding.root, R.string.no_message, LENGTH_INDEFINITE)
//                    .setAction(android.R.string.ok) {
//                        finish()
//                    }
//                    .show()
                return@let
            }
            it.removeExtra(Intent.EXTRA_TEXT)
            findNavController(R.id.nav_host_fragment).navigate(
                R.id.action_feedFragment_to_newPostFragment,
                Bundle().apply {
                    textArg = text
                    isShared = true
                }
            )
//            binding.externalMsg.text = text
        }
//
//
//        binding.repostConfirm.setOnClickListener {
//            val repostIntent = Intent(this@AppActivity, FeedFragment::class.java)
//            repostIntent.putExtra(Intent.EXTRA_TEXT, text.toString())
//            repostIntent.action = Intent.ACTION_SEND
//            repostIntent.type = "text/plain"
//            startActivity(repostIntent)
//
//            finish()
//        }
//
//
//        binding.repostCancel.setOnClickListener {
//            finish()
//        }
    }
}