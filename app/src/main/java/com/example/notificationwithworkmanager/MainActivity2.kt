package com.example.notificationwithworkmanager

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import com.example.notificationwithworkmanager.databinding.ActivityMain2Binding

class MainActivity2 : AppCompatActivity() {
    private lateinit var binding: ActivityMain2Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            buttonSave.setOnClickListener {
                val replyIntent = Intent()
                if (TextUtils.isEmpty(editWord.text)) {
                    setResult(Activity.RESULT_CANCELED, replyIntent)
                } else {
                    val word = editWord.text.toString()
                    replyIntent.putExtra(EXTRA_REPLY, word)
                    setResult(Activity.RESULT_OK, replyIntent)
                }
                finish()
            }
        }
    }
}
