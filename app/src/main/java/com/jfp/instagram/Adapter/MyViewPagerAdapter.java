package com.jfp.instagram.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.jfp.instagram.Config;
import com.jfp.instagram.Custom.FullScreenVideoView;
import com.jfp.instagram.Broadcast.DownloadReceiver;
import com.jfp.instagram.Model.InstaModel;
import com.jfp.instagram.R;
import com.jfp.instagram.Service.DownloadService;
import com.jfp.instagram.Utils;

import java.util.List;

import es.dmoral.toasty.Toasty;

public class MyViewPagerAdapter extends PagerAdapter {

    public Context context;
    public List<InstaModel> instaModels;
    CardView download_img;
    String type = "";

    public MyViewPagerAdapter(Context context, List<InstaModel> instaModels) {
        this.context = context;
        this.instaModels = instaModels;
        Config.fullScreenVideoView = new FullScreenVideoView[getCount()];
    }

    public void Listener(int position) {
        download_img.setOnLongClickListener(v -> {
            Toasty.info(context, "Download Single " + type + " File").show();
            return true;
        });
        download_img.setOnClickListener(v -> {
            if (Utils.checkPermissionForReadExternalStorage(context)) {
                Intent intent = new Intent(context, DownloadService.class);
                intent.putExtra("url", instaModels.get(position).url);
                intent.putExtra("type", instaModels.get(position).is_video);
                intent.putExtra("receiver", new DownloadReceiver(new Handler(), context));
                context.startService(intent);
            } else {
                Utils.requestPermissionForReadExternalStorage(context);
            }


        });
    }

    @Override
    public Object instantiateItem(@NonNull ViewGroup collection, int position) {
        LayoutInflater inflater = LayoutInflater.from(context);
        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.viewpager_layout, collection, false);
        ImageView img = layout.findViewById(R.id.img);
        download_img = layout.findViewById(R.id.download_img);
        FullScreenVideoView videoView = layout.findViewById(R.id.videoView);
        Config.fullScreenVideoView[position] = videoView;
        if (instaModels.get(position).is_video.equals("false")) {
            img.setVisibility(View.VISIBLE);
            videoView.setVisibility(View.GONE);
            Glide.with(context).load(instaModels.get(position).url).into(img);
            type = "Image";
        } else {
            img.setVisibility(View.GONE);
            videoView.setVisibility(View.VISIBLE);
            videoView.setVideoPath(instaModels.get(position).getUrl());
            videoView.setOnPreparedListener(mp -> {
                mp.setLooping(true);
            });
            type = "Video";
        }
        collection.addView(layout);
        Listener(position);
        return layout;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object view) {
        container.removeView((View) view);
    }

    @Override
    public int getCount() {
        return instaModels.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }


}