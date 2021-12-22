package weekend.coder.library.utils

import android.content.Context
import android.content.res.Resources
import android.util.TypedValue

internal fun Float.toDp(context: Context): Int {
    val r = context.resources
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this,
        r.displayMetrics
    ).toInt()
}

internal fun Int.dpToPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()