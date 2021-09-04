package com.udacity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.udacity.databinding.ActivityDetailBinding
import com.udacity.util.DownloadStatus.FAIL
import com.udacity.util.DownloadStatus.SUCCESS
import java.io.Serializable

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        binding.apply {
            val fileName = intent.getStringExtra("fileName").toString()
            val status = intent.getSerializableExtra("status")
            contentDetail.fileName.text = fileName
            status?.let {
                contentDetail.statusText.text = it.toString()
                it.setStatusTextColor()
            }

            fabOk.setOnClickListener {
                startActivity(Intent(this@DetailActivity, MainActivity::class.java))
            }
        }
    }

    private fun Serializable.setStatusTextColor() {
        when (this) {
            SUCCESS -> binding.contentDetail.statusText.setTextColor(Color.GREEN)
            FAIL -> binding.contentDetail.statusText.setTextColor(Color.RED)
        }
    }
}
