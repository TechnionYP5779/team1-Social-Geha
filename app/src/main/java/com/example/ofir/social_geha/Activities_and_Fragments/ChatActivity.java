package com.example.ofir.social_geha.Activities_and_Fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.ofir.social_geha.Activities_and_Fragments.FileHandlers.ContactListFileHandler;
import com.example.ofir.social_geha.Activities_and_Fragments.FileHandlers.KeyFileHandler;
import com.example.ofir.social_geha.Activities_and_Fragments.FileHandlers.MessageFileHandler;
import com.example.ofir.social_geha.Encryption.AES;
import com.example.ofir.social_geha.Firebase.Database;
import com.example.ofir.social_geha.Firebase.Message;
import com.example.ofir.social_geha.R;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Nullable;
import javax.crypto.SecretKey;

import de.hdodenhof.circleimageview.CircleImageView;


/*
This class is responsible for the Chat part of the app.
 */
public class ChatActivity extends AppCompatActivity {
    private static final String MESSAGES = "messages";
    private EditText mMessageEdit;
    private RecyclerView mMessageRecycler;
    private FirebaseFirestore mFirestore;
    private MessageListAdapter mMessageListAdapter;
    private String mOtherPersonId;
    private String mAnonymousOtherPhotoUrl;
    private String mAnonymousOtherPhotoColor;
    private String mAnonymousOtherName;
    private String mLoggedInPersonId;
    private MessageFileHandler fileHandler;
    private KeyFileHandler keyFileHandler;
    private AES aes;
    private boolean isInitiator;
    private List<Message> messageList;
    private ListenerRegistration mListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat2);

        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Display user image as profile pic
        int default_image = this.getResources().getIdentifier("@drawable/image_fail", null, this.getPackageName());
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(ChatActivity.this));
        ImageLoader image_loader = ImageLoader.getInstance();
        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisc(true).resetViewBeforeLoading(true)
                .showImageForEmptyUri(default_image)
                .showImageOnFail(default_image)
                .showImageOnLoading(default_image).build();

        //Display user name and photo
        mAnonymousOtherName = getIntent().getStringExtra("EXTRA_NAME");
        mAnonymousOtherPhotoUrl = getIntent().getStringExtra("EXTRA_PHOTO_URL");
        mAnonymousOtherPhotoColor = getIntent().getStringExtra("EXTRA_PHOTO_COLOR");
        int image_id = this.getResources().getIdentifier("@drawable/" + mAnonymousOtherPhotoUrl, null, this.getPackageName());
        mAnonymousOtherPhotoUrl = "drawable://" + image_id;
        CircleImageView image_holder = findViewById(R.id.user_photo);
        TextView name_holder = findViewById(R.id.user_title);

        name_holder.setText(mAnonymousOtherName);
        image_loader.displayImage(mAnonymousOtherPhotoUrl, image_holder, options); //displays the no_bg image
        image_holder.setCircleBackgroundColor(Color.parseColor(mAnonymousOtherPhotoColor));

        //make sure the profile picture clicks and zooms
        final View viewStart = image_holder;
        final String imageURL = mAnonymousOtherPhotoUrl;
        final String imageColor = mAnonymousOtherPhotoColor;
        image_holder.setOnClickListener(v -> {
            Intent intent = new Intent(ChatActivity.this, ZoomedPictureActivity.class);
            String transitionName = getString(R.string.transition_string);
            ActivityOptionsCompat options1 =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(ChatActivity.this,
                            viewStart,   // Starting view
                            transitionName    // The String
                    );
            intent.putExtra("EXTRA_IMAGE_URL", imageURL);
            intent.putExtra("EXTRA_IMAGE_COLOR", imageColor);

            //Start the Intent
            ActivityCompat.startActivity(ChatActivity.this, intent, options1.toBundle());
        });

        // HANDLE THE CHAT
        mLoggedInPersonId = Database.getInstance().getLoggedInUserID();
        messageList = new ArrayList<>();
        fileHandler = new MessageFileHandler(this);
        mMessageListAdapter = new MessageListAdapter(messageList);
        mOtherPersonId = getIntent().getStringExtra("EXTRA_PERSON_ID");
        isInitiator = getIntent().getBooleanExtra("EXTRA_INITIATOR", false);
        keyFileHandler = new KeyFileHandler(this, mOtherPersonId);
        SecretKey key = keyFileHandler.getKey();
        if (key != null) {
            Log.d("AES_ENCRYPT", "Reading key from file");
            Log.d("AES_ENCRYPT_RESULT", AES.keyToString(key));
            aes = new AES(key);
        } else {
            Log.d("WOO AH!", "YES YOAV WOO AH CAUSE WOO F-ING AH!");
            Log.d("WOO AH!", "key was null :(");
        }

        Log.d("POPO", "onCreate: " + mOtherPersonId);
        mMessageEdit = findViewById(R.id.message_text);
        mMessageRecycler = findViewById(R.id.message_list);
        mMessageRecycler.setHasFixedSize(true);
        mMessageRecycler.setLayoutManager(new LinearLayoutManager(this));
        mMessageRecycler.setAdapter(mMessageListAdapter);
        mFirestore = FirebaseFirestore.getInstance();
    }

    // This function sets a listener for receiving messages from the server
    private void setMessageListeners() {
        mListener = mFirestore.collection(MESSAGES).whereEqualTo("toPersonID", mLoggedInPersonId)
                .whereEqualTo("fromPersonID", mOtherPersonId)
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        Log.d("COOLTEST", "Error: " + e.getMessage());
                        return;
                    }

                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                        if (doc.getType() == DocumentChange.Type.ADDED) {
                            String message_text = doc.getDocument().getString("message");
                            Log.d("COOLTEST1", "Chat got:" + message_text);

                            if(message_text == null || message_text.equals("CHAT REQUEST$") || message_text.equals("CHAT ACCEPT$") || message_text.equals("CHAT REJECT$")) {
                                Log.d("NONONONO", "IM DELETING");
                                mFirestore.collection(MESSAGES).document(doc.getDocument().getId()).delete();
                                continue;
                            }

                            Log.d("COOLTEST", "Content: " + message_text);
                            Log.d("POPO", "onEvent: DOES THIS");
                            Message message = doc.getDocument().toObject(Message.class);

                            //we're actually decrypting this message => must be not null
                            if (aes == null)
                                throw new AssertionError("AES should not be null");
                            message.setMessage(aes.decrypt(message.getMessage()));
                            mFirestore.collection(MESSAGES).document(doc.getDocument().getId()).delete();
                            if (message.getShown())
                                messageList.add(message);
                            fileHandler.writeMessage(message);
                        }
                    }
                    updateMessageList();
                });
    }


    //This function updates the messages shown in the UI.
    public void updateMessageList() {
        messageList.clear();
        for (Message msg : fileHandler.getMessages()) {
            Log.d("COOLTEST", "readMessage: " + msg.getMessage() + "from: " + msg.getFromPersonID());
            if (msg.getFromPersonID().equals(mOtherPersonId) || msg.getToPersonID().equals(mOtherPersonId)) {
                if (msg.getShown())
                    messageList.add(msg);
            }
        }
        mMessageListAdapter.notifyDataSetChanged();
        mMessageRecycler.scrollToPosition(mMessageListAdapter.getItemCount() - 1);
    }

    @Override
    public void onPause() {
        super.onPause();
        new ContactListFileHandler(this).changeLastChatViewDate(mOtherPersonId, new Date());
        // on activity pause stop listening for new messages from this activity
        mListener.remove();
    }

    @Override
    public void onResume(){
        super.onResume();
        // on activity resume start listening for new messages from this activity
        setMessageListeners();
    }


    //This function is called when the send button is pressed
    public void onSendButtonClick(View v) {
        //only tackles shown message
        String message = mMessageEdit.getText().toString();
        if (aes == null && isInitiator) {
            Log.d("AES_ENCRYPT_INIT", "Starting Convo with " + mOtherPersonId);
            //Database.getInstance().sendControlMessage("Starting Convo", mLoggedInPersonId, mOtherPersonId);
            aes = new AES(128);
            keyFileHandler.writeKey(aes.key);
            Database.getInstance().sendControlMessage("AES" + AES.keyToString(aes.key), mLoggedInPersonId, mOtherPersonId);
            Log.d("AES_ENCRYPT_SEND", "Sending " + mOtherPersonId + " message:" + "AES" + AES.keyToString(aes.key));
        }
        Database.getInstance().sendMessage(aes.encrypt(message), mLoggedInPersonId, mOtherPersonId);
        Log.d("AES_SEND_MESSAGE", aes.encrypt(message));
        Message mymessage = new Message(message, mLoggedInPersonId, mOtherPersonId, true);
        fileHandler.writeMessage(mymessage);
        messageList.add(mymessage);
        mMessageListAdapter.notifyDataSetChanged();
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
