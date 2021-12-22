package weekend.coder.library

import android.view.View
import weekend.coder.library.drag.DragListener
import weekend.coder.library.gesture.StoryChangeDirection

interface StoryCallback: DragListener {
    fun onStoryEnd()
    fun onStoryChange(view: View, index: Int, direction: StoryChangeDirection)
}