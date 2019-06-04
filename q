[1mdiff --git a/app/src/main/java/com/example/ofir/social_geha/Activities_and_Fragments/AllChatsActivity.java b/app/src/main/java/com/example/ofir/social_geha/Activities_and_Fragments/AllChatsActivity.java[m
[1mindex e7aca00..f9b0c0c 100644[m
[1m--- a/app/src/main/java/com/example/ofir/social_geha/Activities_and_Fragments/AllChatsActivity.java[m
[1m+++ b/app/src/main/java/com/example/ofir/social_geha/Activities_and_Fragments/AllChatsActivity.java[m
[36m@@ -34,6 +34,7 @@[m [mimport com.google.firebase.firestore.DocumentChange;[m
 import com.google.firebase.firestore.EventListener;[m
 import com.google.firebase.firestore.FirebaseFirestore;[m
 import com.google.firebase.firestore.FirebaseFirestoreException;[m
[32m+[m[32mimport com.google.firebase.firestore.ListenerRegistration;[m
 import com.google.firebase.firestore.QueryDocumentSnapshot;[m
 import com.google.firebase.firestore.QuerySnapshot;[m
 [m
[36m@@ -57,6 +58,7 @@[m [mpublic class AllChatsActivity extends AppCompatActivity {[m
     private Person p;[m
 [m
     private static final int LOGIN_RETURN_CODE = 1;[m
[32m+[m[32m    private ListenerRegistration mListener;[m
 [m
     @Override[m
     protected void onCreate(Bundle savedInstanceState) {[m
[36m@@ -226,7 +228,6 @@[m [mpublic class AllChatsActivity extends AppCompatActivity {[m
         //Create the list of objects[m
         conversationList = new ArrayList<>();[m
         allList = new ArrayList<>();[m
[31m-        populateConversationsList();[m
 [m
         //Attach to adapter[m
         mAdapter = new ChatListAdapter(this, R.layout.match_row_layout, conversationList);[m
[36m@@ -245,10 +246,10 @@[m [mpublic class AllChatsActivity extends AppCompatActivity {[m
         }[m
     }[m
 [m
[31m-    private void populateConversationsList() {[m
[32m+[m[32m    private void setMessageListeners() {[m
         Log.d("SHAI", "IN POPULATE CONV LIST");[m
 [m
[31m-        mFirestore.collection(MESSAGES).whereEqualTo("toPersonID", Database.getInstance().getLoggedInUserID())[m
[32m+[m[32m        mListener = mFirestore.collection(MESSAGES).whereEqualTo("toPersonID", Database.getInstance().getLoggedInUserID())[m
                 .addSnapshotListener(new EventListener<QuerySnapshot>() {[m
                     @Override[m
                     public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {[m
[36m@@ -427,8 +428,15 @@[m [mpublic class AllChatsActivity extends AppCompatActivity {[m
     }[m
 [m
     @Override[m
[31m-    protected void onResume() {[m
[32m+[m[32m    public void onPause() {[m
[32m+[m[32m        super.onPause();[m
[32m+[m[32m        mListener.remove();[m
[32m+[m[32m    }[m
[32m+[m
[32m+[m[32m    @Override[m
[32m+[m[32m    public void onResume(){[m
         super.onResume();[m
[32m+[m[32m        setMessageListeners();[m
         updateList();[m
     }[m
 [m
[1mdiff --git a/app/src/main/java/com/example/ofir/social_geha/Activities_and_Fragments/ChatActivity.java b/app/src/main/java/com/example/ofir/social_geha/Activities_and_Fragments/ChatActivity.java[m
[1mindex 437a593..3c7472b 100644[m
[1m--- a/app/src/main/java/com/example/ofir/social_geha/Activities_and_Fragments/ChatActivity.java[m
[1m+++ b/app/src/main/java/com/example/ofir/social_geha/Activities_and_Fragments/ChatActivity.java[m
[36m@@ -26,6 +26,7 @@[m [mimport com.google.firebase.firestore.DocumentChange;[m
 import com.google.firebase.firestore.EventListener;[m
 import com.google.firebase.firestore.FirebaseFirestore;[m
 import com.google.firebase.firestore.FirebaseFirestoreException;[m
[32m+[m[32mimport com.google.firebase.firestore.ListenerRegistration;[m
 import com.google.firebase.firestore.QuerySnapshot;[m
 import com.nostra13.universalimageloader.core.DisplayImageOptions;[m
 import com.nostra13.universalimageloader.core.ImageLoader;[m
[36m@@ -56,6 +57,7 @@[m [mpublic class ChatActivity extends AppCompatActivity {[m
     private AES aes;[m
     private boolean isInitiator;[m
     private List<Message> messageList;[m
[32m+[m[32m    private ListenerRegistration mListener;[m
 [m
     @Override[m
     protected void onCreate(Bundle savedInstanceState) {[m
[36m@@ -137,12 +139,11 @@[m [mpublic class ChatActivity extends AppCompatActivity {[m
         mMessageRecycler.setAdapter(mMessageListAdapter);[m
         mFirestore = FirebaseFirestore.getInstance();[m
 [m
[31m-        setMessageListeners();[m
     }[m
 [m
 [m
     private void setMessageListeners() {[m
[31m-        mFirestore.collection(MESSAGES).whereEqualTo("toPersonID", mLoggedInPersonId)[m
[32m+[m[32m        mListener = mFirestore.collection(MESSAGES).whereEqualTo("toPersonID", mLoggedInPersonId)[m
                 .whereEqualTo("fromPersonID", mOtherPersonId)[m
                 .addSnapshotListener(new EventListener<QuerySnapshot>() {[m
                     @Override[m
[36m@@ -155,6 +156,7 @@[m [mpublic class ChatActivity extends AppCompatActivity {[m
                         for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {[m
                             if (doc.getType() == DocumentChange.Type.ADDED) {[m
                                 String message_text = doc.getDocument().getString("message");[m
[32m+[m[32m                                Log.d("COOLTEST1", "Chat got:" + message_text);[m
 [m
                                 if(message_text == null || message_text.equals("CHAT REQUEST$") || message_text.equals("CHAT ACCEPT$") || message_text.equals("CHAT REJECT$")) {[m
                                     Log.d("NONONONO", "IM DELETING");[m
[36m@@ -169,7 +171,7 @@[m [mpublic class ChatActivity extends AppCompatActivity {[m
                                 //we're actually decrypting this message => must be not null[m
                                 if (aes == null)[m
                                     throw new AssertionError("AES should not be null");[m
[31m-                                message.setMessage(aes.decrypt(message.getMessage()));[m
[32m+[m[32m                                message.setMessage(message.getMessage());[m
                                 mFirestore.collection(MESSAGES).document(doc.getDocument().getId()).delete();[m
                                 if (message.getShown())[m
                                     messageList.add(message);[m
[36m@@ -198,6 +200,13 @@[m [mpublic class ChatActivity extends AppCompatActivity {[m
     public void onPause() {[m
         super.onPause();[m
         new ContactListFileHandler(this).changeLastChatViewDate(mOtherPersonId, new Date());[m
[32m+[m[32m        mListener.remove();[m
[32m+[m[32m    }[m
[32m+[m
[32m+[m[32m    @Override[m
[32m+[m[32m    public void onResume(){[m
[32m+[m[32m        super.onResume();[m
[32m+[m[32m        setMessageListeners();[m
     }[m
 [m
     public void onSendButtonClick(View v) {[m
