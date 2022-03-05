package org.relaxindia.view.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import org.relaxindia.R
import org.relaxindia.util.toast

class VideoPlayActivity : AppCompatActivity() {

    var videoUrl = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_play)

        videoUrl = intent.getStringExtra("video_url").toString()
        toast(videoUrl)
    }
}