package top.initsnow.simpledict

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageButton
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText

class MyWebView(context: Context, attrs: AttributeSet?) : WebView(context, attrs) {

    private var startX: Float = 0f

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                startX = event.x
            }
            MotionEvent.ACTION_MOVE -> {
                val deltaX = event.x - startX
                if (deltaX > 0) {
                    // 向右滑动
                    if (Math.abs(deltaX) > 200) {
                        // 隐藏 WebView
                        this.visibility = View.GONE
                    }
                } else if (deltaX < 0) {
                    // 向左滑动

                }
            }
        }
        return super.onTouchEvent(event)
    }
}

class MainActivity : AppCompatActivity() {
    private lateinit var editText: TextInputEditText
    private lateinit var webView: WebView
    private lateinit var progressBar: ProgressBar
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editText = findViewById(R.id.editText)
        val settingsButton = findViewById<ImageButton>(R.id.settingsButton)
        webView = findViewById(R.id.webView)
        progressBar = findViewById(R.id.progressBar)

        //设置按钮
        settingsButton.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        // 设置输入框的监听器
        editText.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)
            ) {
                // 处理回车事件
                handleEnterPressed()
                progressBar.visibility = View.VISIBLE
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }

        sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE)
    }

    private fun handleEnterPressed() {

        val url = "${sharedPreferences.getString("url","")}/?q=${editText.text}"
        var count = 0
        // 加载URL并在页面加载完成后执行JavaScript脚本

        webView.settings.javaScriptEnabled = true
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {

                super.onPageFinished(view, url)
                Log.i("a", "加载完成 ${webView.getProgress()} $count")
                // 页面加载完成后执行JavaScript脚本
                val script = """
                function getUrlParams(url) {
                    let urlStr = url.split('?')[1];
                    let obj = {};
                    let paramsArr = urlStr.split('&');
                    for(let i = 0, len = paramsArr.length; i < len; i++){
                        let arr = paramsArr[i].split('=');
                        obj[arr[0]] = arr[1];
                    }
                    return obj;
                }
                const q = decodeURI(getUrlParams(window.location.search)["q"]);
                document.querySelector(".form-control.tt-input").value = q;
                document.querySelector("#btn-search").click();
            """.trimIndent()
                if (count == 0 && webView.getProgress()==100) {
                    view?.loadUrl("javascript:$script")
                } else if (count == 1 && webView.getProgress()==100)  {
                    view?.loadUrl("javascript:${'$'}('body').append('<style>* { background-color: black !important; color: white !important;} .col-sm-3 {display: none;} .container{padding: 0px;} .row{margin: 0px;} .col {padding: 0px;} .card-body {padding: 0px;} </style>');")
                    // 显示WebView
                    progressBar.visibility = View.GONE
                    webView.visibility = View.VISIBLE

                }
                if (webView.getProgress()==100)
                    count++
            }
        }
        webView.settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
        webView.loadUrl(url)
    }
}
