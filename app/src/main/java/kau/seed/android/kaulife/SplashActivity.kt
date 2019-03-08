package kau.seed.android.kaulife

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import kau.seed.android.kaulife.TimeTableAlarm.TimeTable.TimeTableActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val handler = Handler()

        handler.postDelayed({

            val intent = Intent (this, TimeTableActivity::class.java)
            startActivity(intent)
            finish()
        }, 3000)
    }
}
