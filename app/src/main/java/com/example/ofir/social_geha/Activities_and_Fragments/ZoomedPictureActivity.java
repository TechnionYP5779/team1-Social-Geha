package com.example.ofir.social_geha.Activities_and_Fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.example.ofir.social_geha.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

/**
 * This is an activity which displays a profile picture enlarged. Whenever someone navigates to it,
 * they must specify in the intent the picture (url & color) they wish to display
 */
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

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //extracts the image to display from the intent extra information
        ImageView imgView = findViewById(R.id.zoomed_profile_picture); //here it's actually a rectangular image view
        imageURL = getIntent().getStringExtra("EXTRA_IMAGE_URL");
        imageColor = getIntent().getStringExtra("EXTRA_IMAGE_COLOR");

        //init loader and loader options to display the image correctly
        int default_image = this.getResources().getIdentifier("@drawable/image_fail", null, this.getPackageName() );
        ImageLoader image_loader = ImageLoader.getInstance();
        //noinspection deprecation
        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true) //pretty standard options
                .cacheOnDisc(true).resetViewBeforeLoading(true)
                .showImageForEmptyUri(default_image)
                .showImageOnFail(default_image)
                .showImageOnLoading(default_image).build();
        image_loader.init(ImageLoaderConfiguration.createDefault(ZoomedPictureActivity.this));

        //display image from the url specified
        image_loader.displayImage(imageURL, imgView, options);

        //set the desired color to the image background
        imgView.setBackgroundColor(Color.parseColor(imageColor)); //set bg color
    }
}
