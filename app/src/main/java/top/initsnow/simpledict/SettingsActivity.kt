package top.initsnow.simpledict

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import kotlin.math.abs

class SettingsActivity : AppCompatActivity() {
    private lateinit var urlEditText: TextInputEditText
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var mGestureDetector: GestureDetector
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        urlEditText = findViewById(R.id.urlEditText)

        sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE)
        val savedUrl = sharedPreferences.getString("url", "")
        urlEditText.setText(savedUrl)

        mGestureDetector = GestureDetector(this@SettingsActivity, object : GestureDetector.SimpleOnGestureListener() {
            override fun onFling(
                e1: MotionEvent?,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                if (e1 != null) {
                    if (e2.x - e1.x > 100 && abs(velocityX) > 300) {
                        finishActivity()
                        return true
                    }
                }
                return false
            }
        })
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return mGestureDetector.onTouchEvent(event) || super.onTouchEvent(event)
    }

    private fun finishActivity() {
        finish()
    }

    override fun onPause() {
        super.onPause()
        val url = urlEditText.text.toString()
        Log.i("a","urlSet:$url")
        val editor = sharedPreferences.edit()
        editor.putString("url", url)
        editor.apply()
    }



}
