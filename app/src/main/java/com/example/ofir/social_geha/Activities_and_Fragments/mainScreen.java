package com.example.ofir.social_geha.Activities_and_Fragments;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ofir.social_geha.Firebase.Database;
import com.example.ofir.social_geha.Person;
import com.example.ofir.social_geha.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class mainScreen extends AppCompatActivity {

    LinearLayout settings_screen;
    LinearLayout edit_info;
    LinearLayout all_conversations;
    LinearLayout contact_us;
    ImageView profile_pic;
    TextView real_name;
    TextView anonymous_name;

    private static final int LOGIN_RETURN_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        settings_screen = this.findViewById(R.id.settings);
        edit_info = this.findViewById(R.id.edit_info);
        all_conversations = this.findViewById(R.id.current_conversations);
        contact_us = this.findViewById(R.id.contact_us);
        profile_pic = this.findViewById(R.id.overlapImage);
        real_name = this.findViewById(R.id.user_name);
        anonymous_name = this.findViewById(R.id.anonymous_name);

        Database.getInstance().getLoggedInUserID();
        // Load details
        loadUserData();


        ImageView return_home = (ImageView) findViewById(R.id.return_icon);
        return_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if(!Database.getInstance().isLoggedIn()){
            promptLogin();
        }
    }

    private void loadUserData() {
//        Person p = getLoggedInPerson();
//        anonymous_name.setText(p.getAnonymousIdentity().getName());
//        real_name.setText(p.getRealName());
        anonymous_name.setText("כבשה חלקלקה");
        real_name.setText("אופיר אלכסי");

        // Display user image as profile pic
        int default_image = this.getResources().getIdentifier("@drawable/image_fail", null, this.getPackageName() );
        ImageLoader image_loader = ImageLoader.getInstance();
        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisc(true).resetViewBeforeLoading(true)
                .showImageForEmptyUri(default_image)
                .showImageOnFail(default_image)
                .showImageOnLoading(default_image).build();
//        int image_id = this.getResources().getIdentifier("@drawable/" + p.getAnonymousIdentity().getImageName(), null, this.getPackageName() );
        int image_id = R.drawable.sheep_1;
        String photoString = "drawable://" + image_id;
        image_loader.displayImage(photoString , profile_pic, options);
    }

    public void gotoScreen(View view) {
        if (view.equals(settings_screen)) {
            Intent myIntent = new Intent(mainScreen.this, SettingsMainActivity.class);
            mainScreen.this.startActivity(myIntent);
        }
        if (view.equals(edit_info)){
            Intent myIntent = new Intent(mainScreen.this, SettingsInfoEditActivity.class);
            mainScreen.this.startActivity(myIntent);
         }
        if (view.equals(all_conversations)){
            Intent myIntent = new Intent(mainScreen.this, AllChatsActivity.class);
            mainScreen.this.startActivity(myIntent);
        }
        if(view.equals(contact_us)) {
            Toast.makeText(this, "Not implemented yet", Toast.LENGTH_SHORT).show();
        }
    }

    public void promptLogin() {
        Intent intent = new Intent(mainScreen.this, Login.class);
        startActivityForResult(intent, LOGIN_RETURN_CODE);
    }

    private void loginSuccess() {
        Toast.makeText(getApplicationContext(), getString(R.string.strings_succ_login), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOGIN_RETURN_CODE) {
            if (resultCode == RESULT_OK) {
                loginSuccess();
            } else {
                promptLogin();
            }
        }
    }

}
