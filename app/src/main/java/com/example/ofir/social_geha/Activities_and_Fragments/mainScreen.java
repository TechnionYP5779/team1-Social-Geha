package com.example.ofir.social_geha.Activities_and_Fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ofir.social_geha.Firebase.Database;
import com.example.ofir.social_geha.Person;
import com.example.ofir.social_geha.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.w3c.dom.Text;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class mainScreen extends AppCompatActivity {

    LinearLayout share_info;
    LinearLayout edit_info;
    LinearLayout delete_account;
    LinearLayout about;
    CircleImageView profile_pic;
    TextView real_name;
    TextView anonymous_name;
    Person p;

    private static final int LOGIN_RETURN_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        share_info = this.findViewById(R.id.share_info);
        edit_info = this.findViewById(R.id.edit_info);
        delete_account = this.findViewById(R.id.delete_my_account);
        about = this.findViewById(R.id.about);
        profile_pic = this.findViewById(R.id.overlapImage);
        real_name = this.findViewById(R.id.user_name);
        anonymous_name = this.findViewById(R.id.anonymous_name);

        // Load details
        loadUserData(); //initializes the field p


        ImageView return_home = findViewById(R.id.return_icon);
        return_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if (!Database.getInstance().isLoggedIn()) {
            promptLogin();
        }
    }

    // Stores into p the loaded user
    private void loadUserData() {
        final ProgressBar pb = this.findViewById(R.id.mainscreen_pb);
        final TextView loading = this.findViewById(R.id.loading_txt);
        pb.setVisibility(View.VISIBLE);
        loading.setVisibility(View.VISIBLE);

        Log.i("LOGGED_IN_UID", Database.getInstance().getLoggedInUserID());
        Database.getInstance().getdb().collection("users").whereEqualTo("userID", Database.getInstance().getLoggedInUserID()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                p = doc.toObject(Person.class);
                                anonymous_name.setText(p.getAnonymousIdentity().getName());
                                real_name.setText(p.getRealName());

                                // Display user image as profile pic

                                int default_image = mainScreen.this.getResources().getIdentifier("@drawable/image_fail", null, mainScreen.this.getPackageName());
                                ImageLoader image_loader = ImageLoader.getInstance();
                                image_loader.init(ImageLoaderConfiguration.createDefault(mainScreen.this));
                                DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                                        .cacheOnDisc(true).resetViewBeforeLoading(true)
                                        .showImageForEmptyUri(default_image)
                                        .showImageOnFail(default_image)
                                        .showImageOnLoading(default_image).build();
                                int image_id = mainScreen.this.getResources().getIdentifier("@drawable/" + p.getAnonymousIdentity().getImageName(), null, mainScreen.this.getPackageName());
                                final String photoString = "drawable://" + image_id;
                                Log.i("PROFILE_PICTURE", photoString);
                                image_loader.displayImage(photoString, profile_pic, options); //display no_bg image
                                profile_pic.setCircleBackgroundColor(Color.parseColor(p.getAnonymousIdentity().imageColor));
//                                profile_pic.setBackgroundColor(); //set color

                                //make sure the profile picture clicks and zooms
                                final ImageView viewStart = profile_pic;
                                profile_pic.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(mainScreen.this, ZoomedPictureActivity.class);
                                        String transitionName = getString(R.string.transition_string);
                                        ActivityOptionsCompat options =
                                                ActivityOptionsCompat.makeSceneTransitionAnimation(mainScreen.this,
                                                        viewStart,   // Starting view
                                                        transitionName    // The String
                                                );
                                        intent.putExtra("EXTRA_IMAGE_URL", photoString);
                                        intent.putExtra("EXTRA_IMAGE_COLOR", p.getAnonymousIdentity().imageColor); //pass the color to the zoom animation
                                        ActivityCompat.startActivity(mainScreen.this, intent, options.toBundle());
                                    }
                                });

                                pb.setVisibility(View.GONE);
                                loading.setVisibility(View.GONE);
                                profile_pic.setVisibility(View.VISIBLE);
                                LinearLayout buttons_layout = findViewById(R.id.buttons_layout);
                                buttons_layout.setVisibility(View.VISIBLE);
                            }
                        } else {
                            loading.setText(mainScreen.this.getString(R.string.strings_error));
                        }
                    }
                });
    }

    public void gotoScreen(View view) {
        final ArrayList<Integer> mUserItems = new ArrayList<>();
        if (view.equals(share_info)) {
            String[] usersAll = {"אבי ישראלי", "ענת רימון"};
            final boolean[] usersChecked = {true, true};


            AlertDialog.Builder mBuilder = new AlertDialog.Builder(mainScreen.this, R.style.AlertDialogCustom);
            mBuilder.setMultiChoiceItems(usersAll, usersChecked, new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                    if (isChecked) {
                        mUserItems.add(position);
                    } else {
                        mUserItems.remove((Integer.valueOf(position)));
                    }
                }
            });

            mBuilder.setCancelable(false);
            mBuilder.setPositiveButton(R.string.ok_label, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int which) {
                }
            });

            mBuilder.setNegativeButton(R.string.dismiss_label, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });

            mBuilder.setNeutralButton(R.string.clear_all_label, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int which) {
                    for (int i = 0; i < usersChecked.length; i++) {
                        usersChecked[i] = false;
                        mUserItems.clear();
                    }
                }
            });

            AlertDialog mDialog = mBuilder.create();
            mDialog.show();
        }
        if (view.equals(edit_info)) {
            Intent myIntent = new Intent(mainScreen.this, SettingsInfoEditActivity.class);
            mainScreen.this.startActivity(myIntent);
        }
        if (view.equals(delete_account)) {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(mainScreen.this, R.style.AlertDialogCustom);
            mBuilder.setMessage("האם אתה בטוח שברצונך למחוק את החשבון?");

            mBuilder.setCancelable(false);
            mBuilder.setPositiveButton(R.string.agree, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int which) {

                }
            });

            mBuilder.setNegativeButton(R.string.dont_agree, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });

            AlertDialog mDialog = mBuilder.create();
            mDialog.show();
            Toast.makeText(this, "deletion not implemented yet", Toast.LENGTH_SHORT).show();
        }
        if (view.equals(about)) {
            Intent myIntent = new Intent(mainScreen.this, SettingsHelp.class);
            mainScreen.this.startActivity(myIntent);
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
