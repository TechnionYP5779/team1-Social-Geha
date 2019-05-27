package com.example.ofir.social_geha.Activities_and_Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ofir.social_geha.Activities_and_Fragments.FileHandlers.ContactListFileHandler;
import com.example.ofir.social_geha.Activities_and_Fragments.FileHandlers.KeyFileHandler;
import com.example.ofir.social_geha.Activities_and_Fragments.FileHandlers.MessageFileHandler;
import com.example.ofir.social_geha.Encryption.AES;
import com.example.ofir.social_geha.Firebase.Database;
import com.example.ofir.social_geha.Firebase.Message;
import com.example.ofir.social_geha.Person;
import com.example.ofir.social_geha.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AllChatsActivity extends AppCompatActivity {
    Toolbar mToolbar;
    ChatListAdapter mAdapter;
    private MessageFileHandler mFileHandler;
    ListView mListView;
    TextView mEmptyView;
    Map<String, Message> messageMap;
    ArrayList<ChatEntry> conversationList;
    ArrayList<ChatEntry> allList;
    private FirebaseFirestore mFirestore;
    private static final String MESSAGES = "messages";
    private static final String USERS = "users";
    private Person p;

    private static final int LOGIN_RETURN_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_chats);

        if (!Database.getInstance().isLoggedIn()) {
            promptLogin();
        }

        mFileHandler = new MessageFileHandler(this);
        mFirestore = FirebaseFirestore.getInstance();
        FloatingActionButton fab = findViewById(R.id.new_conversation_fb);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AllChatsActivity.this, FilterMatchesActivity.class));
            }
        });

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mListView = findViewById(R.id.list);
        mEmptyView = findViewById(R.id.emptyView);

        Database.getInstance().getdb().collection("users").whereEqualTo("userID", Database.getInstance().getLoggedInUserID()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        //Log.d("SHAI", "task complete!");
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                AllChatsActivity.this.p = doc.toObject(Person.class);
                            }
                        }

                    }
                });

