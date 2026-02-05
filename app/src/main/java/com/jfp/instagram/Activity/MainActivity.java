package com.jfp.instagram.Activity;

import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.jfp.instagram.Config;
import com.jfp.instagram.Fragment.AutoFragment;
import com.jfp.instagram.Fragment.ImageVideoFragment;
import com.jfp.instagram.Fragment.ProfileFragment;
import com.jfp.instagram.Fragment.StoryFragment;
import com.jfp.instagram.R;
import com.jfp.instagram.Utils;

import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity {


    CardView instagram_card;
    TextView profile_tab, image_video_tab, story_tab;
    Boolean exit = false;
    ImageView profile, story, video, instagram, auto;
    MeowBottomNavigation bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        replaceFragment(new ProfileFragment());
        find();
        Listener();
        bottomNavigation.show(1, true);

    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);

        fragmentTransaction.replace(R.id.fragment_layer, fragment);
        fragmentTransaction.commit();


    }

    public void find() {
        instagram_card = findViewById(R.id.instagram_card);
        profile_tab = findViewById(R.id.profile_tab);
        image_video_tab = findViewById(R.id.image_video_tab);
        story_tab = findViewById(R.id.story_tab);

        profile = findViewById(R.id.profile);
        story = findViewById(R.id.story);
        video = findViewById(R.id.video);
        instagram = findViewById(R.id.instagram);
        auto = findViewById(R.id.auto);

    }

    @Override
    public void onBackPressed() {
        if (exit) {
            super.onBackPressed();
            return;
        }
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            exit = false;
        }, 3000);
        Toasty.info(MainActivity.this, "Please click BACK again to exit", Toasty.LENGTH_SHORT).show();
        exit = true;
    }

    public void Listener() {
        bottomNavigation = findViewById(R.id.meowbottom);
        bottomNavigation.add(new MeowBottomNavigation.Model(1, R.drawable.ic_profile_icon));
        bottomNavigation.add(new MeowBottomNavigation.Model(2, R.drawable.ic_story_icon));
        bottomNavigation.add(new MeowBottomNavigation.Model(3, R.drawable.ic_video_icon));
        bottomNavigation.add(new MeowBottomNavigation.Model(4, R.drawable.ic_instagram_icon));
        bottomNavigation.add(new MeowBottomNavigation.Model(5, R.drawable.ic_auto_icon));

        profile.setOnClickListener(v -> {
            bottomNavigation.show(1, true);
            if (!Config.current_tab.equals(ProfileFragment.class.getSimpleName())) {
                replaceFragment(new ProfileFragment());
            }
        });

        profile.setOnLongClickListener(v -> {
            Toasty.info(MainActivity.this, "Profile Downloader", Toasty.LENGTH_SHORT).show();
            return true;
        });

        story.setOnClickListener(v -> {
            bottomNavigation.show(2, true);
            if (!Config.current_tab.equals(StoryFragment.class.getSimpleName())) {
                replaceFragment(new StoryFragment());
            }
        });

        story.setOnLongClickListener(v -> {
            Toasty.info(MainActivity.this, "Story Downloader", Toasty.LENGTH_SHORT).show();
            return true;
        });

        video.setOnClickListener(v -> {
            bottomNavigation.show(3, true);
            if (!Config.current_tab.equals(ImageVideoFragment.class.getSimpleName())) {
                replaceFragment(new ImageVideoFragment());
            }
        });

        video.setOnLongClickListener(v -> {
            Toasty.info(MainActivity.this, "Image & Video Downloader", Toasty.LENGTH_SHORT).show();
            return true;
        });

        instagram.setOnClickListener(v -> {
            bottomNavigation.show(4, true);
            Utils.OpenInstagram(MainActivity.this);
        });

        instagram.setOnLongClickListener(v -> {
            Toasty.info(MainActivity.this, "Open Instagram", Toasty.LENGTH_SHORT).show();
            return true;
        });

        auto.setOnClickListener(v -> {
            bottomNavigation.show(5, true);
            if (!Config.current_tab.equals(AutoFragment.class.getSimpleName())) {
                replaceFragment(new AutoFragment());
            }
        });

        auto.setOnLongClickListener(v -> {
            Toasty.info(MainActivity.this, "Auto Downloader", Toasty.LENGTH_SHORT).show();
            return true;
        });
    }
}
