package com.example.ofir.social_geha.Activities_and_Fragments;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.ofir.social_geha.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class ZoomedPictureActivity extends AppCompatActivity {
    Toolbar mToolbar;
    String imageURL;
    String imageColor;

    @Override
    public boolean onSupportNavigateUp() {
        mToolbar.setVisibility(View.GONE);
        onBackPressed();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoomed_picture);

        mToolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //display the image from URL
        ImageView imgView = findViewById(R.id.zoomed_profile_picture); //here it's actually rectangular
        imageURL = getIntent().getStringExtra("EXTRA_IMAGE_URL");
        imageColor = getIntent().getStringExtra("EXTRA_IMAGE_COLOR");
        Log.i("PROFILE_ZOOM", imageURL);
        Log.i("PROFILE_ZOOM", imageColor);

        //init loader and loader options
        int default_image = this.getResources().getIdentifier("@drawable/image_fail", null, this.getPackageName() );
        ImageLoader image_loader = ImageLoader.getInstance();
        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisc(true).resetViewBeforeLoading(true)
                .showImageForEmptyUri(default_image)
                .showImageOnFail(default_image)
                .showImageOnLoading(default_image).build();
        image_loader.init(ImageLoaderConfiguration.createDefault(ZoomedPictureActivity.this));

        //download and display image from url
        image_loader.displayImage(imageURL, imgView, options);

        //set color
        imgView.setBackgroundColor(Color.parseColor(imageColor)); //set bg color
    }
}
