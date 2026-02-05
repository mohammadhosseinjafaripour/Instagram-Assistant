package com.jfp.instagram.Custom;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.jfp.instagram.Config;
import com.jfp.instagram.R;
import com.jfp.instagram.Utils;

import es.dmoral.toasty.Toasty;

public class CaptionBottomDialogFragment extends BottomSheetDialogFragment {

    ImageView close_img,ic_bio;
    TextView bio_text;
    Button copy_bio;
    static CaptionBottomDialogFragment bioBottomDialogFragment;

    public static CaptionBottomDialogFragment newInstance() {
        bioBottomDialogFragment = new CaptionBottomDialogFragment();
        return bioBottomDialogFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.bio_dialog, container, false);
        find(view);
        Listener();
        init();
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
        copy_bio.setOnClickListener(v -> {
            Utils.setClipboard(getActivity(), bio_text.getText().toString());
            Toasty.success(getActivity(), "Copied", Toasty.LENGTH_SHORT).show();
        });
    }

    public void init()
    {
        bio_text.setText(Config.caption);
    }

    public void find(View view) {
        close_img = view.findViewById(R.id.close_img);
        bio_text = view.findViewById(R.id.bio_text);
        copy_bio = view.findViewById(R.id.copy_bio);
        ic_bio = view.findViewById(R.id.ic_bio);
        ic_bio.setImageResource(R.drawable.ic_caption);

    }
}