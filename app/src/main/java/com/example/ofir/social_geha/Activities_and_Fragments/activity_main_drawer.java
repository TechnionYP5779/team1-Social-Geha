package com.example.ofir.social_geha.Activities_and_Fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ofir.social_geha.AppStorageManipulation;
import com.example.ofir.social_geha.Firebase.Database;
import com.example.ofir.social_geha.Person;
import com.example.ofir.social_geha.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class activity_main_drawer extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

        // FRAGMENT TAGS
        public static String ALL_CHATS_TAG = "ALL_CHATS_FRAGMENT";
        public static String EDIT_INFO_TAG = "EDIT_INFO_FRAGMENT";
        public static String ABOUT_TAG = "INFO_FRAGMENT";

        Person p;
        TextView anonymousName;
        TextView readlName;
        CircleImageView profile_pic;
        NavigationView navigationView;
        Toolbar mToolbar;
        MenuItem mSearch;
        // For AllChatsFragment
        public ArrayList<ChatEntry> conversationList;
        public ArrayList<ChatEntry> allList;
        public ChatListAdapter mAdapter;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_main_drawer);

                mToolbar = findViewById(R.id.toolbar);
                setSupportActionBar(mToolbar);

                final DrawerLayout drawer = findViewById(R.id.drawer_layout);
                ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                        this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
                drawer.addDrawerListener(toggle);
                toggle.syncState();

                navigationView = findViewById(R.id.nav_view);

                navigationView.setNavigationItemSelectedListener(this);

                View headView = navigationView.getHeaderView(0);
                profile_pic = headView.findViewById(R.id.imgProfile);
                anonymousName = headView.findViewById(R.id.textView_anonymous);
                readlName = headView.findViewById(R.id.textView_real_name);

                loadData();

                //default fragment for home
                android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.flMain,new AllChatsFragment(), ALL_CHATS_TAG);
                ft.commit();

                navigationView.setCheckedItem(R.id.nav_inbox);
        }

        public void setActionBarTitle(String title) {
                getSupportActionBar().setTitle(title);
        }

        @Override
        public void onBackPressed() {
                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                } else {
                        super.onBackPressed();
                }
        }


        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
                // Handle action bar item clicks here. The action bar will
                // automatically handle clicks on the Home/Up button, so long
                // as you specify a parent activity in AndroidManifest.xml.
                return super.onOptionsItemSelected(item);
        }

        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
                // Handle navigation view item clicks here.
                int id = item.getItemId();

                if (id == R.id.nav_about) {
                        mSearch.setVisible(false);
                        android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.flMain,new FragmentInfo(),ABOUT_TAG);
                        ft.commit();
                } else if (id == R.id.nav_inbox) {
                        mSearch.setVisible(true);
                        android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.flMain,new AllChatsFragment(),ALL_CHATS_TAG);
                        ft.commit();
                } else if (id == R.id.edit_information){
                        mSearch.setVisible(false);
                        android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.flMain,new EditInfoFragment(),EDIT_INFO_TAG);
                        ft.commit();
                } else if (id == R.id.nav_delete_account){
                        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this, R.style.AlertDialogCustomDark);
                        mBuilder.setMessage("האם אתה בטוח שברצונך למחוק את החשבון?");

                        mBuilder.setCancelable(false);
                        mBuilder.setPositiveButton(R.string.agree, (dialogInterface, which) -> {
                                FirebaseFirestore.getInstance().collection("users").document(Database.getInstance().getLoggedInUserID()).delete();
                                AppStorageManipulation.deleteAppData(getApplicationContext());
                                Intent intent = new Intent(activity_main_drawer.this, Login.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                        });

                        mBuilder.setNegativeButton(R.string.dont_agree, (dialogInterface, i) -> dialogInterface.dismiss());
                        AlertDialog mDialog = mBuilder.create();
                        mDialog.show();

                }

                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return true;
        }

        public void selectNavigationDrawer(int position){
                if(position == R.id.nav_inbox){
                        mSearch.setVisible(true);
                }
                else{
                        mSearch.setVisible(false);
                }
                navigationView.setCheckedItem(position);
        }

        public void loadData() {
                Database.getInstance().getdb().collection("users").whereEqualTo("userID", Database.getInstance().getLoggedInUserID()).get()
                        .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot doc : task.getResult()) {
                                                p = doc.toObject(Person.class);
                                                anonymousName.setText(p.getAnonymousIdentity().getName());
                                                readlName.setText(p.getRealName());

                                                // Display user image as profile pic
                                                int default_image = activity_main_drawer.this.getResources().getIdentifier("@drawable/image_fail", null, activity_main_drawer.this.getPackageName());
                                                ImageLoader image_loader = ImageLoader.getInstance();
                                                image_loader.init(ImageLoaderConfiguration.createDefault(activity_main_drawer.this));
                                                DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                                                        .cacheOnDisc(true).resetViewBeforeLoading(true)
                                                        .showImageForEmptyUri(default_image)
                                                        .showImageOnFail(default_image)
                                                        .showImageOnLoading(default_image).build();
                                                int image_id = activity_main_drawer.this.getResources().getIdentifier("@drawable/" + p.getAnonymousIdentity().getImageName(), null, activity_main_drawer.this.getPackageName());
                                                final String photoString = "drawable://" + image_id;
                                                Log.i("PROFILE_PICTURE", photoString);
                                                image_loader.displayImage(photoString, profile_pic, options); //display no_bg image
                                                profile_pic.setCircleBackgroundColor(Color.parseColor("#6ebae1"));

                                                //make sure the profile picture clicks and zooms
                                                final ImageView viewStart = profile_pic;
                                                profile_pic.setOnClickListener(v -> {
                                                        Intent intent = new Intent(activity_main_drawer.this, ZoomedPictureActivity.class);
                                                        String transitionName = getString(R.string.transition_string);
                                                        ActivityOptionsCompat options1 =
                                                                ActivityOptionsCompat.makeSceneTransitionAnimation(activity_main_drawer.this,
                                                                        viewStart,   // Starting view
                                                                        transitionName    // The String
                                                                );
                                                        intent.putExtra("EXTRA_IMAGE_URL", photoString);
                                                        intent.putExtra("EXTRA_IMAGE_COLOR", p.getAnonymousIdentity().imageColor); //pass the color to the zoom animation
                                                        ActivityCompat.startActivity(activity_main_drawer.this, intent, options1.toBundle());
                                                });
                                        }
                                }
                        });
        }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
                getMenuInflater().inflate(R.menu.toolbar_menu_search, menu);
                // Set up search icon
                mSearch = menu.findItem(R.id.action_search);
                SearchView mSearchView = (SearchView) mSearch.getActionView();
                mSearchView.setQueryHint("חיפוש");

                mSearch.setVisible(true); // Because the first screen allchats

                mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                        @Override
                        public boolean onQueryTextSubmit(String query) {
                                return false;
                        }

                        @Override
                        public boolean onQueryTextChange(String newText) {
                                        conversationList.clear();
                                        for (ChatEntry chatEntry : allList) {
                                                Log.d("PEOPLE", chatEntry.getName());
                                                if (chatEntry.getName().trim().toLowerCase().contains(newText.trim().toLowerCase())) {
                                                        conversationList.add(chatEntry);
                                                }
                                        }
                                        mAdapter.notifyDataSetChanged();
                                return true;
                        }
                });
                return super.onCreateOptionsMenu(menu);
        }

}
