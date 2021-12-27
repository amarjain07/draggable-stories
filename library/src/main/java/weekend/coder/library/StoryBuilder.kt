package weekend.coder.library

interface StoryBuilder {
    fun setStoryDurationsInSeconds(durations: List<Int>): StoriesView
    fun crossFadeDurationInMillis(crossFadeDuration: Long): StoriesView
    fun setCallback(callback: StoryCallback): StoriesView
}