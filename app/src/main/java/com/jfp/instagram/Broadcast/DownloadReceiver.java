package com.jfp.instagram.Broadcast;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.view.View;
import android.widget.Toast;

import com.jfp.instagram.Config;
import com.jfp.instagram.Service.DownloadService;

import es.dmoral.toasty.Toasty;

public class DownloadReceiver extends ResultReceiver {

    Context context;

    public DownloadReceiver(Handler handler, Context context) {
        super(handler);
        this.context = context;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {

        super.onReceiveResult(resultCode, resultData);

        if (resultCode == DownloadService.UPDATE_PROGRESS) {

            int progress = resultData.getInt("progress"); //get the progress

            if (progress == 100) {
                Toasty.success(context, "File Downloaded", Toast.LENGTH_SHORT).show();
                if (Config.path != null)
                    Config.path.setVisibility(View.VISIBLE);
                if (Config.mode != null) {
                    if (Config.type.equals("true")) {
                        if (Config.path != null)
                            Config.path.setText("Path : InstaAssistant/Story/Video" + Config.file_path);
                    } else {
                        if (Config.path != null)
                            Config.path.setText("Path : InstaAssistant/Story/Image" + Config.file_path);
                    }
                } else {
                    if (Config.type.equals("true")) {
                        if (Config.path != null)
                            Config.path.setText("Path : InstaAssistant/Video" + Config.file_path);
                    } else {
                        if (Config.path != null)
                            Config.path.setText("Path : InstaAssistant/Image" + Config.file_path);
                    }
                }
            }
        }
    }
}