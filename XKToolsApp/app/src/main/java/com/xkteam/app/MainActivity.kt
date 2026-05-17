package com.xktools.app

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import rikka.shizuku.Shizuku
import android.os.Handler
import android.os.Looper

class MainActivity : AppCompatActivity() {
    private lateinit var webView: WebView
    private val shellExecutor = ShellExecutor()
    private val mainHandler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (!Shizuku.pingBinder()) {
            Toast.makeText(this, "Shizuku tidak berjalan! Aktifkan Shizuku.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        webView = findViewById(R.id.webView)
        webView.settings.javaScriptEnabled = true
        webView.webViewClient = WebViewClient()
        
        webView.addJavascriptInterface(object {
            @android.webkit.JavascriptInterface
            fun execShellAsync(command: String, callbackId: String) {
                Thread {
                    val result = try {
                        shellExecutor.exec(command)
                    } catch (e: Exception) {
                        "Error: ${e.message}"
                    }
                    mainHandler.post {
                        webView.evaluateJavascript("window._shizukuCallback('$callbackId', '${result.replace("'", "\\'")}')", null)
                    }
                }.start()
            }

            @android.webkit.JavascriptInterface
            fun showToast(message: String) {
                mainHandler.post {
                    Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
                }
            }
        }, "XKShell")

        webView.loadUrl("file:///android_asset/dashboard.html")
    }
}