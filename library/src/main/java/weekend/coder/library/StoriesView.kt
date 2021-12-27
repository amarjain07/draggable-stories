package weekend.coder.library

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.ViewPropertyAnimator
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.wc_view_story.view.currentView
import kotlinx.android.synthetic.main.wc_view_story.view.dragParent
import kotlinx.android.synthetic.main.wc_view_story.view.progressOverlay
import weekend.coder.library.drag.DragListener
import weekend.coder.library.gesture.StoryChangeDirection
import weekend.coder.library.gesture.StoryTouchListener
import weekend.coder.library.progress.StoryHorizontalProgress
import weekend.coder.library.progress.StoryProgressWatcherImpl
import weekend.coder.library.utils.DEFAULT_STORY_INDEX
import weekend.coder.library.utils.crossFade
import kotlin.math.max
import kotlin.math.min

class StoriesView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attributeSet, defStyleAttr), StoryAction, StoryBuilder, DragListener {

    private var parentView: View? = null
    private var crossFadeAnimator: ViewPropertyAnimator? = null
    private var storyCallback: StoryCallback? = null

    private var storyDurationInSeconds = DEFAULT_STORY_DURATION_IN_SECONDS
    private val storyList: MutableList<View> = mutableListOf()
    private var durationPerStory: List<Int>? = null
    private var crossFadeDuration: Long = 0L
    private val progressViews: MutableList<StoryHorizontalProgress> = mutableListOf()

    private var currentIndex = 0
    private var previousIndex = 0

    private val touchListener by lazy(LazyThreadSafetyMode.NONE) {
        StoryTouchListener(this)
    }

    private val progressWatcher by lazy(LazyThreadSafetyMode.NONE) {
        StoryProgressWatcherImpl(this)
    }

    init {
        initializeAttributes(attributeSet)
        parentView = View.inflate(context, R.layout.wc_view_story, this)
        parentView?.currentView?.setOnTouchListener(touchListener)
        parentView?.dragParent?.setDragListener(this)
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        this.layoutParams = params
    }

    override fun addView(child: View?, index: Int, params: ViewGroup.LayoutParams?) {
        parentView?.let { mainView ->
            if (child != null) {
                if (child.id == NO_ID) child.id = View.generateViewId()
                val lastIndex = mainView.currentView.indexOfChild(child)
                if (lastIndex < 0) {
                    val storyIndex = storyList.size
                    storyList.add(storyIndex, child)
                    val progressView = progressView()
                    progressViews.add(progressView)
                    mainView.progressOverlay.addView(progressView)
                    mainView.currentView.addView(child)
                    if (storyIndex == 0) {
                        child.visibility = View.VISIBLE
                    } else {
                        child.visibility = View.GONE
                    }
                }
            }
        } ?: super.addView(child, index, params)
    }

    override fun onDrag(dragOffset: Float) {
        if (dragOffset == 0f) resume() else pause()
        storyCallback?.onDrag(dragOffset)
    }

    override fun onDragDismiss() {
        progressViews[currentIndex].cancelProgress()
        storyCallback?.onDragDismiss()
    }

    override fun setStoryDurationsInSeconds(durations: List<Int>): StoriesView {
        durationPerStory = durations
        return this
    }

    override fun crossFadeDurationInMillis(crossFadeDuration: Long): StoriesView {
        this.crossFadeDuration = crossFadeDuration
        return this
    }

    override fun setCallback(callback: StoryCallback): StoriesView {
        storyCallback = callback
        return this
    }

    override fun start(from: Int) {
        resetDrag()
        previousIndex = currentIndex
        currentIndex = if(from >= 0 && from < storyList.size){
            from
        } else {
            DEFAULT_STORY_INDEX
        }
        showCurrent(StoryChangeDirection.INIT)
    }

    override fun reset() {
        resetDrag()
        previousIndex = currentIndex
        currentIndex = DEFAULT_STORY_INDEX
    }

    private fun resetDrag() {
        parentView?.dragParent?.changeDragViewAlpha(0f)
    }

    override fun pause() {
        if (currentIndex < storyList.size) {
            progressViews[currentIndex].pauseProgress()
        }
    }

    override fun resume() {
        if (currentIndex < storyList.size) {
            progressViews[currentIndex].resumeProgress()
        }
    }

    override fun resumeWithNewDuration(durationInSeconds: Int) {
        if (currentIndex < storyList.size) {
            progressViews[currentIndex].resumeWithUpdatedDuration(durationInSeconds)
        }
    }

    override fun next() {
        previousIndex = currentIndex
        currentIndex++
        if (currentIndex >= storyList.size) {
            currentIndex = previousIndex
            finish()
            return
        }
        showCurrent(StoryChangeDirection.NEXT)
    }

    override fun previous() {
        previousIndex = currentIndex
        currentIndex--
        if (currentIndex < 0) {
            currentIndex = 0
            previousIndex = currentIndex
            resume()
            return
        }
        showCurrent(StoryChangeDirection.PREVIOUS)
    }

    private fun showCurrent(direction: StoryChangeDirection) {
        val totalStories = storyList.size
        if (totalStories == 0) return
        if (currentIndex != 0) {
            for (i in 0..max(0, currentIndex - 1)) {
                progressViews[i].progress = 100
                progressViews[i].cancelProgress()
            }
        }

        if (currentIndex != totalStories - 1) {
            for (i in currentIndex + 1 until totalStories) {
                progressViews[i].progress = 0
                progressViews[i].cancelProgress()
            }
        }
        if (currentIndex < totalStories) {
            val currentView = storyList[currentIndex]
            progressViews[currentIndex].startProgress(getStoryDuration(currentIndex))
            storyCallback?.onStoryChange(currentView, currentIndex, direction)
            parentView?.let { mainView ->
                val prevView = mainView.currentView.getChildAt(previousIndex)
                val curView = mainView.currentView.getChildAt(currentIndex)
                if (previousIndex != currentIndex) {
                    val fadeDuration = if (crossFadeDuration < 0) 0L else crossFadeDuration
                    crossFadeAnimator?.cancel()
                    crossFadeAnimator =
                        prevView.crossFade(curView, fadeDuration, previousIndex > currentIndex)
                } else {
                    prevView.visibility = View.GONE
                    curView.alpha = 1f
                    curView.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun progressView(): StoryHorizontalProgress {
        return StoryHorizontalProgress(context).apply { progressWatcher(progressWatcher) }
    }

    private fun finish() {
        storyCallback?.onStoryEnd()
        for (progressBar in progressViews) {
            progressBar.cancelProgress()
            progressBar.progress = 100
        }
    }

    private fun getStoryDuration(index: Int): Int {
        return durationPerStory?.let { durationList ->
            if (index < durationList.size) {
                durationList[index]
            } else {
                storyDurationInSeconds
            }
        } ?: storyDurationInSeconds
    }

    /**
     * Initializes XML attributes.
     */
    private fun initializeAttributes(attrs: AttributeSet?) {
        val array =
            context.theme.obtainStyledAttributes(attrs, R.styleable.StoriesView, 0, 0)
        try {
            storyDurationInSeconds = array.getInteger(
                R.styleable.StoriesView_storyDurationInSec,
                DEFAULT_STORY_DURATION_IN_SECONDS
            )
        } finally {
            array.recycle()
        }
    }

    companion object {
        private const val DEFAULT_STORY_DURATION_IN_SECONDS = 5
    }
}