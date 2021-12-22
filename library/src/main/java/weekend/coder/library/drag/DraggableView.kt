package weekend.coder.library.drag

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import androidx.annotation.IdRes
import androidx.core.view.ViewCompat
import androidx.customview.widget.ViewDragHelper
import weekend.coder.library.R
import kotlin.math.abs

internal class DraggableView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), DraggableViewCallback {

    @IdRes
    private var containerId: Int = -1
    @IdRes
    private var draggableViewId: Int = -1
    private lateinit var container: View
    private lateinit var draggableView: View
    private lateinit var dragHelper: ViewDragHelper
    private var listener: DragListener? = null
    private var viewHeight: Int = 0
    private val cardShape: GradientDrawable by lazy { GradientDrawable() }

    init {
        initializeAttributes(attrs)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        initViews()
        initViewDragHelper()
    }

    override fun onSizeChanged(w: Int, h: Int, oldW: Int, oldH: Int) {
        super.onSizeChanged(w, h, oldW, oldH)
        viewHeight = h
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        var handled = false
        if (isEnabled) {
            handled = dragHelper.shouldInterceptTouchEvent(event)
                    && dragHelper.isViewUnder(draggableView, event.x.toInt(), event.y.toInt())
        } else {
            dragHelper.cancel()
        }
        return handled || super.onInterceptTouchEvent(event)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        dragHelper.processTouchEvent(event)
        return isViewTouched(draggableView, event.x.toInt(), event.y.toInt())
    }

    override fun computeScroll() {
        if (dragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this)
        }
    }

    fun setDragListener(listener: DragListener) {
        this.listener = listener
    }

    override fun getDraggableRange(): Int = viewHeight

    override fun onDragStart() {
        listener?.onDrag(0f)
    }

    override fun onDragComplete() {
        listener?.onDragDismiss()
    }

    override fun onViewPositionChanged() {
        val verticalDragOffset = getVerticalDragOffset()
        changeDragViewAlpha(verticalDragOffset)
        listener?.onDrag(verticalDragOffset)
    }

    internal fun changeDragViewAlpha(verticalDragOffset: Float) {
        //container.alpha = 1 - verticalDragOffset
        if (verticalDragOffset < SCALE_THRESHOLD) {
            container.scaleX = 1 - verticalDragOffset
        }
        if (verticalDragOffset < HEIGHT_THRESHOLD_TO_CORNER_RADIUS) {
            cardShape.cornerRadius = SPEED_THRESHOLD_TO_CORNER_RADIUS * verticalDragOffset
        }
        container.background = cardShape
    }

    override fun smoothScrollToY(settleDestY: Int) {
        if (dragHelper.settleCapturedViewAt(paddingLeft, settleDestY)) {
            ViewCompat.postInvalidateOnAnimation(this)
        }
    }

    override fun paddingTop(): Int {
        return paddingTop
    }

    private fun initializeAttributes(attrs: AttributeSet?) {
        val array =
            context.theme.obtainStyledAttributes(attrs, R.styleable.DraggableView, 0, 0)
        try {
            draggableViewId = array.getResourceId(
                R.styleable.DraggableView_draggableView,
                -1
            )
            containerId = array.getResourceId(
                R.styleable.DraggableView_draggableContainer,
                -1
            )
            if (draggableViewId == -1 || containerId == -1) {
                throw IllegalArgumentException("draggableView and container attributes are required.")
            }
        } finally {
            array.recycle()
        }
    }

    private fun initViews() {
        container = findViewById(containerId)
            ?: throw IllegalArgumentException("container not found!")
        draggableView = findViewById(draggableViewId)
            ?: throw IllegalArgumentException("draggableView not found!")
        container.clipToOutline = true
    }

    private fun initViewDragHelper() {
        dragHelper = ViewDragHelper.create(
            this, DRAG_SENSITIVITY,
            DragHelperCallback(this, container)
        )
    }

    private fun isViewTouched(view: View, x: Int, y: Int): Boolean {
        val viewLocation = IntArray(2)
        view.getLocationOnScreen(viewLocation)
        val parentLocation = IntArray(2)
        this.getLocationOnScreen(parentLocation)
        val screenX = parentLocation[0] + x
        val screenY = parentLocation[1] + y
        return (screenX >= viewLocation[0]
                && screenX < viewLocation[0] + view.width
                && screenY >= viewLocation[1]
                && screenY < viewLocation[1] + view.height)
    }

    private fun getVerticalDragOffset(): Float =
        abs(container.top).toFloat() / height.toFloat()

    companion object {

        // Sensitivity detecting the start of a drag (larger values are more sensitive)
        internal const val DRAG_SENSITIVITY = 1.0f

        // If the view is dragged with a higher speed than the threshold, the view is
        // closed automatically
        internal const val SPEED_THRESHOLD_TO_CLOSE = 800.0f

        // If dragging finishes below this threshold the view returns to its original position,
        // if the threshold is exceeded, the view is closed automatically
        internal const val HEIGHT_THRESHOLD_TO_CLOSE = 0.5f

        internal const val HEIGHT_THRESHOLD_TO_CORNER_RADIUS = 0.1f

        internal const val SPEED_THRESHOLD_TO_CORNER_RADIUS = 512f

        internal const val SCALE_THRESHOLD = 0.05
    }
}