//        if(this.p == null) {
//            Log.d("SHAI", "logged in person IS null");
//        } else {
//            Log.d("SHAI", "logged in person ISN'T null");
//        }

        loadList();

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object o = mListView.getItemAtPosition(position);
                ChatEntry chatEntry = (ChatEntry) o; //As you are using Default String Adapter
                Intent myIntent = new Intent(AllChatsActivity.this, ChatActivity.class);

                myIntent.putExtra("EXTRA_PERSON_ID", chatEntry.getUserID());
                myIntent.putExtra("EXTRA_NAME", chatEntry.getName());
                myIntent.putExtra("EXTRA_PHOTO_URL", chatEntry.getImageName());
                myIntent.putExtra("EXTRA_PHOTO_COLOR", chatEntry.getImageColor());
                myIntent.putExtra("EXTRA_INITIATOR", false);
                AllChatsActivity.this.startActivity(myIntent);
            }
        });

        //Set long click listener
        mListView.setLongClickable(true);
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           final int pos, long id) {

                String uid = conversationList.get(pos).getUserID();
                //cannot click once it's been shared
                for (ContactListFileHandler.Contact c : new ContactListFileHandler(AllChatsActivity.this).getContacts()) {
                    if (c.getUid().equals(uid) && !c.getRealName().equals(ContactListFileHandler.Contact.UNKNOWN_NAME)) {
                        String toastText = "לא ניתן לבטל את שיתוף המידע עם " + c.getRealName();
                        Toast.makeText(AllChatsActivity.this, toastText, Toast.LENGTH_SHORT).show();
                        return true;
                    }
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(AllChatsActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.alert_dialog_information_exposre, null);
                builder.setView(dialogView);
                Button one = dialogView.findViewById(R.id.button1);
                Button three = dialogView.findViewById(R.id.button3);
                final AlertDialog dialog = builder.create();
                one.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(AllChatsActivity.this, "בוטל", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });

                three.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String content = "IDENTITY$" + Database.getInstance().getLoggedInUserID() + "#" + AllChatsActivity.this.p.getRealName();
                        Log.d("SHAI", "going to send reveal message:" + content);
                        Database.getInstance().sendControlMessage(content, Database.getInstance().getLoggedInUserID(), conversationList.get(pos).getUserID());
                        Toast.makeText(AllChatsActivity.this, "שותף בהצלחה", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });

                String are_you_sure_msg = "האם אתה בטוח שברצונך לשתף את זהותך עם " + conversationList.get(pos).getName() + "?";
                TextView message = dialogView.findViewById(R.id.textView2);
                message.setText(are_you_sure_msg);
                dialog.show();
                return true;
            }
        });

        mListView.setEmptyView(mEmptyView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu_search, menu);

        MenuItem mSearch = menu.findItem(R.id.action_search);
        MenuItem mProfile = menu.findItem(R.id.personal_info);
        mProfile.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // move to personal settings screen
                Intent intent = new Intent(AllChatsActivity.this, mainScreen.class);
                AllChatsActivity.this.startActivity(intent);
                return true;
            }
        });

        SearchView mSearchView = (SearchView) mSearch.getActionView();
        mSearchView.setQueryHint("חיפוש");

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

    public void promptLogin() {
        Intent intent = new Intent(AllChatsActivity.this, Login.class);
        startActivityForResult(intent, LOGIN_RETURN_CODE);
    }

    private void loadList() {
        //Create the list of objects
        conversationList = new ArrayList<>();
        allList = new ArrayList<>();
        messageMap = new HashMap<>();
        populateConversationsList();

        //Attach to adapter
        mAdapter = new ChatListAdapter(this, R.layout.match_row_layout, conversationList);
        mListView.setAdapter(mAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOGIN_RETURN_CODE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, getString(R.string.strings_succ_login), Toast.LENGTH_SHORT).show();
            } else {
                promptLogin();
            }
        }
    }

    private void populateConversationsList() {
        Log.d("SHAI", "IN POPULATE CONV LIST");

        mFirestore.collection(MESSAGES).whereEqualTo("toPersonID", Database.getInstance().getLoggedInUserID())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            return;
                        }

                        for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                            if (doc.getType() == DocumentChange.Type.ADDED) {
                                Message message = doc.getDocument().toObject(Message.class); //added message
                                mFirestore.collection(MESSAGES).document(doc.getDocument().getId()).delete();
                                // decrypt regular messages
                                String contactUID = message.getFromPersonID();
                                if (message.getShown()) {
                                    KeyFileHandler keyFileHandler = new KeyFileHandler(AllChatsActivity.this, contactUID);
                                    AES aes = new AES(keyFileHandler.getKey());
                                    message.setMessage(aes.decrypt(message.getMessage()));
                                }
                                mFileHandler.writeMessage(message); //non shown messages will be ignored anyway but may as well write them

                                if (message.getShown()) {
                                    continue;
                                }

                                //it's a control message - our business
                                String text = message.getMessage();

                                /* self message*/
                                if (contactUID.equals(Database.getInstance().getLoggedInUserID()))
                                    continue;

                                /*a control message but not an identity reveal*/
                                if (text.substring(0, "IDENTITY".length()).equals("IDENTITY")) {
                                    String realName = text.substring(text.indexOf('#') + 1);
                                    Log.d("SHAI", "got control message from " + message.getFromPersonID());
                                    Log.d("SHAI", "his/her realname is " + realName);
                                    new ContactListFileHandler(AllChatsActivity.this).changeName(contactUID, realName);

                                    //for the full effect - swap the names in the lists
                                    //The contact must be in the list since a first message can not be an identity reveal one
                                    for (int i = 0; i < conversationList.size(); i++) {
                                        if (conversationList.get(i).getUserID().equals(contactUID)) {
                                            conversationList.get(i).setRealName(realName);
                                        }
                                    }

                                    for (int i = 0; i < allList.size(); i++) {
                                        if (allList.get(i).getUserID().equals(contactUID)) {
                                            allList.get(i).setRealName(realName);
                                        }
                                    }
                                    // a message we should ignore because it's not an identity reveal - to the next change
                                } else if (text.substring(0, "AES".length()).equals("AES")) {
                                    KeyFileHandler keyFileHandler = new KeyFileHandler(AllChatsActivity.this, contactUID);
                                    keyFileHandler.writeKey(AES.stringToKey(text.substring("AES".length())));
                                    Log.d("AES_READ", "received key from " + contactUID + " and the key is " + text.substring("AES".length()));
                                }

                            }

                        }
                        updateList();
                    }
                });
    }

    public void updateList() {
        Log.d("SHAI", "IN UPDATE LIST");
        String loggedInUserID = Database.getInstance().getLoggedInUserID();

        for (Message msg : mFileHandler.getMessages()) {
            if (!msg.getShown()) continue; //ensures that the last SHOWN message is in the map

            Log.d("COOLTEST", "found msg:" + msg.getFromPersonID());
            if (loggedInUserID.equals(msg.getFromPersonID()))
                messageMap.put(msg.getToPersonID(), msg);
            else
                messageMap.put(msg.getFromPersonID(), msg);
        }

        //messageMap.remove(loggedInUserID);

        ArrayList<ContactListFileHandler.Contact> contacts = new ContactListFileHandler(this).getContacts();
        for (final Map.Entry<String, Message> entry : messageMap.entrySet()) {
//            if (!entry.getValue().getShown()) { //it's a control message
//                String contactUID = entry.getValue().getFromPersonID();
//
//                if(contactUID.equals(Database.getInstance().getLoggedInUserID()) ||
//                        !entry.getValue().getMessage().substring(0, "IDENTITY".length()).equals("IDENTITY")) {
//                    continue; // a message we should ignore because it's not an identity reveal
//                }
//
//                String text = entry.getValue().getMessage();
//                String realName = text.substring(text.indexOf('#') + 1);
//                Log.d("SHAI", "got control message from " + entry.getKey());
//                Log.d("SHAI", "his/her realname is " + realName);
//
//                new ContactListFileHandler(this).changeName(contactUID, realName);
//
//                //for the full effect
//                boolean found = false;
//                for(int i = 0; i < conversationList.size(); i++) {
//                    if(conversationList.get(i).getUserID().equals(contactUID)) {
//                        conversationList.get(i).setRealName(realName);
//                        found = true;
//                    }
//                }
//
//                for(int i = 0; i < allList.size(); i++) {
//                    if(allList.get(i).getUserID().equals(contactUID)) {
//                        allList.get(i).setRealName(realName);
//                    }
//                }
//                continue;
//            }
            int i = knowContact(contacts, entry.getKey());
            if (i != -1) {
                Log.d("SHAI", "got shown message from known contact " + entry.getKey());
                ChatEntry chatEntry = new ChatEntry(contacts.get(i), entry.getValue());

                int j = listContains(conversationList, chatEntry.getUserID());
                if (j != -1) {
                    conversationList.remove(chatEntry);
                    allList.remove(chatEntry);

                    conversationList.set(j, chatEntry);
                    allList.set(j, chatEntry);
                } else {
                    conversationList.add(chatEntry);
                    allList.add(chatEntry);
                }
                mAdapter.notifyDataSetChanged();
            } else {
                Log.d("SHAI", "got shown message from unknown contact " + entry.getKey());
                //new message from someone we do not know
                Task<QuerySnapshot> personQueryTask = mFirestore.collection(USERS).whereEqualTo("userID", entry.getKey()).get();
                personQueryTask.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        QuerySnapshot person = task.getResult();
                        Log.d("COOLTEST", "get user Complete");

                        if (!person.isEmpty()) {
                            Person myPerson = person.toObjects(Person.class).get(0);
                            Log.d("COOLTEST", "got user: " + myPerson.getDescription());

                            //general unknown person from person - in case of new message
                            ContactListFileHandler.Contact myContact = new ContactListFileHandler.Contact(myPerson.getUserID(), ContactListFileHandler.Contact.UNKNOWN_NAME,
                                    myPerson.getDescription(), myPerson.getAnonymousIdentity());
                            ChatEntry chatEntry = new ChatEntry(myContact, entry.getValue()); //contact & message might have been updated

                            int j = listContains(conversationList, chatEntry.getUserID());
                            if (j != -1) {
                                conversationList.remove(chatEntry);
                                allList.remove(chatEntry);

                                conversationList.set(j, chatEntry);
                                allList.set(j, chatEntry);
                            } else {
                                conversationList.add(chatEntry);
                                allList.add(chatEntry);
                            }
                            mAdapter.notifyDataSetChanged();

                            //since this person is not in the contactList we to update the file
                            Log.d("SHAI", "NEW CONTACT");
                            new ContactListFileHandler(AllChatsActivity.this).addContact(myContact);
                        }
                    }
                });
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateList();
    }

    int listContains(List<ChatEntry> list, String uid) {
        for (int i = 0; i < list.size(); i++) {
            ChatEntry ce = list.get(i);
            if (ce.getUserID().equals(uid)) return i;
        }
        return -1;
    }

    int knowContact(ArrayList<ContactListFileHandler.Contact> contacts, String uid) {
        for (int i = 0; i < contacts.size(); i++) {
            ContactListFileHandler.Contact ce = contacts.get(i);
            if (ce.getUid().equals(uid)) return i;
        }
        return -1;
    }
}