package org.fossify.gallery.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.SeekParameters
import androidx.media3.ui.PlayerView
import org.fossify.commons.extensions.*
import org.fossify.commons.helpers.ensureBackgroundThread
import org.fossify.gallery.R
import org.fossify.gallery.extensions.config

class VideoPlayerActivity : SimpleActivity() {
    private var player: ExoPlayer? = null
    private lateinit var playerView: PlayerView
    private var path: String = ""
    private var uri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)
        
        playerView = findViewById(R.id.player_view)
        
        path = intent.getStringExtra("path") ?: ""
        uri = if (path.isNotEmpty()) Uri.parse(path) else intent.data
        
        if (uri == null) {
            toast(org.fossify.commons.R.string.unknown_error_occurred)
            finish()
            return
        }
        
        initializePlayer()
    }

    private fun initializePlayer() {
        val currentUri = uri ?: return
        
        player = ExoPlayer.Builder(this)
            .setSeekParameters(SeekParameters.CLOSEST_SYNC)
            .build()
            .apply {
                setMediaItem(MediaItem.fromUri(currentUri))
                prepare()
                playWhenReady = true
                repeatMode = Player.REPEAT_MODE_ONE
            }
        
        playerView.player = player
    }

    private fun releasePlayer() {
        player?.release()
        player = null
    }

    override fun onPause() {
        super.onPause()
        player?.playWhenReady = false
    }

    override fun onResume() {
        super.onResume()
        player?.playWhenReady = true
    }

    override fun onDestroy() {
        super.onDestroy()
        releasePlayer()
    }

    companion object {
        fun openVideo(context: SimpleActivity, path: String, uri: Uri? = null) {
            ensureBackgroundThread {
                val mimeType = context.getUriMimeType(path, uri)
                context.runOnUiThread {
                    Intent(context, VideoPlayerActivity::class.java).apply {
                        putExtra("path", path)
                        uri?.let { data = it }
                        val intentUri = uri ?: Uri.parse(path)
                        setDataAndType(intentUri, mimeType)
                        context.startActivity(this)
                    }
                }
            }
        }
    }
}
