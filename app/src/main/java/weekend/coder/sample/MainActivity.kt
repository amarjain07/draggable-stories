package weekend.coder.sample

import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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
        dynamicStoriesView?.addView(storyOne, INDEX_REMOTE_IMAGE_STORY)
        val storyTwo = TextView(this)
        storyTwo.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        storyTwo.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_red_light))
        storyTwo.gravity = Gravity.CENTER
        storyTwo.textSize = 24f
        storyTwo.typeface = Typeface.DEFAULT_BOLD
        storyTwo.text = getString(R.string.title_2)
        dynamicStoriesView?.addView(storyTwo)
        dynamicStoriesView?.let { storiesView ->
            storiesView
                .setStoryDurationsInSeconds(listOf(2, 4))
                .setCallback(this@MainActivity)
                .start()
        }
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

    private fun loadImage(imageView: ImageView, onImageLoad: () -> Unit) {
        val circularProgressDrawable = CircularProgressDrawable(this)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 60f
        circularProgressDrawable.start()
        Glide.with(this)
            .load("https://tinyurl.com/amarjain")
            .placeholder(circularProgressDrawable)
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

    override fun onStoryEnd() {
        Log.d(TAG, "Story Ended")
    }

    override fun onStoryChange(view: View, index: Int, direction: StoryChangeDirection) {
        Log.d(TAG, "Story Changed to $index")
        if (currentClickState == ClickState.DYNAMIC && index == INDEX_REMOTE_IMAGE_STORY) {
            dynamicStoriesView?.pause()
            loadImage(view.findViewById(R.id.iv_image)) {
                dynamicStoriesView?.resumeWithNewDuration(5)
            }
        }
    }

    override fun onDrag(dragOffset: Float) {
        //Log.d(TAG, "Story View onDrag: $dragOffset")
    }

    override fun onDragDismiss() {
        Log.d(TAG, "Story View dismissed")
        toggleStaticStories(hide = true)
        toggleDynamicStories(hide = true)
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val INDEX_REMOTE_IMAGE_STORY = 0
    }
}