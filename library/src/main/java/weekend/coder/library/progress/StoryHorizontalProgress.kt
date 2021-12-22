package weekend.coder.library.progress

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import weekend.coder.library.R
import weekend.coder.library.utils.toDp

internal class StoryHorizontalProgress @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ProgressBar(
    context,
    attributeSet,
    defStyleAttr,
    android.R.style.Widget_ProgressBar_Horizontal
) {
    private var storyProgressWatcher: StoryProgressWatcher? = null
    private val objectAnimator: ObjectAnimator =
        ObjectAnimator.ofInt(this, "progress", this.progress, 100)

    init {
        val progressBarHeight = 2f.toDp(context)
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            progressBarHeight, 1f
        )
        params.marginEnd = 5f.toDp(context)
        this.max = 100
        this.progress = 0
        this.layoutParams = params
        this.progressDrawable =
            ContextCompat.getDrawable(context, R.drawable.wc_story_progress_default)
    }

    fun progressDrawableRes(@DrawableRes drawable: Int) {
        this.progressDrawable = ContextCompat.getDrawable(context, drawable)
    }

    fun progressWatcher(watcher: StoryProgressWatcher) {
        this.storyProgressWatcher = watcher
    }

    fun startProgress(currentDurationInSeconds: Int) {
        objectAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                animation?.apply { removeAllListeners() }
                storyProgressWatcher?.onEnd()
            }

            override fun onAnimationCancel(animation: Animator?) {
                animation?.apply { removeAllListeners() }
            }

            override fun onAnimationRepeat(animation: Animator?) {}
        })
        objectAnimator.apply {
            duration = (currentDurationInSeconds * 1000).toLong()
            start()
        }
    }

    fun cancelProgress() {
        if (objectAnimator.isStarted) {
            objectAnimator.cancel()
        }
    }

    fun pauseProgress() {
        if (objectAnimator.isStarted && objectAnimator.isRunning) {
            objectAnimator.pause()
        }
    }

    fun resumeProgress() {
        if (objectAnimator.isStarted && objectAnimator.isPaused) {
            objectAnimator.resume()
        }
    }

    fun resumeWithUpdatedDuration(newDurationInSeconds: Int) {
        cancelProgress()
        startProgress(newDurationInSeconds)
    }
}