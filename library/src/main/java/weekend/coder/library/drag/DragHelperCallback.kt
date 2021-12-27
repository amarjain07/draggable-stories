package weekend.coder.library.drag

import android.view.View
import androidx.customview.widget.ViewDragHelper
import weekend.coder.library.drag.DraggableView.Companion.HEIGHT_THRESHOLD_TO_CLOSE
import weekend.coder.library.drag.DraggableView.Companion.SPEED_THRESHOLD_TO_CLOSE
import kotlin.math.max
import kotlin.math.min

internal class DragHelperCallback(
    private val draggableViewCallback: DraggableViewCallback,
    private val draggableContainer: View
): ViewDragHelper.Callback() {

    private var lastDraggingState: Int = ViewDragHelper.STATE_IDLE
    private var topBorderDraggableContainer: Int = 0

    override fun onViewDragStateChanged(state: Int) {
        if (state == lastDraggingState) return
        if ((lastDraggingState == ViewDragHelper.STATE_DRAGGING
                    || lastDraggingState == ViewDragHelper.STATE_SETTLING)
            && state == ViewDragHelper.STATE_IDLE
            && topBorderDraggableContainer >= draggableViewCallback.getDraggableRange()) {
                lastDraggingState = ViewDragHelper.STATE_IDLE
            draggableViewCallback.onDragComplete()
        }
        if (state == ViewDragHelper.STATE_DRAGGING) {
            draggableViewCallback.onDragStart()
        }
        lastDraggingState = state
    }

    override fun onViewPositionChanged(changedView: View, left: Int, top: Int, dx: Int, dy: Int) {
        topBorderDraggableContainer = top
        draggableViewCallback.onViewPositionChanged()
    }

    override fun onViewReleased(releasedChild: View, xVel: Float, yVel: Float) {
        if (topBorderDraggableContainer == 0 || topBorderDraggableContainer >= draggableViewCallback.getDraggableRange()) {
            return
        }
        var settleToClosed = false
        if (yVel > SPEED_THRESHOLD_TO_CLOSE) {
            settleToClosed = true
        } else {
            val verticalDraggableThreshold = (draggableViewCallback.getDraggableRange() * HEIGHT_THRESHOLD_TO_CLOSE).toInt()
            if (topBorderDraggableContainer > verticalDraggableThreshold) {
                settleToClosed = true
            }
        }
        val settleDestY = if (settleToClosed) draggableViewCallback.getDraggableRange() else 0
        draggableViewCallback.smoothScrollToY(settleDestY)
    }

    override fun getViewVerticalDragRange(child: View): Int = draggableViewCallback.getDraggableRange()

    override fun tryCaptureView(child: View, pointerId: Int): Boolean = child == draggableContainer

    override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int = child.left

    override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
        val topBound = draggableViewCallback.paddingTop()
        val bottomBound = draggableViewCallback.getDraggableRange()
        return min(max(top, topBound), bottomBound)
    }
}