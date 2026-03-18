package org.fossify.gallery.activities

import android.content.Intent
import android.graphics.SurfaceTexture
import android.net.Uri
import android.os.Bundle
import android.view.Surface
import android.view.TextureView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.SeekParameters
import androidx.media3.exoplayer.ExoPlayer
import org.fossify.commons.extensions.*
import org.fossify.commons.helpers.ensureBackgroundThread
import org.fossify.gallery.R

class VideoPlayerActivity : SimpleActivity(), TextureView.SurfaceTextureListener {
    private var player: ExoPlayer? = null
    private lateinit var textureView: TextureView
    private var path: String = ""
    private var uri: Uri? = null
    private var isPlayerReady = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)
        
        textureView = findViewById(R.id.video_surface)
        textureView.surfaceTextureListener = this
        
        path = intent.getStringExtra("path") ?: ""
        uri = if (path.isNotEmpty()) Uri.parse(path) else intent.data
        
        if (uri == null) {
            toast(org.fossify.commons.R.string.unknown_error_occurred)
            finish()
            return
        }
    }

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
        initializePlayer(Surface(surface))
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {}
    override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {}
    
    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
        releasePlayer()
        return true
    }

    private fun initializePlayer(surface: Surface) {
        if (isPlayerReady) return
        
        val currentUri = uri ?: return
        
        player = ExoPlayer.Builder(this)
            .build()
        
            .apply {
                setVideoSurface(surface)
                setMediaItem(MediaItem.fromUri(currentUri))
                prepare()
                playWhenReady = true
                repeatMode = Player.REPEAT_MODE_ONE
            }
        
        isPlayerReady = true
    }

    private fun releasePlayer() {
        player?.release()
        player = null
        isPlayerReady = false
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
                val mimeType = context.getUriMimeType(path, uri!!)
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
