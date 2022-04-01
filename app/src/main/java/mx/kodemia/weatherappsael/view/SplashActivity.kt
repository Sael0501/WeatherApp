package mx.kodemia.weatherappsael.view

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowInsets
import android.view.WindowManager
import android.view.animation.AnimationUtils
import kotlinx.android.synthetic.main.activity_splash.*
import mx.kodemia.weatherappsael.R

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val splashAnimation = AnimationUtils.loadAnimation(this, R.anim.splash_anim)
        val tvMovement = AnimationUtils.loadAnimation(this, R.anim.tv_translate)
        logoImageView.startAnimation(splashAnimation)
        tvSplash.startAnimation(tvMovement)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            @Suppress("DEPRECATION")
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        @Suppress("DEPRECATION")
        Handler().postDelayed(
            {
                startActivity(
                    Intent(
                        this@SplashActivity, MainActivity::class.java
                    )
                )
                finish()
            }, 3000
        )
    }

}