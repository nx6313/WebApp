package com.mtxyao.nxx.webapp

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.ImageView
import com.youth.banner.BannerConfig
import com.youth.banner.Transformer
import com.youth.banner.loader.ImageLoader
import kotlinx.android.synthetic.main.activity_banner.*

class BannerActivity : AppCompatActivity() {
    private var images: MutableList<Int> = mutableListOf(R.drawable.guide_1, R.drawable.guide_2, R.drawable.guide_3)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_banner)

        banner.setImageLoader(GlideImageLoader())
        banner.setImages(images)
        banner.setBannerStyle(BannerConfig.NOT_INDICATOR)
        banner.setBannerAnimation(Transformer.Tablet)
        banner.isAutoPlay(false)
        banner.start()
        banner.setOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                if (position == 2) {
                    if (guideBtn.visibility != View.VISIBLE) {
                        val mShowAction = TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                                Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF, 0.0f)
                        mShowAction.duration = 400
                        guideBtn.startAnimation(mShowAction)
                        guideBtn.visibility = View.VISIBLE
                    }
                }
            }
        })
    }

    fun startUse (view: View) {
        val loginIntent = Intent(this, LoginActivity::class.java)
        startActivity(loginIntent)
        finish()
    }

    class GlideImageLoader : ImageLoader() {
        override fun displayImage(context: Context?, path: Any?, imageView: ImageView?) {
            imageView!!.setImageResource(path as Int)
        }
    }
}
