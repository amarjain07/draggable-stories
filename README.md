# Draggable StoriesView
Stories View like Whatsapp, Instagram, Facebook, Twitter.

[![](https://jitpack.io/v/amarjain07/draggable-stories.svg)](https://jitpack.io/#amarjain07/draggable-stories)

## Install

Add jitpack to your root's `build.gradle`
```groovy
allprojects {
	repositories {
		maven { url "https://jitpack.io" }
	}
}
```
Add dependency to the library in a module
```groovy
dependencies {
	implementation 'com.github.amarjain07:draggable-stories:<latest-version>'
}
```

## Demo
![Static Stories](demo/stories_static.webm) ![Dynamic Stories](demo/stories_dynamic.webm)

## Features
- Automatic switch between the stories with a configurable duration for each story.
- Drag/Pull to close the stories view.
- Callbacks to the client on Next/Previous stories.
- Pause/Resume stories on tap.
- Support for loading remote images/videos in the story.
- Option to start stories from any index.

### Using xml
All the direct children of *weekend.coder.library.StoriesView* become the individual stories.
```xml
<weekend.coder.library.StoriesView
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    app:storyDurationInSec="5">
    <ImageView.../>
    <TextView../>
    <LinearLayout.../>
</weekend.coder.library.StoriesView>
```
### Using Kotlin code
```kotlin
import weekend.coder.library.StoriesView
...
val storiesView = StoriesView(this)
val storyOne = LayoutInflater.from(this).inflate(R.layout.layout_story, null, false)
storiesView.addView(storyOne)
storiesView.addView(storyTwoTextView())
storiesView
    .setStoryDurationsInSeconds(listOf(2, 1))
    .setCallback(object: StoryCallback{})
    .start()
```

## APIs
#### Builder
- ```setStoryDurationsInSeconds(List<Int>)``` - Sets duration for each story. Default is 5 seconds.
- ```crossFadeDurationInMillis(Long)``` - Enables fade animation on stories switch. By default, there is no animation.
- ```setCallback(StoryCallback)``` - Defines callbacks for story change, end of stories.

#### Action
- ```start(Int)``` - To start a story from a particular index. By default, it starts from the first story.
- ```next()``` - To go to the next story.
- ```previous``` - To go back to the previous story.
- ```pause()``` - To pause a story.
- ```resume``` - To resume a story.
- ```resumeWithNewDuration(Int)``` - To resume a story with the specified duration.
- ```reset``` - To reset the current position of the stories to the first story at index 0.

License
-------

    MIT License

    Copyright (c) 2022 Amar Jain

    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:

    The above copyright notice and this permission notice shall be included in all
    copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
    SOFTWARE.
