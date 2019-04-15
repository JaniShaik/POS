package com.medianet.qrcodedemo.Activity

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import com.medianet.qrcodedemo.R

class LauncherActivity : AppCompatActivity() {
    // Context
    val mContext: Context = this


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_launcher)
        // To display launcher activity for some time.....
        Thread {
            Thread.sleep(500)
            val intent = Intent(this@LauncherActivity, LoginActivity::class.java)
            startActivity(intent)
        }.start()
    }
}
