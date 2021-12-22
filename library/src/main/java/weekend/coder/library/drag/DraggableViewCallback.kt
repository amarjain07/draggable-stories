package weekend.coder.library.drag

internal interface DraggableViewCallback {
    fun paddingTop(): Int
    fun getDraggableRange(): Int
    fun onDragStart()
    fun onDragComplete()
    fun onViewPositionChanged()
    fun smoothScrollToY(settleDestY: Int)
}