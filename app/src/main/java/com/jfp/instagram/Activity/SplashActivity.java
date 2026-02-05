package com.jfp.instagram.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.jfp.instagram.R;
import com.jfp.instagram.Service.ClipboardMonitorService;

import java.io.File;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        setPostDelay();

        File file = new File(Environment.getExternalStorageDirectory() + "/InstaAssistant");
        if (file.exists() && file.isDirectory()) {
            File[] pictures = file.listFiles();
        } else {
            // no file exist
        }


    }

    public void setPostDelay() {
        int SPLASH_DISPLAY_LENGTH = 2000;
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            SplashActivity.this.startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            SplashActivity.this.finish();
        }, SPLASH_DISPLAY_LENGTH);
    }
}
