const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

exports.onMessageSent = functions.firestore.document('messages/{message_id}').onCreate((snap, context) => {
    console.log("START");
    const messageData = snap.data();
    console.log("Started - this is the message from: ", messageData.fromPersonID);
    console.log("to: ", messageData.toPersonID);
    const payload_reg = {
        data: {
            fromPersonID: messageData.fromPersonID,
            title:  "קיבלת הודעה חדשה",
            body:   "לחץ כאן לראותה",
            click_action:   "com.example.ofir.social_geha.DISPLAY_MESSAGE"
        }
    }
    const payload_request = {
        data: {
            fromPersonID: messageData.fromPersonID,
            title:  "משתמש מחפש את עזרתך",
            body:   "האם אתה מעוניין לעזור?",
            click_action:   "com.example.ofir.social_geha.REQUEST_HELP"
        }
    }
    console.log("The message is: ", messageData.message)
   
    if(messageData.shown) {
        return sendNotification(messageData.toPersonID, payload_reg);
    } else if (messageData.message == "CHAT REQUEST$" || messageData.message == "8BC9ED90825A196EB43D647BB9B321B9"){
        console.log("BRANCH");
        return sendNotification(messageData.toPersonID, payload_request);
    }
    return;
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