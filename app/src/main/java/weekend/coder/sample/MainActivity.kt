package weekend.coder.sample

import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.android.synthetic.main.activity_main.*
import weekend.coder.library.StoriesView
import weekend.coder.library.StoryCallback
import weekend.coder.library.gesture.StoryChangeDirection

class MainActivity : AppCompatActivity(), StoryCallback {

    private var currentClickState = ClickState.NONE
    private var dynamicStoriesView: StoriesView? = null
    private var player: ExoPlayer? = null
    private var loader: ProgressBar? = null
    private val playbackStateListener: Player.Listener by lazy { playbackStateListener() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.hideStatusBar()
        setContentView(R.layout.activity_main)
    }

    fun onLaunchStaticStory(view: View) {
        Log.d(TAG, "Static Stories Clicked: ${view.id}")
        currentClickState = ClickState.STATIC
        toggleStaticStories(hide = false)
        staticStoriesContainer
            .setCallback(this@MainActivity)
            .start()
    }

    fun onLaunchDynamicStory(view: View) {
        Log.d(TAG, "Dynamic Stories Clicked: ${view.id}")
        currentClickState = ClickState.DYNAMIC
        dynamicStoriesView = StoriesView(this)
        val storyOne = LayoutInflater.from(this).inflate(
            R.layout.layout_story, null, false
        )
        val storyThree = LayoutInflater.from(this).inflate(
            R.layout.layout_video, null, false
        )
        loader = storyThree.findViewById(R.id.pb_loader)
        dynamicStoriesView?.addView(storyOne, INDEX_REMOTE_IMAGE_STORY)
        dynamicStoriesView?.addView(storyTwoTextView())
        dynamicStoriesView?.addView(storyThree, INDEX_REMOTE_VIDEO_STORY)
        dynamicStoriesView
            ?.setStoryDurationsInSeconds(listOf(2, 1))
            ?.setCallback(this@MainActivity)
            ?.start()
        dynamicStoriesContainer.removeAllViews()
        toggleDynamicStories(hide = false)
        dynamicStoriesContainer.addView(dynamicStoriesView)
    }

    private fun toggleStaticStories(hide: Boolean) {
        if (hide) {
            staticStoriesContainer.visibility = View.GONE
            toggleButtons(View.VISIBLE)
        } else {
            staticStoriesContainer.visibility = View.VISIBLE
            toggleButtons(View.GONE)
        }
    }

    private fun toggleDynamicStories(hide: Boolean) {
        if (hide) {
            dynamicStoriesContainer.visibility = View.GONE
            toggleButtons(View.VISIBLE)
        } else {
            dynamicStoriesContainer.visibility = View.VISIBLE
            toggleButtons(View.GONE)
        }
    }

    private fun toggleButtons(visibility: Int) {
        btnStaticLaunch.visibility = visibility
        btnDynamicLaunch.visibility = visibility
    }

    private fun loader(): CircularProgressDrawable {
        val circularProgressDrawable = CircularProgressDrawable(this)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 60f
        circularProgressDrawable.start()
        return circularProgressDrawable
    }

    private fun storyOneImage(imageView: ImageView, onImageLoad: () -> Unit) {
        Glide.with(this)
            .load(Constants.IMAGE_URL)
            .placeholder(loader())
            .addListener(object : RequestListener<Drawable> {
                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    Log.d(TAG, "Image Loaded")
                    onImageLoad()
                    return false
                }

                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    Log.d(TAG, "Image Loading Failed")
                    onImageLoad()
                    return false
                }
            })
            .into(imageView)
    }

    private fun storyTwoTextView(): TextView {
        val storyTwo = TextView(this)
        storyTwo.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        storyTwo.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_green_light))
        storyTwo.gravity = Gravity.CENTER
        storyTwo.textSize = 24f
        storyTwo.typeface = Typeface.DEFAULT_BOLD
        storyTwo.text = getString(R.string.title_2)
        return storyTwo
    }

    private fun storyThreeVideo(playerView: PlayerView) {
        playerView.useController = false;
        player = ExoPlayer.Builder(this)
            .build()
            .also { exoPlayer ->
                playerView.player = exoPlayer
                val mediaItem =
                    MediaItem.fromUri(Uri.parse(Constants.VIDEO_URL))
                exoPlayer.setMediaItem(mediaItem)
                exoPlayer.playWhenReady = true
                exoPlayer.seekTo(0, 0)
                exoPlayer.addListener(playbackStateListener)
                exoPlayer.prepare()
            }
    }

    private fun releasePlayer() {
        player?.removeListener(playbackStateListener)
        player?.release()
        player = null
    }

    override fun onStoryEnd() {
        Log.d(TAG, "Story Ended")
        if (currentClickState == ClickState.DYNAMIC) {
            toggleDynamicStories(hide = true)
        }
    }

    override fun onStoryChange(view: View, index: Int, direction: StoryChangeDirection) {
        Log.d(TAG, "Story Changed to $index")
        if (currentClickState == ClickState.DYNAMIC) {
            if (index == INDEX_REMOTE_IMAGE_STORY) {
                dynamicStoriesView?.pause()
                storyOneImage(view.findViewById(R.id.iv_image)) {
                    dynamicStoriesView?.resumeWithNewDuration(3)
                }
            }
            if (index == INDEX_REMOTE_VIDEO_STORY) {
                storyThreeVideo(view.findViewById(R.id.pv_video))
            }
        }
    }

    override fun onDrag(dragOffset: Float) {
        //Log.d(TAG, "Story View onDrag: $dragOffset")
    }

    override fun onDragDismiss() {
        Log.d(TAG, "Story View dismissed")
        if (currentClickState == ClickState.STATIC) {
            toggleStaticStories(hide = true)
        } else if (currentClickState == ClickState.DYNAMIC) {
            toggleDynamicStories(hide = true)
        }
    }

    private fun playbackStateListener() = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            when (playbackState) {
                ExoPlayer.STATE_IDLE,
                ExoPlayer.STATE_BUFFERING -> {
                    Log.d(TAG, "Downloading Video")
                    loader?.visibility = View.VISIBLE
                    dynamicStoriesView?.pause()
                }
                ExoPlayer.STATE_READY -> {
                    Log.d(TAG, "Video Ready")
                    loader?.visibility = View.GONE
                    val realDurationMillis: Long = player?.duration ?: 5L
                    dynamicStoriesView?.resumeWithNewDuration(realDurationMillis.toInt() / 1000)
                }
                ExoPlayer.STATE_ENDED -> {
                    Log.d(TAG, "Video Ended")
                    releasePlayer()
                }
            }
        }
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val INDEX_REMOTE_IMAGE_STORY = 0
        private const val INDEX_REMOTE_VIDEO_STORY = 2
    }
}