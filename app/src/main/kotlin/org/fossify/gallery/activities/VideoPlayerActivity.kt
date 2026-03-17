package org.fossify.gallery.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
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
    private var path = ""
    private var uri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.video_player_activity)
        
        playerView = findViewById(R.id.player_view)
        path = intent.getStringExtra("path") ?: ""
        
        if (path.isNotEmpty()) {
            uri = Uri.parse(path)
        } else {
            uri = intent.data
        }
        
        if (uri == null) {
            toast(R.string.unknown_error_occurred)
            finish()
            return
        }
    }

    override fun onResume() {
        super.onResume()
        initializePlayer()
    }

    override fun onPause() {
        super.onPause()
        releasePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        releasePlayer()
    }

    private fun initializePlayer() {
        if (player != null) return
        
        player = ExoPlayer.Builder(this)
            .setSeekParameters(SeekParameters.CLOSEST_SYNC)
            .build()
            .apply {
                setMediaItem(MediaItem.fromUri(uri!!))
                prepare()
                playWhenReady = true
                repeatMode = Player.REPEAT_MODE_ONE
            }
        
        playerView.player = player
        playerView.setShowShuffleButton(true)
        playerView.setShowSubtitleButton(true)
        playerView.controllerShowTimeoutMs = 3000
    }

    private fun releasePlayer() {
        player?.release()
        player = null
    }

    companion object {
        fun openVideo(context: SimpleActivity, path: String, uri: Uri? = null) {
            ensureBackgroundThread {
                val mimeType = context.getUriMimeType(path, uri)
                context.runOnUiThread {
                    Intent(context, VideoPlayerActivity::class.java).apply {
                        putExtra("path", path)
                        uri?.let { data = it }
                        setDataAndType(uri ?: Uri.parse(path), mimeType)
                        context.startActivity(this)
                    }
                }
            }
        }
    }
}
