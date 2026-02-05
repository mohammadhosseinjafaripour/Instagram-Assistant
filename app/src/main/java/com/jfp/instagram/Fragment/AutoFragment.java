package com.jfp.instagram.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.jfp.instagram.Config;
import com.jfp.instagram.R;
import com.jfp.instagram.Service.ClipboardMonitorService;
import com.jfp.instagram.Utils;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;
import es.dmoral.toasty.Toasty;

public class AutoFragment extends Fragment {

    CircularProgressButton btn_auto;
    TextView status;
    ImageView tap_img;

    public AutoFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_auto, container, false);
        Config.current_tab = AutoFragment.class.getSimpleName();
        findView(view);
        checkPreferences();
        Listener();
        return view;
    }

    public void findView(View view) {
        btn_auto = view.findViewById(R.id.btn_auto);
        status = view.findViewById(R.id.status);
        tap_img = view.findViewById(R.id.tap_img);
        Config.btn_auto = btn_auto;
        Config.status = status;
    }

    public void Listener() {
        btn_auto.setOnClickListener(v -> {
            if (Utils.checkPermissionForReadExternalStorage(getContext())) {
                if (!Config.status_flag) {
                    Config.status_flag = true;
                    btn_auto.startAnimation();
                    Handler handler = new Handler();
                    handler.postDelayed(() -> {
                        btn_auto.doneLoadingAnimation(Color.parseColor("#4CAF50"), Utils.getBitmapFromVectorDrawable(getContext(), R.drawable.ic_done));
                        Intent intent = new Intent(getContext(), ClipboardMonitorService.class);
                        intent.setAction("Start");
                        getContext().startService(intent);
                        update("true");
                        status.setText("Status : On");
                        status.setTextColor(Color.parseColor("#4CAF50"));
                        btn_auto.revertAnimation();
                        btn_auto.setBackground(getResources().getDrawable(R.drawable.rounded_corners_yellow));
                        Handler handler1 = new Handler();
                        handler1.postDelayed(() -> btn_auto.setText("Turn Off"), 1000);

                    }, 3000);
                } else {
                    Config.status_flag = false;
                    btn_auto.startAnimation();
                    Handler handler = new Handler();
                    handler.postDelayed(() -> {
                        btn_auto.doneLoadingAnimation(Color.parseColor("#4CAF50"), Utils.getBitmapFromVectorDrawable(getContext(), R.drawable.ic_done));
                        Intent intent = new Intent(getContext(), ClipboardMonitorService.class);
                        intent.setAction("Stop");
                        getContext().startService(intent);
                        update("false");
                        status.setText("Status : Off");
                        status.setTextColor(Color.parseColor("#E91E63"));
                        btn_auto.revertAnimation();
                        btn_auto.setBackground(getResources().getDrawable(R.drawable.rounded_corners_yellow));
                        Handler handler1 = new Handler();
                        handler1.postDelayed(() -> btn_auto.setText("Turn On"), 1000);
                    }, 3000);
                }
            } else {
                Utils.requestPermissionForReadExternalStorage(getContext());
            }
        });

        blink();
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            tap_img.clearAnimation();
            tap_img.animate().alpha(0);
        }, 9000);

    }

    public void checkPreferences() {
        SharedPreferences prefs = getContext().getSharedPreferences("Insta", Context.MODE_PRIVATE);
        String st = prefs.getString("status", "null");
        if (st.equals("null")) {
            update("false");
            Config.status_flag = false;
        } else if (st.equals("false")) {
            Config.status_flag = false;
            btn_auto.setText("Turn On");
            status.setText("Status : Off");
            status.setTextColor(Color.parseColor("#E91E63"));
        } else if (st.equals("true")) {
            Config.status_flag = true;
            btn_auto.setText("Turn Off");
            status.setText("Status : On");
            status.setTextColor(Color.parseColor("#4CAF50"));

        }
    }

    public void update(String status) {
        SharedPreferences.Editor editor = getContext().getSharedPreferences("Insta", Context.MODE_PRIVATE).edit();
        editor.putString("status", status);
        editor.apply();
    }

    public void blink() {
        Animation animation = new AlphaAnimation(1, 0);
        animation.setDuration(500);
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(Animation.INFINITE);
        animation.setRepeatMode(Animation.REVERSE);
        tap_img.startAnimation(animation);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 100: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toasty.success(getContext(), "Access Granted", Toasty.LENGTH_SHORT).show();
                } else {
                    Toasty.error(getContext(), "Without permission we can't download file", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

}
