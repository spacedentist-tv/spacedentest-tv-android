package tv.spacedentist.android.view

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.Log

import androidx.appcompat.widget.AppCompatButton

/**
 * A custom Button so we can set the font
 */
class SDButtonView(context: Context, attrs: AttributeSet) : AppCompatButton(context, attrs) {

    init {
        try {
            val tf = Typeface.createFromAsset(context.assets, "fonts/BebasNeue.ttf")
            typeface = tf
        } catch (e: Exception) {
            Log.e(TAG, "Couldn't load font", e)
        }

    }

    companion object {
        private val TAG = "SDButtonView"
    }
}
