package weekend.coder.library.utils

import android.view.View
import android.view.ViewPropertyAnimator

fun View.crossFade(fadeInView: View, crossFadeDuration: Long, isFadeOutViewOnTop: Boolean): ViewPropertyAnimator {
    if (isFadeOutViewOnTop) {
        fadeInView.visibility = View.VISIBLE
        return animate()
            .alpha(0f)
            .setUpdateListener { valueAnimator ->
                fadeInView.alpha = valueAnimator.animatedValue as Float
            }
            .withEndAction {
                this.visibility = View.GONE
            }
            .setDuration(crossFadeDuration)
            .setListener(null)
    } else {
        fadeInView.alpha = 0f
        fadeInView.visibility = View.VISIBLE
        return fadeInView.animate()
            .alpha(1f)
            .setUpdateListener { valueAnimator ->
                this.alpha = 1.minus(valueAnimator.animatedValue as Float)
            }
            .withEndAction {
                this.visibility = View.GONE
            }
            .setDuration(crossFadeDuration)
            .setListener(null)
    }
}