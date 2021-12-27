package weekend.coder.library

import weekend.coder.library.utils.DEFAULT_STORY_INDEX

interface StoryAction {
    fun start(from: Int = DEFAULT_STORY_INDEX)
    fun next()
    fun previous()
    fun pause()
    fun resume()
    fun resumeWithNewDuration(durationInSeconds: Int)
    fun reset()
}