package tv.spacedentist.android.view

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.Log

import androidx.appcompat.widget.AppCompatTextView

/**
 * A custom text view so that we can set the font
 */
class SDTextView(context: Context, attr: AttributeSet) : AppCompatTextView(context, attr) {

    init {
        try {
            val tf = Typeface.createFromAsset(context.assets, "fonts/BebasNeue.ttf")
            typeface = tf
        } catch (e: Exception) {
            Log.e(TAG, "Couldn't load font", e)
        }

    }

    companion object {
        private val TAG = "SDTextView"
    }
}
