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
import com.example.ofir.social_geha.Activities_and_Fragments.FileHandlers.SharingsFileHandler;
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
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AllChatsActivity extends AppCompatActivity {
    Toolbar mToolbar;
    ChatListAdapter mAdapter;
    private MessageFileHandler mFileHandler;
    private SharingsFileHandler sharingsFileHandler;
    ListView mListView;
    TextView mEmptyView;
    ArrayList<ChatEntry> conversationList;
    ArrayList<ChatEntry> allList;
    private FirebaseFirestore mFirestore;
    private static final String MESSAGES = "messages";
    private static final String USERS = "users";
    private Person p;

    private static final int LOGIN_RETURN_CODE = 1;
    private ListenerRegistration mListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_chats);

        if (!Database.getInstance().isLoggedIn()) {
            promptLogin();
        }

        mFileHandler = new MessageFileHandler(this);
        sharingsFileHandler = new SharingsFileHandler(this);
        mFirestore = FirebaseFirestore.getInstance();
        FloatingActionButton fab = findViewById(R.id.new_conversation_fb);
        fab.setOnClickListener(v -> startActivity(new Intent(AllChatsActivity.this, FilterMatchesActivity.class)));

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mListView = findViewById(R.id.list);
        mEmptyView = findViewById(R.id.emptyView);

        Database.getInstance().getdb().collection("users").whereEqualTo("userID", Database.getInstance().getLoggedInUserID()).get()
                .addOnCompleteListener(task -> {
                    //Log.d("SHAI", "task complete!");
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            AllChatsActivity.this.p = doc.toObject(Person.class);
                        }
                    }

                });

