const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

exports.onMessageSent = functions.firestore.document('messages/{message_id}').onCreate((snap, context) => {
    console.log("START");
    const message = snap.data();
    console.log("Started - this is the message from: ", message.fromPersonID);
    console.log("to: ", message.toPersonID);
    const payload = {
        notification: {
            title:  "קיבלת הודעה חדשה",
            body:   "לחץ כאן לראותה",
            click_action:   "com.example.ofir.social_geha.DISPLAY_MESSAGE"
        },
        data: {
            fromPersonID: message.fromPersonID
        }
    }
    return sendNotification(message.toPersonID, payload);
});

function sendNotification(toPersonID, payload){
    console.log("Starting to send notification");
    const deviceTokenRef = admin.firestore().collection('deviceTokens').doc(toPersonID);
    return deviceTokenRef.get().then(result => {
        const token_id = result.data().deviceToken;
        console.log("Sending the notification: ", payload, "to ", token_id);
        return admin.messaging().sendToDevice(token_id, payload).then((response) => {
            console.log("Sent the notification :D");
        })
        .catch((error) => {
            console.log("Error ", error);
        });

    });
}