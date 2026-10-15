package com.example.jarvis

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.AlarmClock
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import java.util.Locale

class MainActivity : Activity(), TextToSpeech.OnInitListener {
    private lateinit var web: WebView
    private var tts: TextToSpeech? = null
    private val requestSpeech = 42

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tts = TextToSpeech(this, this)
        web = WebView(this)
        web.settings.javaScriptEnabled = true
        web.settings.domStorageEnabled = true
        web.webChromeClient = WebChromeClient()
        web.webViewClient = WebViewClient()
        web.addJavascriptInterface(Bridge(), "Jarvis")
        setContentView(web)
        web.loadUrl("file:///android_asset/index.html")
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) tts?.language = Locale.getDefault()
    }

    inner class Bridge {
        @JavascriptInterface fun speak(text: String) {
            runOnUiThread { tts?.speak(text.take(500), TextToSpeech.QUEUE_FLUSH, null, "jarvisSpeech") }
        }
        @JavascriptInterface fun listen() {
            runOnUiThread {
                if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), 7)
                    Toast.makeText(this@MainActivity, "Allow microphone permission, then tap the mic again.", Toast.LENGTH_LONG).show()
                    return@runOnUiThread
                }
                try {
                    val i = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                    i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                    i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                    i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to JARVIS")
                    startActivityForResult(i, requestSpeech)
                } catch (_: Exception) {
                    Toast.makeText(this@MainActivity, "Speech recognition is unavailable on this device.", Toast.LENGTH_LONG).show()
                }
            }
        }
        @JavascriptInterface fun openApp(name: String) {
            runOnUiThread {
                val intent = when (name.lowercase()) {
                    "youtube" -> launch("com.google.android.youtube")
                    "whatsapp" -> launch("com.whatsapp")
                    "chrome" -> launch("com.android.chrome")
                    "maps" -> launch("com.google.android.apps.maps")
                    "camera" -> Intent("android.media.action.IMAGE_CAPTURE")
                    "settings" -> Intent(android.provider.Settings.ACTION_SETTINGS)
                    "play store" -> launch("com.android.vending")
                    else -> null
                }
                try {
                    if (intent != null) startActivity(intent)
                    else Toast.makeText(this@MainActivity, "App shortcut not configured: $name", Toast.LENGTH_SHORT).show()
                } catch (_: Exception) {
                    Toast.makeText(this@MainActivity, "Could not open $name. Is it installed?", Toast.LENGTH_SHORT).show()
                }
            }
        }
        @JavascriptInterface fun search(query: String) {
            runOnUiThread {
                val i = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=" + Uri.encode(query)))
                startActivity(i)
            }
        }
        @JavascriptInterface fun setAlarm(hour: Int, minute: Int) {
            runOnUiThread {
                val i = Intent(AlarmClock.ACTION_SET_ALARM)
                    .putExtra(AlarmClock.EXTRA_HOUR, hour.coerceIn(0,23))
                    .putExtra(AlarmClock.EXTRA_MINUTES, minute.coerceIn(0,59))
                    .putExtra(AlarmClock.EXTRA_MESSAGE, "JARVIS reminder")
                try { startActivity(i) } catch (_: Exception) {
                    Toast.makeText(this@MainActivity, "No compatible clock app found.", Toast.LENGTH_SHORT).show()
                }
            }
        }
        private fun launch(pkg: String): Intent? = packageManager.getLaunchIntentForPackage(pkg)
    }

    @Deprecated("Deprecated in Android API")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == requestSpeech && resultCode == RESULT_OK) {
            val said = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull().orEmpty()
            web.evaluateJavascript("window.onJarvisHeard(${org.json.JSONObject.quote(said)})", null)
        }
    }
    override fun onDestroy() {
        tts?.stop(); tts?.shutdown()
        web.destroy()
        super.onDestroy()
    }
}
