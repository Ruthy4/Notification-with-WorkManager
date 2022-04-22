package com.example.notificationwithworkmanager

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.*
import com.example.notificationwithworkmanager.WorkerClass.Companion.setUpNotification
import com.example.notificationwithworkmanager.databinding.ActivityMain2Binding
import com.example.notificationwithworkmanager.databinding.ActivityMainBinding
import com.example.notificationwithworkmanager.local.Word
import com.example.notificationwithworkmanager.local.WordListAdapter
import com.example.notificationwithworkmanager.local.WordViewModel
import com.example.notificationwithworkmanager.local.WordViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {
    private val newWordActivityRequestCode = 1
    private lateinit var binding: ActivityMainBinding
    private val wordViewModel: WordViewModel by viewModels() {
        WordViewModelFactory((application as WordsApplication).repository)
    }
    private val adapter = WordListAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        wordViewModel.allWords.observe(this) { words ->
            words?.let {
                adapter.submitList(it)
            }
        }

        binding.apply {
            NotifyMeButton.setOnClickListener {
                showNotification()
            }

            fab.setOnClickListener {
                val intent = Intent(this@MainActivity, MainActivity2::class.java)
                startActivityForResult(intent, newWordActivityRequestCode)
            }
            setUpRecyclerView()
        }
    }


    private fun setUpRecyclerView() {
        binding.apply {
            recyclerview.adapter = adapter
            recyclerview.layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == newWordActivityRequestCode && resultCode == Activity.RESULT_OK) {
            data?.getStringExtra(EXTRA_REPLY)?.let {
                val word = Word(it)
                wordViewModel.insertWord(word)
                createNotificationChannel()
                createWorkRequest()
                showNotification()
            }
        } else {
            Toast.makeText(
                applicationContext,
                R.string.empty_not_saved,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT

            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createWorkRequest() {
        //set charging constraints
        val constraints = Constraints.Builder()
            .setRequiresCharging(true)
            .build()

        //periodic work request
        val workRequest = OneTimeWorkRequest.Builder(WorkerClass::class.java)
            .build()

        WorkManager.getInstance(applicationContext).enqueue(workRequest)

        //observe result state
        WorkManager.getInstance(applicationContext).getWorkInfoByIdLiveData(workRequest.id)
            .observe(this) { workInfo ->
                binding.notificationResult.append(workInfo.state.name + "\n")
            }
    }

    private fun showNotification() {
        setUpNotification(this)
    }

    private fun cancelWorkRequest() {
        WorkManager.getInstance(applicationContext).cancelAllWork()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        cancelWorkRequest()
    }
}