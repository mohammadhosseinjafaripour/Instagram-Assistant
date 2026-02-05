package com.jfp.instagram.Fragment;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jfp.instagram.Adapter.MyViewPagerAdapter;
import com.jfp.instagram.Broadcast.DownloadReceiver;
import com.jfp.instagram.Config;
import com.jfp.instagram.Model.InstaModel;
import com.jfp.instagram.R;
import com.jfp.instagram.Service.DownloadService;
import com.jfp.instagram.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;


public class StoryFragment extends Fragment {
    ViewPager vpPager;
    TextView pager_number_tv;
    LinearLayout count_container;
    TextView path;
    ImageView search_img, loading, paste_img;
    RelativeLayout default_img;
    EditText insta_link_edt;
    CardView download_all_btn;
    List<InstaModel> instaModels;
    Boolean is_download = false;
    LinearLayout story;

    public StoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_story, container, false);
        Config.current_tab = StoryFragment.class.getSimpleName();
        find(view);
        //prepareViewPager();
        Listener();
        return view;
    }

    public void find(View view) {
        vpPager = view.findViewById(R.id.viewpager_main);
        pager_number_tv = view.findViewById(R.id.pager_number_tv);
        count_container = view.findViewById(R.id.count_container);
        path = view.findViewById(R.id.path);
        insta_link_edt = view.findViewById(R.id.insta_link_edt);
        search_img = view.findViewById(R.id.search_img);
        download_all_btn = view.findViewById(R.id.download_all_btn);
        loading = view.findViewById(R.id.loading);
        story = view.findViewById(R.id.story);
        default_img = view.findViewById(R.id.default_img);
        paste_img = view.findViewById(R.id.paste_img);
        Config.path = path;
    }

    public void Listener() {
        search_img.setOnLongClickListener(v -> {
            Toasty.info(getContext(), "Search for Story", Toasty.LENGTH_SHORT).show();
            return true;
        });
        search_img.setOnClickListener(v -> {
            if (Utils.isNetworkAvailable(getContext())) {
                if (insta_link_edt.getText().toString().length() != 0) {
                    getJson(insta_link_edt.getText().toString().trim());
                    Utils.hideKeyboard(getActivity());

                } else {
                    Toasty.error(getContext(), "Please Paste ID !", Toasty.LENGTH_SHORT).show();
                }
            } else {
                Toasty.error(getContext(), "No Internet Connection", Toasty.LENGTH_SHORT).show();
            }
        });
        download_all_btn.setOnClickListener(v -> {
            if (Utils.checkPermissionForReadExternalStorage(getContext())) {
                downloadAll();
            } else {
                Utils.requestPermissionForReadExternalStorage(getContext());
            }
        });
        download_all_btn.setOnLongClickListener(v -> {
            Toasty.info(getContext(), "Download All Story", Toasty.LENGTH_SHORT).show();
            return true;
        });
        insta_link_edt.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (Utils.isNetworkAvailable(getContext())) {
                    if (insta_link_edt.getText().toString().length() != 0) {
                        getJson(insta_link_edt.getText().toString().trim());
                        Utils.hideKeyboard(getActivity());

                    } else {
                        Toasty.error(getContext(), "Please Paste ID !", Toasty.LENGTH_SHORT).show();
                    }
                } else {
                    Toasty.error(getContext(), "No Internet Connection", Toasty.LENGTH_SHORT).show();
                }
            }
            return false;
        });
        paste_img.setOnClickListener(v -> insta_link_edt.setText(Utils.pasteClipboard(getContext())));
        paste_img.setOnLongClickListener(v -> {
            Toasty.info(getContext(), "Paste Link From Clipboard", Toasty.LENGTH_SHORT).show();
            return true;
        });

    }

    public void getJson(String id) {
        String url = "https://programchi.ir/InstaApi/story.php?username=" + id + "&key=" + getString(R.string.parse_key);
        default_img.animate().alpha(0);
        Handler handler = new Handler();
        handler.postDelayed(() -> default_img.setVisibility(View.GONE), 500);
        Glide.with(getActivity()).asGif().load(R.raw.laoding).fitCenter().diskCacheStrategy(DiskCacheStrategy.NONE).into(loading);
        loading.setVisibility(View.VISIBLE);
        JsonArrayRequest request = new JsonArrayRequest(url, response -> {
            loading.setVisibility(View.GONE);
            instaModels = new ArrayList<>();
            for (int i = 0; i < response.length(); i++) {
                try {
                    JSONObject jsonObject = response.getJSONObject(i);

                    String video = jsonObject.getString("video");
                    String _url = jsonObject.getString("url");

                    instaModels.add(new InstaModel(video, _url));


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            prepareViewPager(instaModels);
            is_download = true;
        }, error -> {
            Toasty.error(getContext(), "User Not Found With Any Story", Toast.LENGTH_SHORT).show();
            loading.setVisibility(View.GONE);
            default_img.animate().alpha(1);
        });
        request.setShouldCache(false);
        Volley.newRequestQueue(getContext()).add(request);
    }

    public void prepareViewPager(List<InstaModel> instaModels) {
        vpPager.setClipToPadding(false);
        MyViewPagerAdapter adapterViewPager = new MyViewPagerAdapter(getActivity(), instaModels);
        vpPager.setAdapter(adapterViewPager);
        int max = adapterViewPager.getCount();
        Handler handler = new Handler();

        vpPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                count_container.setVisibility(View.VISIBLE);
                count_container.setAlpha(1.0f);
                pager_number_tv.setText((position + 1) + "/" + max);
                handler.postDelayed(() -> count_container.animate().setDuration(500).alpha(0.0f), 4000);


                if (instaModels.get(position).getIs_video().equals("true")) {
                    for (int i = 0; i < instaModels.size(); i++) {
                        if (Config.fullScreenVideoView[i] != null) {
                            if (Config.fullScreenVideoView[i].isPlaying()) {
                                Config.fullScreenVideoView[i].stopPlayback();
                            }
                        }
                    }
                    Config.fullScreenVideoView[position].animate().alpha(1);
                    Config.fullScreenVideoView[position].seekTo(0);
                    Handler handler1 = new Handler();
                    handler1.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Config.fullScreenVideoView[position].start();
                            Config.fullScreenVideoView[position].resume();
                        }
                    }, 300);

                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 100: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    downloadAll();
                } else {
                    Toasty.error(getContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    public void downloadAll() {
        if (is_download) {
            for (int i = 0; i < instaModels.size(); i++) {
                Intent intent = new Intent(getContext(), DownloadService.class);
                intent.putExtra("url", instaModels.get(i).url);
                intent.putExtra("type", instaModels.get(i).is_video);
                intent.putExtra("mode", "story");
                intent.putExtra("receiver", new DownloadReceiver(new Handler(), getContext()));
                getContext().startService(intent);
            }
        } else {
            Toasty.error(getContext(), "No Data Received Still...", Toasty.LENGTH_SHORT).show();
        }
    }

}
