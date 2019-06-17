package com.example.ofir.social_geha.Activities_and_Fragments;

import android.content.Intent;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.github.angads25.toggle.widget.DayNightSwitch;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AllChatsFragment extends Fragment {
    private MessageFileHandler mFileHandler;
    private SharingsFileHandler sharingsFileHandler;
    private ContactListFileHandler mContactsHandler;

    ListView mListView;
    TextView mEmptyView;
    private FirebaseFirestore mFirestore;
    private static final String MESSAGES = "messages";
    private static final String USERS = "users";
    private Person p;
    private ListenerRegistration mListener;

    public AllChatsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_all_chats, container, false);
        ((activity_main_drawer) getActivity()).setActionBarTitle("שיחות");
        mFileHandler = new MessageFileHandler(getActivity());
        mContactsHandler = new ContactListFileHandler(getActivity());
        sharingsFileHandler = new SharingsFileHandler(getActivity());
        mFirestore = FirebaseFirestore.getInstance();
        mListView = v.findViewById(R.id.list);
        mEmptyView = v.findViewById(R.id.emptyView);

        //find the current person in the DB
        Database.getInstance().getdb().collection("users").whereEqualTo("userID", Database.getInstance().getLoggedInUserID()).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            p = doc.toObject(Person.class);
                            FloatingActionButton fab = v.findViewById(R.id.new_conversation_fab);
                            // For current patient or family member, we want to enable new conversation
                            // and disable toggle
                            if(p.getKind() != Person.Kind.PATIENT && p.getKind() != Person.Kind.FAMILY_MEMBER){
                                setupToggle(v, true);
                                fab.hide();
                            }
                            else{ // vice-versa
                                fab.show();
                                fab.setOnClickListener(v1 -> startActivity(new Intent(getActivity(), FilterMatchesActivity.class)));
                                setupToggle(v, false);
                            }

                        }
                    }

                });

        loadList();
        //makes sure that when a chat item is pressed, we're transferred to the appropriate chat with this person
        mListView.setOnItemClickListener((parent, view, position, id) -> {
            Object o = mListView.getItemAtPosition(position);
            ChatEntry chatEntry = (ChatEntry) o; //As you are using Default String Adapter
            Intent myIntent = new Intent(getActivity(), ChatActivity.class);

            myIntent.putExtra("EXTRA_PERSON_ID", chatEntry.getUserID());
            myIntent.putExtra("EXTRA_NAME", chatEntry.getName());
            myIntent.putExtra("EXTRA_PHOTO_URL", chatEntry.getImageName());
            myIntent.putExtra("EXTRA_PHOTO_COLOR", chatEntry.getImageColor());
            myIntent.putExtra("EXTRA_INITIATOR", false);
            getActivity().startActivity(myIntent);
        });


        //Set long click listener on each chat item
        //a long click pops up the sharing menu
        mListView.setLongClickable(true);
        mListView.setOnItemLongClickListener((arg0, arg1, pos, id) -> {

            //the id of the person we've clicked
            String uid = ((activity_main_drawer) getActivity()).conversationList.get(pos).getUserID();
            //this loop makes sure that we cannot long-click a chat the information has been shared with that person
            //that's because we've decided that once information has been shared, there is no way to take it back
            for (String ID : sharingsFileHandler.getUIDs()) {
                if (uid.equals(ID)) {
                    String toastText = "לא ניתן לבטל את שיתוף המידע עם " + ((activity_main_drawer) getActivity()).conversationList.get(pos).getName();
                    Toast.makeText(getActivity(), toastText, Toast.LENGTH_SHORT).show();
                    return true;
                }
            }

            //sets up the dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater1 = getLayoutInflater();
            View dialogView = inflater1.inflate(R.layout.alert_dialog_information_exposre, null);
            builder.setView(dialogView);
            Button one = dialogView.findViewById(R.id.button1);
            Button three = dialogView.findViewById(R.id.button3);
            final AlertDialog dialog = builder.create();

            //the red button merely dismissed the dialog without doing anything
            one.setOnClickListener(v12 -> {
                Toast.makeText(getActivity(), "בוטל", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            });

            //the "ok" button, shares the information by sending a control message to that person with our real name (which his listener will then interpret & record)
            three.setOnClickListener(v12 -> {
                String content = "IDENTITY$" + Database.getInstance().getLoggedInUserID() + "#" + p.getRealName();
                sharingsFileHandler.addUID(((activity_main_drawer) getActivity()).conversationList.get(pos).getUserID());
                Database.getInstance().sendControlMessage(content, Database.getInstance().getLoggedInUserID(), ((activity_main_drawer) getActivity()).conversationList.get(pos).getUserID());
                Toast.makeText(getActivity(), "שותף בהצלחה", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            });

            //for an added layer of protection against accidental clicks, we make sure the user knows the implications of pressing "ok"
            String are_you_sure_msg = "האם אתה בטוח שברצונך לשתף את זהותך עם " + ((activity_main_drawer) getActivity()).conversationList.get(pos).getName() + "?";
            TextView message = dialogView.findViewById(R.id.textView2);
            message.setText(are_you_sure_msg);
            dialog.show();
            return true;
        });

        mListView.setEmptyView(mEmptyView);

        // Inflate the layout for this fragment
        return v;
    }


    /**
     * Provides the necessary initialization for the two conversation lists
     */
    private void loadList() {
        //Create the list of objects
        ((activity_main_drawer) getActivity()).conversationList = new ArrayList<>();
        ((activity_main_drawer) getActivity()).allList = new ArrayList<>();

        //Attach to adapter
        ((activity_main_drawer) getActivity()).mAdapter = new ChatListAdapter(getActivity(), R.layout.match_row_layout, ((activity_main_drawer) getActivity()).conversationList);
        mListView.setAdapter(((activity_main_drawer) getActivity()).mAdapter);
    }


    /**
     * Adds a listener which deals with new incoming messages. Then, populates the list to display to the user
     */
    private void setMessageListeners() {
        mListener = mFirestore.collection(MESSAGES).whereEqualTo("toPersonID", Database.getInstance().getLoggedInUserID())
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        return;
                    }

                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                        if (doc.getType() == DocumentChange.Type.ADDED) {
                            Message message = doc.getDocument().toObject(Message.class); //this is the new message that was added
                            mFirestore.collection(MESSAGES).document(doc.getDocument().getId()).delete();
                            // decrypt regular messages
                            String contactUID = message.getFromPersonID();
                            if (message.getShown()) {
                                //A shown message is decrypted
                                KeyFileHandler keyFileHandler = new KeyFileHandler(getActivity(), contactUID);
                                AES aes = new AES(keyFileHandler.getKey());
                                message.setMessage(aes.decrypt(message.getMessage()));
                            }

                            //every message is written to the file
                            mFileHandler.writeMessage(message); //non shown messages will be ignored anyway but may as well write them

                            if (message.getShown()) {
                                continue;
                            }

                            //it's a control message
                            String text = message.getMessage();

                            /* self message - we skip*/
                            if (contactUID.equals(Database.getInstance().getLoggedInUserID()))
                                continue;

                            /*a control message which indicates someone has revealed their information to us*/
                            if (text.substring(0, "IDENTITY".length()).equals("IDENTITY")) {
                                //identify the sensitive info & write it in the Contact
                                String realName = text.substring(text.indexOf('#') + 1);
                                mContactsHandler.changeName(contactUID, realName);

                                //for the full effect - swap the names in the lists to display the correct name
                                //The contact must be in the list since a first message can not be an identity reveal one
                                for (int i = 0; i < ((activity_main_drawer) getActivity()).conversationList.size(); i++) {
                                    if (((activity_main_drawer) getActivity()).conversationList.get(i).getUserID().equals(contactUID)) {
                                        ((activity_main_drawer) getActivity()).conversationList.get(i).setRealName(realName);
                                    }
                                }

                                //switch in both lists, the full one & the displayed one (for searching)
                                for (int i = 0; i < ((activity_main_drawer) getActivity()).allList.size(); i++) {
                                    if (((activity_main_drawer) getActivity()).allList.get(i).getUserID().equals(contactUID)) {
                                        ((activity_main_drawer) getActivity()).allList.get(i).setRealName(realName);
                                    }
                                }

                                //a key exchange message, we record the AES key in the appropriate file
                            } else if (text.substring(0, "AES".length()).equals("AES")) {
                                KeyFileHandler keyFileHandler = new KeyFileHandler(getActivity(), contactUID);
                                keyFileHandler.writeKey(AES.stringToKey(text.substring("AES".length())));
                                // a name change message, indicating someone who has revealed their name to us has now changed it - we record the new name in the contact
                            } else if (text.substring(0, "NAME_CHANGE".length()).equals("NAME_CHANGE")) {
                                String realName = text.substring(text.indexOf('#') + 1);
                                mContactsHandler.changeName(contactUID, realName);

                                //update the new name in the two lists for an immediate effect
                                for (int i = 0; i < ((activity_main_drawer) getActivity()).conversationList.size(); i++) {
                                    if (((activity_main_drawer) getActivity()).conversationList.get(i).getUserID().equals(contactUID)) {
                                        ((activity_main_drawer) getActivity()).conversationList.get(i).setRealName(realName);
                                    }
                                }

                                for (int i = 0; i < ((activity_main_drawer) getActivity()).allList.size(); i++) {
                                    if (((activity_main_drawer) getActivity()).allList.get(i).getUserID().equals(contactUID)) {
                                        ((activity_main_drawer) getActivity()).allList.get(i).setRealName(realName);
                                    }
                                }
                            }

                        }

                    }

                    //populates the list on a first call
                    updateList();
                });
    }

    /**
     * This function makes sure the list is updated with new conversation that might have arrived
     */
    public void updateList() {
        String loggedInUserID = Database.getInstance().getLoggedInUserID();
        Map<String, ChatEntry> chatEntryMap = new HashMap<>();
        Map<String, ContactListFileHandler.Contact> contactMap = new ContactListFileHandler(getActivity()).getContacts();
        final String dummyName = "CONTACT DUMMY";

        //initializes an empty mapping between id's and contacts in the contact-list (in order to save get each contact's last message to display)
        for (Map.Entry<String, ContactListFileHandler.Contact> contact : contactMap.entrySet()) {
            chatEntryMap.put(contact.getKey(), new ChatEntry(contact.getValue(), null, 0));
        }

        //fills in the mapping from an id to the last shown message sent by that person
        //any non-contact encountered is added to the contact-list file & the mapping
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

        //remove the logged in user from the list
        chatEntryMap.remove(loggedInUserID);

        //traverses the mapping we've created, if the screen already displays a chat from that contact it replaces the message to the new one
        //if it's a new person, it adds a box with this new chat to the list on the screen - hence, updating it.
        for (final Map.Entry<String, ChatEntry> entry : chatEntryMap.entrySet()) {

            if (!entry.getValue().getName().equals(dummyName)) {
                ChatEntry chatEntry = entry.getValue();

                int j = listContains(((activity_main_drawer) getActivity()).conversationList, chatEntry.getUserID());
                if (j != -1) {
                    ((activity_main_drawer) getActivity()).conversationList.remove(chatEntry);
                    ((activity_main_drawer) getActivity()).allList.remove(chatEntry);

                    ((activity_main_drawer) getActivity()).conversationList.set(j, chatEntry);
                    ((activity_main_drawer) getActivity()).allList.set(j, chatEntry);
                } else {
                    ((activity_main_drawer) getActivity()).conversationList.add(chatEntry);
                    ((activity_main_drawer) getActivity()).allList.add(chatEntry);
                }
                ((activity_main_drawer) getActivity()).mAdapter.notifyDataSetChanged();
            } else {
                //new message from someone we do not know
                Task<QuerySnapshot> personQueryTask = mFirestore.collection(USERS).whereEqualTo("userID", entry.getKey()).get();
                personQueryTask.addOnCompleteListener(task -> {
                    QuerySnapshot person = task.getResult();
                    if (person != null && !person.isEmpty()) {
                        Person myPerson = person.toObjects(Person.class).get(0);
                        ContactListFileHandler.Contact myContact = new ContactListFileHandler.Contact(myPerson.getUserID(), ContactListFileHandler.Contact.UNKNOWN_NAME,
                                myPerson.getDescription(), myPerson.getAnonymousIdentity(), new Date(Long.MIN_VALUE));
                        ChatEntry chatEntry = new ChatEntry(myContact, entry.getValue().getMessage(), entry.getValue().getUnreadCount()); //contact & message might have been updated

                        int j = listContains(((activity_main_drawer) getActivity()).conversationList, chatEntry.getUserID());
                        if (j != -1) {
                            ((activity_main_drawer) getActivity()).conversationList.remove(chatEntry);
                            ((activity_main_drawer) getActivity()).allList.remove(chatEntry);

                            ((activity_main_drawer) getActivity()).conversationList.set(j, chatEntry);
                            ((activity_main_drawer) getActivity()).allList.set(j, chatEntry);
                        } else {
                            ((activity_main_drawer) getActivity()).conversationList.add(chatEntry);
                            ((activity_main_drawer) getActivity()).allList.add(chatEntry);
                        }
                        ((activity_main_drawer) getActivity()).mAdapter.notifyDataSetChanged();

                        //since this person is not in the contactList we to update the file
                        mContactsHandler.addContact(myContact);
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

    /**
     * @param list - a list of chat entries
     * @param uid - an id of a user
     * @return - the index of a an entry whose id matches @param uid if one exists (or -1 otherwise)
     */
    int listContains(List<ChatEntry> list, String uid) {
        for (int i = 0; i < list.size(); i++) {
            ChatEntry ce = list.get(i);
            if (ce.getUserID().equals(uid)) return i;
        }
        return -1;
    }

    /**
     * Setup the availability toggle
     */
    private void setupToggle(View v, Boolean show) {
        DayNightSwitch dayNightSwitch = v.findViewById(R.id.toggle_availability);
        TextView textView = v.findViewById(R.id.description_availability);
        ConstraintLayout constraintLayout = v.findViewById(R.id.constraint_layout_availability);

        if(!show){
            constraintLayout.setVisibility(View.GONE);
            return;
        }
        else{
            constraintLayout.setVisibility(View.VISIBLE);
        }

        dayNightSwitch.setOnToggledListener((toggleableView, isOn) -> {
            TransitionDrawable transition = (TransitionDrawable) constraintLayout.getBackground();
            if (isOn) {
                Database.getInstance().updateAvailability(true);
                textView.setText(R.string.available);
                transition.reverseTransition(300);
            } else {
                Database.getInstance().updateAvailability(false);
                textView.setText(R.string.not_available);
                transition.startTransition(300);
            }
        });

    }

}