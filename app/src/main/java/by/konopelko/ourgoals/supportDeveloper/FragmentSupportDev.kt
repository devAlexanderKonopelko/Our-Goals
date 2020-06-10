package by.konopelko.ourgoals.supportDeveloper

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import by.konopelko.ourgoals.R
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import kotlinx.android.synthetic.main.fragment_supportdev.*

class FragmentSupportDev: DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_supportdev, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        MobileAds.initialize(context)

        val interstitialAd = InterstitialAd(this.context)
        interstitialAd.adUnitId = getString(R.string.fullscreen_banner_id)
        interstitialAd.loadAd(AdRequest.Builder().build())

        val interstitialAdListener = object : AdListener() {

            override fun onAdFailedToLoad(errorCode: Int) {
                Toast.makeText(
                    this@FragmentSupportDev.context,
                    "Ошибка загрузки рекламы",
                    Toast.LENGTH_SHORT)
                    .show()
            }

            override fun onAdClosed() {
                interstitialAd.loadAd(AdRequest.Builder().build())
            }
        }

        interstitialAd.adListener = interstitialAdListener

        supportDevAdvButton.setOnClickListener {
            if (interstitialAd.isLoaded) {
                interstitialAd.show()
            }
        }
    }
}