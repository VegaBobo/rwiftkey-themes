package com.rswiftkey

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity


class InstallIntent: ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = Intent(this@InstallIntent, MainActivity::class.java)
        intent.data = getIntent().data
        startActivity(intent)
        finish()
    }
}