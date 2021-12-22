package weekend.coder.library.gesture

import android.view.GestureDetector
import android.view.MotionEvent
import kotlin.math.abs

internal class SimpleGestureListener(private val swipeGestures: SwipeGestures) : GestureDetector.SimpleOnGestureListener() {
    override fun onSingleTapUp(event: MotionEvent): Boolean {
        return true
    }

    override fun onFling(
        e1: MotionEvent,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        val diffY = e2.y - e1.y
        val diffX = e2.x - e1.x
        if (abs(diffX) > abs(diffY)) {
            if (abs(diffX) > SWIPE_THRESHOLD && abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffX > 0) {
                    swipeGestures.onSwipeRight()
                } else {
                    swipeGestures.onSwipeLeft()
                }
            }
        }
        return super.onFling(e1, e2, velocityX, velocityY)
    }
    companion object {
        const val SWIPE_THRESHOLD = 100
        const val SWIPE_VELOCITY_THRESHOLD = 100
    }
}