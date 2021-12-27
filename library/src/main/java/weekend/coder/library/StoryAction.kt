package weekend.coder.library

import weekend.coder.library.utils.DEFAULT_STORY_INDEX

interface StoryAction {
    fun start(from: Int = DEFAULT_STORY_INDEX)
    fun reset()
    fun resume()
    fun resumeWithNewDuration(durationInSeconds: Int)
    fun pause()
    fun next()
    fun previous()
}