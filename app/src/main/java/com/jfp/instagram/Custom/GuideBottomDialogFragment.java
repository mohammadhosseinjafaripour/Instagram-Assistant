package com.jfp.instagram.Custom;

import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.jfp.instagram.R;

import es.dmoral.toasty.Toasty;

public class GuideBottomDialogFragment extends BottomSheetDialogFragment {

    ImageView close_img;
    VideoView fullScreenVideoView;
    static GuideBottomDialogFragment bioBottomDialogFragment;

    public static GuideBottomDialogFragment newInstance() {
        bioBottomDialogFragment = new GuideBottomDialogFragment();
        return bioBottomDialogFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.guide_dialog, container, false);
        find(view);
        Listener();
        return view;

    }

    public void Listener() {
        close_img.setOnClickListener(v -> {
            bioBottomDialogFragment.dismiss();
        });
        close_img.setOnLongClickListener(v -> {
            Toasty.info(getActivity(), "Close The Dialog", Toasty.LENGTH_SHORT).show();
            return false;
        });



        MediaController mediaController = new MediaController(getContext());
        mediaController.setAnchorView(fullScreenVideoView);
        fullScreenVideoView.setMediaController(mediaController);
        fullScreenVideoView.setVideoURI(Uri.parse("android.resource://" + getContext().getPackageName() + "/" + R.raw.guide));
        fullScreenVideoView.start();


       /* fullScreenVideoView.setOnPreparedListener(mp -> {
            mp.setLooping(true);
        });
        */

    }


    public void find(View view) {
        close_img = view.findViewById(R.id.close_img);
        fullScreenVideoView = view.findViewById(R.id.video_view);
    }

}