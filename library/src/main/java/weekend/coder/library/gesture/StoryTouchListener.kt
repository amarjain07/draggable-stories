package weekend.coder.library.gesture

import android.annotation.SuppressLint
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import weekend.coder.library.StoriesView

internal class StoryTouchListener(
    private val storiesView: StoriesView
) : View.OnTouchListener, SwipeGestures {
    private val gestureDetector = GestureDetector(storiesView.context, SimpleGestureListener(this))

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (gestureDetector.onTouchEvent(event)) {
            val actionX = event?.x ?: 0f
            val leftPivotPoint = storiesView.width * TOUCH_THRESHOLD_TO_PREV_NEXT
            val rightPivotPoint = storiesView.width * (1 - TOUCH_THRESHOLD_TO_PREV_NEXT)
            if (actionX > rightPivotPoint) {
                storiesView.next()
            }
            if (actionX < leftPivotPoint) {
                storiesView.previous()
            }
            return true
        } else {
            return when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    storiesView.pause()
                    true
                }
                MotionEvent.ACTION_UP -> {
                    storiesView.resume()
                    true
                }
                else -> false
            }
        }
    }

    override fun onSwipeLeft() {
        storiesView.next()
    }

    override fun onSwipeRight() {
        storiesView.previous()
    }

    companion object {
        private const val TOUCH_THRESHOLD_TO_PREV_NEXT = 0.5
    }
}