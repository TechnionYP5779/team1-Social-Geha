package com.example.ofir.social_geha.Activities_and_Fragments;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ofir.social_geha.Firebase.Database;
import com.example.ofir.social_geha.Firebase.Message;
import com.example.ofir.social_geha.R;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class ChatActivity extends AppCompatActivity {
    private static final String MESSAGES = "messages";
    private EditText mMessageEdit;
    private RecyclerView mMessageRecycler;
    private FirebaseFirestore mFirestore;
    private MessageListAdapter mMessageListAdapter;
    private String mOtherPersonId;
    private String mAnonymousOtherPhotoUrl;
    private String mAnonymousOtherName;
    private String mLoggedInPersonId;
    private FileHandler fileHandler;
    private List<Message> messageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat2);

        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Display user image as profile pic
        int default_image = this.getResources().getIdentifier("@drawable/image_fail", null, this.getPackageName());
        ImageLoader image_loader = ImageLoader.getInstance();
        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisc(true).resetViewBeforeLoading(true)
                .showImageForEmptyUri(default_image)
                .showImageOnFail(default_image)
                .showImageOnLoading(default_image).build();

        //Display user name and photo
        mAnonymousOtherName = getIntent().getStringExtra("EXTRA_NAME");
        mAnonymousOtherPhotoUrl = getIntent().getStringExtra("EXTRA_PHOTO_URL");
        int image_id = this.getResources().getIdentifier("@drawable/" + mAnonymousOtherPhotoUrl, null, this.getPackageName());
        mAnonymousOtherPhotoUrl = "drawable://" + image_id;
        ImageView image_holder = findViewById(R.id.user_photo);
        TextView name_holder = findViewById(R.id.user_title);

        name_holder.setText(mAnonymousOtherName);
        image_loader.displayImage(mAnonymousOtherPhotoUrl, image_holder, options);
        final View viewStart = image_holder;
        final String imageURL = mAnonymousOtherPhotoUrl;

        //make sure the profile picture clicks and zooms
        image_holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatActivity.this, ZoomedPictureActivity.class);
                String transitionName = getString(R.string.transition_string);
                ActivityOptionsCompat options =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(ChatActivity.this,
                                viewStart,   // Starting view
                                transitionName    // The String
                        );
                intent.putExtra("EXTRA_IMAGE_URL", imageURL);

                //Start the Intent
                ActivityCompat.startActivity(ChatActivity.this, intent, options.toBundle());
            }
        });

        // HANDLE THE CHAT
        mLoggedInPersonId = Database.getInstance().getLoggedInUserID();
        messageList = new ArrayList<>();
        fileHandler = new FileHandler(this);
        mMessageListAdapter = new MessageListAdapter(messageList);
        mOtherPersonId = getIntent().getStringExtra("EXTRA_PERSON_ID");
        Log.d("POPO", "onCreate: " + mOtherPersonId);
        mMessageEdit = findViewById(R.id.message_text);
        mMessageRecycler = findViewById(R.id.message_list);
        mMessageRecycler.setHasFixedSize(true);
        mMessageRecycler.setLayoutManager(new LinearLayoutManager(this));
        mMessageRecycler.setAdapter(mMessageListAdapter);

        mFirestore = FirebaseFirestore.getInstance();

        mFirestore.collection(MESSAGES).whereEqualTo("toPersonID", mLoggedInPersonId)
                .whereEqualTo("fromPersonID", mOtherPersonId)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.d("COOLTEST", "Error: " + e.getMessage());
                            return;
                        }

                        for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                            if (doc.getType() == DocumentChange.Type.ADDED) {
                                String message_text = doc.getDocument().getString("message");
                                Log.d("COOLTEST", "Content: " + message_text);
                                Log.d("POPO", "onEvent: DOES THIS");
                                Message message = doc.getDocument().toObject(Message.class);
                                mFirestore.collection(MESSAGES).document(doc.getDocument().getId()).delete();
                                fileHandler.writeMessage(message);
                                mMessageListAdapter.notifyDataSetChanged();
                            }
                        }

                    }
                });

        mFirestore.collection(MESSAGES).whereEqualTo("fromPersonID", mLoggedInPersonId)
                .whereEqualTo("toPersonID", mOtherPersonId)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.d("COOLTEST", "Error: " + e.getMessage());
                            return;
                        }

                        for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                            if (doc.getType() == DocumentChange.Type.ADDED) {
                                //String message = doc.getDocument().getString("message");
                                //Log.d("COOLTEST","Content: " + message);
                                Message message = doc.getDocument().toObject(Message.class);
                                messageList.add(message);
                            }
                        }
                        mMessageListAdapter.notifyDataSetChanged();
                    }
                });

        for (Message msg : fileHandler.getMessages()) {
            Log.d("COOLTEST", "readMessage: " + msg.getMessage() + "from: " + msg.getFromPersonID());
            if (msg.getFromPersonID().equals(mOtherPersonId) || msg.getToPersonID().equals(mOtherPersonId)) {
                messageList.add(msg);
            }
        }
        mMessageListAdapter.notifyDataSetChanged();
    }

    public void onSendButtonClick(View v) {
        String message = mMessageEdit.getText().toString();
        Database.getInstance().sendMessage(message, mLoggedInPersonId, mOtherPersonId);
        fileHandler.writeMessage(new Message(message, mLoggedInPersonId, mOtherPersonId));
        mMessageEdit.setText("");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
