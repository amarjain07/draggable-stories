package weekend.coder.library

interface StoryAction {
    fun start()
    fun reset()
    fun resume()
    fun resumeWithNewDuration(durationInSeconds: Int)
    fun pause()
    fun next()
    fun previous()
}