//        if(this.p == null) {
//            Log.d("SHAI", "logged in person IS null");
//        } else {
//            Log.d("SHAI", "logged in person ISN'T null");
//        }

        loadList();

        mListView.setOnItemClickListener((parent, view, position, id) -> {
            Object o = mListView.getItemAtPosition(position);
            ChatEntry chatEntry = (ChatEntry) o; //As you are using Default String Adapter
            Intent myIntent = new Intent(AllChatsActivity.this, ChatActivity.class);

            myIntent.putExtra("EXTRA_PERSON_ID", chatEntry.getUserID());
            myIntent.putExtra("EXTRA_NAME", chatEntry.getName());
            myIntent.putExtra("EXTRA_PHOTO_URL", chatEntry.getImageName());
            myIntent.putExtra("EXTRA_PHOTO_COLOR", chatEntry.getImageColor());
            myIntent.putExtra("EXTRA_INITIATOR", false);
            AllChatsActivity.this.startActivity(myIntent);
        });

        //Set long click listener
        Log.d("CLICK", "SETTING UP THE LONG CLICK LISTENER");
        mListView.setLongClickable(true);
        mListView.setOnItemLongClickListener((arg0, arg1, pos, id) -> {

            String uid = conversationList.get(pos).getUserID();
            Log.d("CLICK", "Clicked: " + uid);
            //cannot click once it's been shared
            for(String ID: sharingsFileHandler.getUIDs()){
                Log.d("CLICK", "person I've shared with: " + ID);
                if(uid.equals(ID)) {
                    String toastText = "לא ניתן לבטל את שיתוף המידע עם " + conversationList.get(pos).getName();
                    //Toast.makeText(AllChatsActivity.this, toastText, Toast.LENGTH_SHORT).show();
                    return true;
                }
            }

//            Map<String, ContactListFileHandler.Contact> contactMap = new ContactListFileHandler(AllChatsActivity.this).getContacts();
//            for (Map.Entry<String, ContactListFileHandler.Contact> c : contactMap.entrySet()) {
//                if (c.getValue().getUid().equals(uid) && !c.getValue().getRealName().equals(ContactListFileHandler.Contact.UNKNOWN_NAME)) {
//                    String toastText = "לא ניתן לבטל את שיתוף המידע עם " + c.getValue().getRealName();
//                    Toast.makeText(AllChatsActivity.this, toastText, Toast.LENGTH_SHORT).show();
//                    return true;
//                }
//            }

            AlertDialog.Builder builder = new AlertDialog.Builder(AllChatsActivity.this);
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.alert_dialog_information_exposre, null);
            builder.setView(dialogView);
            Button one = dialogView.findViewById(R.id.button1);
            Button three = dialogView.findViewById(R.id.button3);
            final AlertDialog dialog = builder.create();
            one.setOnClickListener(v -> {
                Toast.makeText(AllChatsActivity.this, "בוטל", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            });

            three.setOnClickListener(v -> {
                String content = "IDENTITY$" + Database.getInstance().getLoggedInUserID() + "#" + AllChatsActivity.this.p.getRealName();
                Log.d("SHAI", "going to send reveal message:" + content);
                Database.getInstance().sendControlMessage(content, Database.getInstance().getLoggedInUserID(), conversationList.get(pos).getUserID());
                Toast.makeText(AllChatsActivity.this, "שותף בהצלחה", Toast.LENGTH_SHORT).show();
                sharingsFileHandler.addUID(conversationList.get(pos).getUserID());
                dialog.dismiss();
            });

            String are_you_sure_msg = "האם אתה בטוח שברצונך לשתף את זהותך עם " + conversationList.get(pos).getName() + "?";
            TextView message = dialogView.findViewById(R.id.textView2);
            message.setText(are_you_sure_msg);
            dialog.show();
            return true;
        });

        mListView.setEmptyView(mEmptyView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu_search, menu);

        MenuItem mSearch = menu.findItem(R.id.action_search);

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

    private void setMessageListeners() {
        Log.d("SHAI", "IN POPULATE CONV LIST");

        mListener = mFirestore.collection(MESSAGES).whereEqualTo("toPersonID", Database.getInstance().getLoggedInUserID())
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

//                                if(message.getMessage() == null || message.getMessage().equals("CHAT REQUEST$")
//                                        || message.getMessage().equals("CHAT ACCEPT$") || message.getMessage().equals("CHAT REJECT$")) continue;
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
                                } else if (text.substring(0, "NAME_CHANGE".length()).equals("NAME_CHANGE")) {
                                    String realName = text.substring(text.indexOf('#') + 1);
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
        Map<String, ChatEntry> chatEntryMap = new HashMap<>();
        Map<String, ContactListFileHandler.Contact> contactMap = new ContactListFileHandler(this).getContacts();
        final String dummyName = "CONTACT DUMMY";

        for (Map.Entry<String, ContactListFileHandler.Contact> contact : contactMap.entrySet()) {
            chatEntryMap.put(contact.getKey(), new ChatEntry(contact.getValue(), null, 0));
        }

        for (Message msg : mFileHandler.getMessages()) {
            if (!msg.getShown()) continue; //ensures that the last SHOWN message is in the map
            ChatEntry chatEntry;

            if (loggedInUserID.equals(msg.getFromPersonID())) {
                if (!chatEntryMap.containsKey(msg.getToPersonID())) {
                    ContactListFileHandler.Contact dummy = new ContactListFileHandler.Contact(msg.getToPersonID(), dummyName, "", null, new Date(Long.MIN_VALUE));
                    chatEntryMap.put(msg.getToPersonID(), new ChatEntry(dummy, null, 0));
                }
                chatEntry = chatEntryMap.get(msg.getToPersonID());
            } else {
                if (!chatEntryMap.containsKey(msg.getFromPersonID())) {
                    ContactListFileHandler.Contact dummy = new ContactListFileHandler.Contact(msg.getFromPersonID(), dummyName, "", null, new Date(Long.MIN_VALUE));
                    chatEntryMap.put(msg.getFromPersonID(), new ChatEntry(dummy, null, 0));
                }
                chatEntry = chatEntryMap.get(msg.getFromPersonID());
            }

            chatEntry.setMessage(msg);
            if (!loggedInUserID.equals(msg.getFromPersonID()) && msg.getMessageDate().after(chatEntry.getLastChatReadDate())) {
                chatEntry.setUnreadCount(chatEntry.getUnreadCount() + 1);
            }
        }

        chatEntryMap.remove(loggedInUserID);

        for (final Map.Entry<String, ChatEntry> entry : chatEntryMap.entrySet()) {
            if (!entry.getValue().getName().equals(dummyName)) {
                Log.d("SHAI", "got shown message from known contact " + entry.getKey());
                ChatEntry chatEntry = entry.getValue();

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
                            ContactListFileHandler.Contact myContact = new ContactListFileHandler.Contact(myPerson.getUserID(), ContactListFileHandler.Contact.UNKNOWN_NAME,
                                    myPerson.getDescription(), myPerson.getAnonymousIdentity(), new Date(Long.MIN_VALUE));
                            ChatEntry chatEntry = new ChatEntry(myContact, entry.getValue().getMessage(), entry.getValue().getUnreadCount()); //contact & message might have been updated

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
    public void onPause() {
        super.onPause();
        mListener.remove();
    }

    @Override
    public void onResume() {
        super.onResume();
        setMessageListeners();
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