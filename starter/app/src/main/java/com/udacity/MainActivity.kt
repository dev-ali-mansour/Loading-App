package com.udacity

import android.app.DownloadManager
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.udacity.databinding.ActivityMainBinding
import com.udacity.util.ButtonState
import com.udacity.util.DownloadStatus
import com.udacity.util.sendNotification


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0
    private var url: String? = null
    private var fileName: String? = null

    private lateinit var binding: ActivityMainBinding
    private lateinit var notificationManager: NotificationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        binding.contentMain.apply {
            radioGroup.setOnCheckedChangeListener { _, id ->
                when (id) {
                    R.id.glide_radio -> {
                        url = getString(R.string.glideRepoURL)
                        fileName = getString(R.string.glide_text)
                    }
                    R.id.load_app_radio -> {
                        url = getString(R.string.loadAppRepoURL)
                        fileName = getString(R.string.load_app_text)
                    }
                    R.id.retrofit_radio -> {
                        url = getString(R.string.retrofitRepoURL)
                        fileName = getString(R.string.retrofit_text)
                    }
                }
            }

            loadingButton.setOnClickListener { download() }
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            val action = intent?.action
            if (id == downloadID) {
                if (action.equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                    val query = DownloadManager.Query()
                    query.setFilterById(
                        intent.getLongExtra(
                            DownloadManager.EXTRA_DOWNLOAD_ID,
                            0
                        )
                    )
                    val manager =
                        context?.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                    val cursor: Cursor = manager.query(query)
                    if (cursor.moveToFirst()) {
                        if (cursor.count > 0) {
                            val status =
                                cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                            if (status == DownloadManager.STATUS_SUCCESSFUL) {
                                Log.d(MainActivity::class.simpleName, "Success")
                                binding.contentMain.loadingButton.setState(ButtonState.Completed)
                                notificationManager.sendNotification(
                                    fileName.toString(),
                                    applicationContext,
                                    DownloadStatus.SUCCESS
                                )
                            } else {
                                Log.d(MainActivity::class.simpleName, "Failed")
                                binding.contentMain.loadingButton.setState(ButtonState.Completed)
                                notificationManager.sendNotification(
                                    fileName.toString(),
                                    applicationContext,
                                    DownloadStatus.FAIL
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun download() {
        binding.contentMain.loadingButton.setState(ButtonState.Clicked)

        val cm = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
        if (!isConnected) {
            Toast.makeText(
                this,
                getString(R.string.no_network_alert),
                Toast.LENGTH_LONG
            ).show()
            return
        }
        url?.let {
            notificationManager = ContextCompat.getSystemService(
                applicationContext,
                NotificationManager::class.java
            ) as NotificationManager
            binding.contentMain.apply {
                mainLayout.transitionToEnd()
                mainLayout.transitionToStart()
                loadingButton.setState(ButtonState.Loading)
            }

            val request = DownloadManager.Request(Uri.parse(url))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS,
                    "/repository.zip"
                )
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            downloadID =
                downloadManager.enqueue(request)// enqueue puts the download request in the queue.
        } ?: run {
            binding.contentMain.apply {
                loadingButton.setState(ButtonState.Completed)
            }
            Toast.makeText(
                this,
                getString(R.string.no_repo_selected_message),
                Toast.LENGTH_LONG
            ).show()
        }
    }
}
