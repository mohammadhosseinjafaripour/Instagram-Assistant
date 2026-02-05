package com.jfp.instagram.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.jfp.instagram.Activity.SplashActivity;
import com.jfp.instagram.Broadcast.ServiceNotificationListener;
import com.jfp.instagram.Config;
import com.jfp.instagram.Broadcast.DownloadReceiver;
import com.jfp.instagram.Model.InstaModel;
import com.jfp.instagram.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class ClipboardMonitorService extends Service {
    private static final String TAG = "ClipboardManager";
    private ClipboardManager mClipboardManager;
    boolean is_download = false;
    List<InstaModel> instaModels;

    @Override
    public void onCreate() {
        super.onCreate();


        mClipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        mClipboardManager.addPrimaryClipChangedListener(mOnPrimaryClipChangedListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mClipboardManager != null) {
            mClipboardManager.removePrimaryClipChangedListener(
                    mOnPrimaryClipChangedListener);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!intent.getAction().equals("Stop")) {
            startForeground(1054, getNotification());
        } else {
            stopForeground(true);
            stopSelf();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private ClipboardManager.OnPrimaryClipChangedListener mOnPrimaryClipChangedListener = new ClipboardManager.OnPrimaryClipChangedListener() {
        @Override
        public void onPrimaryClipChanged() {
            Log.d(TAG, "onPrimaryClipChanged");
            ClipData clip = mClipboardManager.getPrimaryClip();
            if (clip.getItemAt(0).getText().toString().startsWith("https://www.instagram.com")) {
                getJson(clip.getItemAt(0).getText().toString());
            }
        }
    };

    public void getJson(String insta_share_link) {
        String url = "https://programchi.ir/InstaApi/instagram.php?url=" + insta_share_link + "&key=" + getString(R.string.parse_key);
        JsonArrayRequest request = new JsonArrayRequest(url, response -> {
            instaModels = new ArrayList<>();
            for (int i = 0; i < response.length(); i++) {
                try {
                    JSONObject jsonObject = response.getJSONObject(i);

                    String caption = jsonObject.getString("caption");
                    String video = jsonObject.getString("video");
                    String _url = jsonObject.getString("url");

                    Config.caption = caption;

                    instaModels.add(new InstaModel(video, _url, caption));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                is_download = true;
                downloadAll();
            }

        }, error -> {
            Toasty.error(getApplicationContext(), "Try Again Later Something Happened...", Toast.LENGTH_SHORT).show();
        });
        Volley.newRequestQueue(getApplicationContext()).add(request);
    }

    public void downloadAll() {
        if (is_download) {
            for (int i = 0; i < instaModels.size(); i++) {

                Intent intent = new Intent(getApplicationContext(), DownloadService.class);
                intent.putExtra("url", instaModels.get(i).url);
                intent.putExtra("type", instaModels.get(i).is_video);
                intent.putExtra("receiver", new DownloadReceiver(new Handler(), getApplicationContext()));
                startService(intent);


            }
        } else {
            Toasty.error(getApplicationContext(), "No Data Received Still...", Toasty.LENGTH_SHORT).show();
        }
    }

    private Notification getNotification() {

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, SplashActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationChannel channel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = new NotificationChannel("insta_01", "InstaAssistant", NotificationManager.IMPORTANCE_DEFAULT);
        }

        NotificationManager notificationManager = null;


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            notificationManager = getSystemService(NotificationManager.class);
        } else {
            notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            notificationManager.createNotificationChannel(channel);
        }

        Notification.Builder builder = null;
        Intent switchIntent = new Intent(this, ServiceNotificationListener.class);
        switchIntent.setAction("Close");
        //PendingIntent servicenotificationlistener = PendingIntent.getBroadcast(getApplicationContext(), 0, switchIntent, 0);
        PendingIntent servicenotificationlistener = PendingIntent.getBroadcast(getApplicationContext(),1,switchIntent,PendingIntent.FLAG_UPDATE_CURRENT);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new Notification.Builder(getApplicationContext(), "insta_01").setAutoCancel(true);
            builder.setContentTitle("Auto Instagram Downloader is Running")
                    .setContentText("Tap to open the app.")
                    .setSmallIcon(R.drawable.ic_notification)
                    .setColor(Color.parseColor("#FFA000"))
                    .setAutoCancel(true)
                    .setOngoing(true)
                    .addAction(R.drawable.ic_power_off, "Turn Off", servicenotificationlistener)
                    .setContentIntent(contentIntent)
                    .setStyle(new Notification.BigTextStyle(builder).bigText("Tap to open the app."));
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new Notification.Builder(getApplicationContext());
                builder.setContentTitle("Auto Instagram Downloader is Running")
                        .setContentText("Tap to open the app.")
                        .setAutoCancel(true)
                        .setContentIntent(contentIntent)
                        .setOngoing(true)
                        .addAction(R.drawable.ic_power_off, "Turn Off", servicenotificationlistener)
                        .setStyle(new Notification.BigTextStyle(builder).bigText("Tap to open the app."))
                        .setSmallIcon(R.drawable.ic_notification)
                        .setColor(Color.parseColor("#FFA000"));
            }
        }
        return builder.build();
    }
}

