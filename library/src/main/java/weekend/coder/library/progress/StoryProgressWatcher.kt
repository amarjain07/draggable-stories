package weekend.coder.library.progress

import weekend.coder.library.StoryAction

internal interface StoryProgressWatcher {
    fun onEnd()
}

internal class StoryProgressWatcherImpl(private val action: StoryAction): StoryProgressWatcher {
    override fun onEnd() {
        action.next()
    }
}