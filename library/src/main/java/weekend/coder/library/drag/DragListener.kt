package weekend.coder.library.drag

interface DragListener {
    fun onDrag(dragOffset: Float)
    fun onDragDismiss()
}