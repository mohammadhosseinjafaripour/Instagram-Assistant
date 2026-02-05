package com.jfp.instagram.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jfp.instagram.Config;
import com.jfp.instagram.Custom.BioBottomDialogFragment;
import com.jfp.instagram.R;
import com.jfp.instagram.Utils;

import org.json.JSONException;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import es.dmoral.toasty.Toasty;


public class ProfileFragment extends Fragment {
    CardView download_profile_btn, biography_profile_btn;
    EditText insta_id_edt;
    ImageView search_img, profile_img, loading, paste_img;
    Activity activity;
    Boolean pic_downloaded = false;
    String bio = "";
    TextView picture_path, profile_txt;
    int[] default_padding = new int[4];


    public ProfileFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        Config.current_tab = ProfileFragment.class.getSimpleName();
        find(view);
        Listener();
        return view;
    }

    public void find(View view) {
        insta_id_edt = view.findViewById(R.id.insta_id_edt);
        search_img = view.findViewById(R.id.search_img);
        profile_img = view.findViewById(R.id.profile_img);
        download_profile_btn = view.findViewById(R.id.download_profile_btn);
        biography_profile_btn = view.findViewById(R.id.biography_profile_btn);
        picture_path = view.findViewById(R.id.picture_path);
        loading = view.findViewById(R.id.loading);
        paste_img = view.findViewById(R.id.paste_img);
        profile_txt = view.findViewById(R.id.profile_txt);
        activity = getActivity();
    }

    public void Listener() {


        search_img.setOnClickListener(v -> {
            if (Utils.isNetworkAvailable(activity)) {
                if (insta_id_edt.getText().toString().length() != 0) {
                    Utils.hideKeyboard(activity);
                    getProfilePic(insta_id_edt.getText().toString().trim());
                } else {
                    Toasty.warning(activity, "Write USERNAME !", Toasty.LENGTH_SHORT).show();
                }
            } else {
                Toasty.error(activity, "No Internet Connection", Toasty.LENGTH_SHORT).show();
            }
        });

        insta_id_edt.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (Utils.isNetworkAvailable(activity)) {
                    if (insta_id_edt.getText().toString().length() != 0) {
                        Utils.hideKeyboard(activity);
                        getProfilePic(insta_id_edt.getText().toString().trim());
                    } else {
                        Toasty.warning(activity, "Write USERNAME !", Toasty.LENGTH_SHORT).show();
                    }
                } else {
                    Toasty.error(activity, "No Internet Connection", Toasty.LENGTH_SHORT).show();
                }
            }
            return false;
        });
        insta_id_edt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null) {
                    if (s.toString().length() != 0) {
                        profile_txt.animate().alpha(0);
                    } else {
                        profile_txt.animate().alpha(1);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        download_profile_btn.setOnClickListener(v -> {
            if (Utils.checkPermissionForReadExternalStorage(activity)) {
                //save to sd
                if (pic_downloaded)
                    saveToSd();
                else
                    Toasty.warning(activity, "No Picture Downloaded Still...", Toasty.LENGTH_SHORT).show();
            } else {
                Utils.requestPermissionForReadExternalStorage(activity);
            }
        });

        biography_profile_btn.setOnClickListener(v -> {
            if (pic_downloaded && bio.length() == 0) {
                Toasty.info(activity, "user Bio is empty !", Toasty.LENGTH_SHORT).show();
            } else if (pic_downloaded) {
                Config.bio = bio;
                BioBottomDialogFragment bioBottomDialogFragment = BioBottomDialogFragment.newInstance();
                bioBottomDialogFragment.show(getFragmentManager(), "bioBottomDialogFragment");
            } else {
                Toasty.warning(activity, "No Data Received Still...", Toasty.LENGTH_SHORT).show();
            }
        });

        paste_img.setOnClickListener(v -> insta_id_edt.setText(Utils.pasteClipboard(getContext())));
        paste_img.setOnLongClickListener(v -> {
            Toasty.info(getContext(), "Paste ID From Clipboard", Toasty.LENGTH_SHORT).show();
            return true;
        });
    }

    public void getProfilePic(String id) {
        String url = "https://www.instagram.com/" + id + "/?__a=1";
        loading.setVisibility(View.VISIBLE);
        profile_img.setImageResource(0);
        Glide.with(getActivity()).asGif().load(R.raw.laoding).fitCenter().diskCacheStrategy(DiskCacheStrategy.NONE).into(loading);
        JsonObjectRequest request = new JsonObjectRequest(url, null, response -> {
            loading.setVisibility(View.GONE);
            try {
                response = response.getJSONObject("graphql").getJSONObject("user");
                String biography = response.getString("biography") + "";
                String profile_pic = response.getString("profile_pic_url_hd");
                String full_name = response.getString("full_name");

                Glide.with(activity).asBitmap().load(profile_pic).into(profile_img);


                profile_img.setPadding(0, 0, 0, 0);
                pic_downloaded = true;
                bio = biography;

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Toasty.error(activity, "User Not Found", Toasty.LENGTH_SHORT).show();
            profile_img.setImageResource(R.drawable.ic_not_found);
            int paddingPixel = Utils.paddingDp(getContext(), 60);
            profile_img.setPadding(paddingPixel, paddingPixel, paddingPixel, paddingPixel);
            loading.setVisibility(View.GONE);

        });
        request.setShouldCache(false);
        Volley.newRequestQueue(activity).add(request);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 100: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    saveToSd();
                } else {
                    Toasty.error(activity, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    public void saveToSd() {
        BitmapDrawable btmpDr = (BitmapDrawable) profile_img.getDrawable();
        Bitmap bmp = btmpDr.getBitmap();

        try {
            File sdCardDirectory = new File(Environment.getExternalStorageDirectory() + File.separator + "InstaAssistant" + File.separator + "Profile");
            sdCardDirectory.mkdirs();
            String imageNameForSDCard;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String currentDateandTime = sdf.format(new Date());
//            imageNameForSDCard = "image_" + String.valueOf(random.nextInt(1000)) + System.currentTimeMillis() + ".jpg";
            imageNameForSDCard = "image_" + currentDateandTime + ".jpg";

            File image = new File(sdCardDirectory, imageNameForSDCard);
            FileOutputStream outStream;

            outStream = new FileOutputStream(image);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            outStream.flush();
            outStream.close();
            Toasty.success(getActivity(), "Picture Saved Completely", Toasty.LENGTH_SHORT).show();
            picture_path.setVisibility(View.VISIBLE);
            picture_path.setText("Path : InstaAssistant/Profile/Images/" + imageNameForSDCard);
            activity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory())));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
