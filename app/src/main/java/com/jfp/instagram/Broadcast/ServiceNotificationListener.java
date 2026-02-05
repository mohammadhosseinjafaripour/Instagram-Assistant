package com.jfp.instagram.Broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.widget.Toast;

import com.jfp.instagram.Config;
import com.jfp.instagram.R;
import com.jfp.instagram.Service.ClipboardMonitorService;
import com.jfp.instagram.Utils;

public class ServiceNotificationListener extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("Close")) {
            if (Config.btn_auto != null) {
                Config.status_flag = false;
                Config.btn_auto.startAnimation();
                Handler handler = new Handler();
                handler.postDelayed(() -> {
                    Config.btn_auto.doneLoadingAnimation(Color.parseColor("#4CAF50"), Utils.getBitmapFromVectorDrawable(context, R.drawable.ic_done));
                    Intent intent3 = new Intent(context, ClipboardMonitorService.class);
                    intent3.setAction("Stop");
                    context.startService(intent3);
                    SharedPreferences.Editor editor = context.getSharedPreferences("Insta", Context.MODE_PRIVATE).edit();
                    editor.putString("status", "false");
                    editor.apply();
                    Config.status.setText("Status : Off");
                    Config.status.setTextColor(Color.parseColor("#E91E63"));
                    Config.btn_auto.revertAnimation();
                    Config.btn_auto.setBackground(context.getResources().getDrawable(R.drawable.rounded_corners_yellow));
                    Handler handler1 = new Handler();
                    handler1.postDelayed(() -> Config.btn_auto.setText("Turn On"), 1000);
                }, 3000);

            }else {
                SharedPreferences.Editor editor = context.getSharedPreferences("Insta", Context.MODE_PRIVATE).edit();
                editor.putString("status", "false");
                editor.apply();
                Intent intent2 = new Intent(context, ClipboardMonitorService.class);
                intent2.setAction("Stop");
                context.startService(intent2);
            }

        }

    }
}