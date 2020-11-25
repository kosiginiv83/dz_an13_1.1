package ru.netology.nmedia.activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ru.netology.nmedia.databinding.ActivityNewPostBinding


class NewPostActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityNewPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.resolveActivity(packageManager) != null
                && !intent.getStringExtra(Intent.EXTRA_TEXT).isNullOrBlank()) {
            binding.postEditText.setText(intent.getStringExtra(Intent.EXTRA_TEXT))
        }
        binding.postEditText.requestFocus()

        binding.okBtn.setOnClickListener {
            val newIntent = Intent()
            if (binding.postEditText.text.isNullOrBlank()) {
                setResult(Activity.RESULT_CANCELED, newIntent)
            } else {
                val content = binding.postEditText.text.toString()
                newIntent.putExtra(Intent.EXTRA_TEXT, content)
                setResult(Activity.RESULT_OK, newIntent)
            }
            finish()
        }

        binding.cancelBtn.setOnClickListener {
            finish()
        }
    }
}