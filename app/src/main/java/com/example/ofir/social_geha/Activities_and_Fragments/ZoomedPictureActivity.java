package com.example.ofir.social_geha.Activities_and_Fragments;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.example.ofir.social_geha.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ZoomedPictureActivity extends AppCompatActivity {

    String imageURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoomed_picture);

        //display the image from URL
        ImageView imgView = findViewById(R.id.zoomed_profile_picture);
        imageURL = getIntent().getStringExtra("EXTRA_IMAGE_URL");
        Log.i("PROFILE_ZOOM", imageURL);

        int default_image = this.getResources().getIdentifier("@drawable/image_fail", null, this.getPackageName() );
        ImageLoader image_loader = ImageLoader.getInstance();
        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisc(true).resetViewBeforeLoading(true)
                .showImageForEmptyUri(default_image)
                .showImageOnFail(default_image)
                .showImageOnLoading(default_image).build();

        //download and display image from url
        image_loader.displayImage(imageURL, imgView, options);
    }
}
