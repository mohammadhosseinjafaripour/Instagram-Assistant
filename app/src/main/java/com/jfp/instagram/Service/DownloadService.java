package com.jfp.instagram.Service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.ResultReceiver;

import com.jfp.instagram.Config;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class DownloadService extends IntentService {
    public static final int UPDATE_PROGRESS = 8344;

    public DownloadService() {
        super("DownloadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        String urlToDownload = intent.getStringExtra("url");
        String type = intent.getStringExtra("type");
        String mode = intent.getStringExtra("mode");
        ResultReceiver receiver = intent.getParcelableExtra("receiver");
        try {

            URL url = new URL(urlToDownload);
            URLConnection connection = url.openConnection();
            connection.connect();

            int fileLength = connection.getContentLength();

            InputStream input = new BufferedInputStream(connection.getInputStream());

            File sdCardDirectory = null;
            Config.type = type;
            if (mode != null) {
                Config.mode = "ok";
                if (type.equals("true")) {
                    sdCardDirectory = new File(Environment.getExternalStorageDirectory() + File.separator + "InstaAssistant" + File.separator + "Story" + File.separator + "Videos");
                } else {
                    sdCardDirectory = new File(Environment.getExternalStorageDirectory() + File.separator + "InstaAssistant" + File.separator + "Story" + File.separator + "Images");
                }
            } else {
                if (type.equals("true")) {
                    sdCardDirectory = new File(Environment.getExternalStorageDirectory() + File.separator + "InstaAssistant" + File.separator + "Videos");
                } else {
                    sdCardDirectory = new File(Environment.getExternalStorageDirectory() + File.separator + "InstaAssistant" + File.separator + "Images");
                }
            }
            sdCardDirectory.mkdirs();
            String imageNameForSDCard = "";
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String currentDateandTime = sdf.format(new Date());
            Random random = new Random();
            String long_name = String.valueOf(random.nextInt(1000)) + System.currentTimeMillis();

            if (type.equals("true")) {
                imageNameForSDCard = "video_" + currentDateandTime + long_name + ".mp4";
            } else {
                imageNameForSDCard = "img_" + currentDateandTime + long_name + ".jpg";

            }
            Config.file_path = imageNameForSDCard;

            File video = new File(sdCardDirectory, imageNameForSDCard);

            OutputStream output = new FileOutputStream(video);

            byte data[] = new byte[1024];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                total += count;
                Bundle resultData = new Bundle();
                resultData.putInt("progress", (int) (total * 100 / fileLength));
                receiver.send(UPDATE_PROGRESS, resultData);
                output.write(data, 0, count);
            }

            output.flush();
            output.close();
            input.close();


        } catch (IOException e) {
            e.printStackTrace();
        }

        Bundle resultData = new Bundle();
        resultData.putInt("progress", 100);

        receiver.send(UPDATE_PROGRESS, resultData);
    }
}