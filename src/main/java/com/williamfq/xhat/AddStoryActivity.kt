package com.williamfq.xhat

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.williamfq.data.dao.StoryDao
import com.williamfq.data.entities.MediaType
import com.williamfq.data.entities.Story
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.ads.AdListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.android.gms.ads.nativead.NativeAdOptions

@AndroidEntryPoint
class AddStoryActivity : AppCompatActivity() {

    @Inject
    lateinit var storyDao: StoryDao

    private lateinit var etStoryTitle: EditText
    private lateinit var etStoryDescription: EditText
    private lateinit var btnUploadStory: Button

    private var nativeAd: NativeAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_story)

        etStoryTitle = findViewById(R.id.etStoryTitle)
        etStoryDescription = findViewById(R.id.etStoryDescription)
        btnUploadStory = findViewById(R.id.btnUploadStory)

        btnUploadStory.setOnClickListener {
            val title = etStoryTitle.text.toString().trim()
            val description = etStoryDescription.text.toString().trim()
            uploadStory(title, description)
        }

        loadNativeAd()
    }

    private fun loadNativeAd() {
        val adLoader = AdLoader.Builder(this, "ca-app-pub-2587938308176637/4265820740") // Reemplazar con ID real
            .forNativeAd { ad: NativeAd ->
                nativeAd?.destroy()
                nativeAd = ad
                val adView = layoutInflater.inflate(R.layout.native_ad_layout, null) as NativeAdView
                populateNativeAdView(ad, adView)
                findViewById<LinearLayout>(R.id.adContainer).addView(adView)
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(error: LoadAdError) {
                    Toast.makeText(this@AddStoryActivity, "Error al cargar anuncio: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
            .withNativeAdOptions(NativeAdOptions.Builder().build())
            .build()

        adLoader.loadAd(AdRequest.Builder().build())
    }

    private fun populateNativeAdView(nativeAd: NativeAd, adView: NativeAdView) {
        adView.headlineView = adView.findViewById(R.id.ad_headline)
        adView.mediaView = adView.findViewById(R.id.ad_media)
        adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)

        (adView.headlineView as TextView).text = nativeAd.headline
        adView.mediaView?.mediaContent = nativeAd.mediaContent
        adView.callToActionView?.let {
            (it as Button).text = nativeAd.callToAction
            it.visibility = if (nativeAd.callToAction != null) View.VISIBLE else View.INVISIBLE
        }

        adView.setNativeAd(nativeAd)
    }

    private fun uploadStory(title: String, description: String) {
        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
        } else {
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val currentUserId = getCurrentUserId()
                    val currentTimestamp = System.currentTimeMillis()
                    val mediaUrl = "https://example.com/default.jpg"
                    val mediaType = MediaType.IMAGE

                    val newStory = Story(
                        userId = currentUserId,
                        title = title,
                        description = description,
                        mediaUrl = mediaUrl,
                        mediaType = mediaType,
                        timestamp = currentTimestamp,
                        isActive = true,
                        views = 0,
                        duration = 24,
                        tags = listOf("example", "story"),
                        comments = emptyList(),
                        reactions = emptyList(),
                        poll = null
                    )

                    storyDao.insertStory(newStory)

                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@AddStoryActivity,
                            "Historia subida exitosamente: $title",
                            Toast.LENGTH_SHORT
                        ).show()
                        etStoryTitle.text.clear()
                        etStoryDescription.text.clear()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@AddStoryActivity,
                            "Error al subir la historia: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private suspend fun getCurrentUserId(): String {
        return "12345"
    }

    override fun onDestroy() {
        super.onDestroy()
        nativeAd?.destroy()
    }
}
