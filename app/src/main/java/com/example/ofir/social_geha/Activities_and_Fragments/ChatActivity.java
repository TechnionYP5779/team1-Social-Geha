package com.example.ofir.social_geha.Activities_and_Fragments;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.example.ofir.social_geha.Firebase.Database;
import com.example.ofir.social_geha.Firebase.Message;
import com.example.ofir.social_geha.R;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

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

    private List<Message> messageList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat2);
        messageList = new ArrayList<>();
        mMessageListAdapter = new MessageListAdapter(messageList);

        mOtherPersonId = getIntent().getStringExtra("EXTRA_PERSON_ID");

        mMessageEdit = findViewById(R.id.message_text);

        mMessageRecycler = findViewById(R.id.message_list);
        mMessageRecycler.setHasFixedSize(true);
        mMessageRecycler.setLayoutManager(new LinearLayoutManager(this));
        mMessageRecycler.setAdapter(mMessageListAdapter);

        mFirestore = FirebaseFirestore.getInstance();

        mFirestore.collection(MESSAGES).whereEqualTo("toPersonID",Database.getInstance().getLoggedInUserID())
                .whereEqualTo("fromPersonID",mOtherPersonId)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(e != null){
                    Log.d("COOLTEST","Error: " + e.getMessage());
                    return;
                }

                for(DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()){
                    if(doc.getType() == DocumentChange.Type.ADDED){
                        String message_text = doc.getDocument().getString("message");
                        Log.d("COOLTEST","Content: " + message_text);
                        Message message = doc.getDocument().toObject(Message.class);
                        messageList.add(message);
                        mMessageListAdapter.notifyDataSetChanged();
                    }
                }

            }
        });

        mFirestore.collection(MESSAGES).whereEqualTo("fromPersonD",Database.getInstance().getLoggedInUserID())
                .whereEqualTo("toPersonID",mOtherPersonId)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if(e != null){
                            Log.d("COOLTEST","Error: " + e.getMessage());
                            return;
                        }

                        for(DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()){
                            if(doc.getType() == DocumentChange.Type.ADDED){
                                //String message = doc.getDocument().getString("message");
                                //Log.d("COOLTEST","Content: " + message);
                                Message message = doc.getDocument().toObject(Message.class);
                                messageList.add(message);

                                mMessageListAdapter.notifyDataSetChanged();
                            }
                        }

                    }
                });
    }

    public void onSendButtonClick(View v) {
        String message = mMessageEdit.getText().toString();
        Database.getInstance().sendMessage(message,Database.getInstance().getLoggedInUserID(),mOtherPersonId);
        mMessageEdit.setText("");
    }
}
