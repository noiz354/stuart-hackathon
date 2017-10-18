'use strict';

const functions = require('firebase-functions');

// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//  response.send("Hello from Firebase!");
// });


// The Firebase Admin SDK to access the Firebase Realtime Database. 
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

// Take the text parameter passed to this HTTP endpoint and insert it into the
// Realtime Database under the path /messages/:pushId/original
exports.addMessage = functions.https.onRequest((req, res) => {
  // Grab the text parameter.
  const original = req.query.text;
  // Push the new message into the Realtime Database using the Firebase Admin SDK.
  admin.database().ref('/messages').push({original: original}).then(snapshot => {
    // Redirect with 303 SEE OTHER to the URL of the pushed object in the Firebase console.
    res.redirect(303, snapshot.ref);
  });
});

const gcs = require('@google-cloud/storage')()
const spawn = require('child-process-promise').spawn

exports.generateThumbnail = functions.storage.object()
  .onChange(event => {
    const object = event.data
    const filePath = object.name
    const fileName = filePath.split('/').pop()
    const fileBucket = object.bucket
    const bucket = gcs.bucket(fileBucket)
    const tempFilePath = `/tmp/${fileName}`

    if(fileName.startsWith('thumb_')){
      console.log('Already a Thumbnail.')
      return
    }

    if(!object.contentType.startsWith('image/')){
      console.log('This is not an image')
      return
    }

    if(!object.resourceState === 'not_exists'){
      console.log('This is a deletion event')
      return 
    }

    return bucket.file(filePath).download({
      destination: tempFilePath
    }).then(() => {
      console.log('Image downloaded locally to', tempFilePath)
      return spawn('convert', [tempFilePath, '-thumbnail', '200x200>', tempFilePath])
    }).then(() => {
      console.log('Thumbnail Created')
      const thumbFilePath = filePath.replace(/(\/)?([^\/]*)$/, `$1thumb_$2`)

      return bucket.upload(tempFilePath, {
        destination: thumbFilePath
      })
    })
  })


  exports.sendNotificationForAddNewReminder = 
      functions.database.ref('/user/{email}/reminder_from/{reminderId}').onCreate(event => {
    const emailByYourSelf = event.params.email
    const reminderId = event.params.reminderId
    const reminderSnapshot = event.data

    var deviceToken = null;

    console.log('added ', reminderSnapshot.val())

    event.data.ref.parent.parent.child('fcmToken').on('value', function(snapshot){
      console.log('device token ',snapshot.val())
      deviceToken = snapshot.val()
    }, function(errorObject){
      console.error("The read failed: " + errorObject.code)
    })

    reminderSnapshot.ref.child('title').on('value', function(snapshot){
      console.log(snapshot.val())
    }, function(errorObject){
      console.error("The read failed: " + errorObject.code)
    })

    const title_ = reminderSnapshot.ref.child('title').once('value')
    const fromUserId_ = reminderSnapshot.ref.child('fromUserId').once('value')
    const createdAt_ = reminderSnapshot.ref.child('createdAt').once('value')

    // Notification details.
    const payload = {
      notification: {
        title: `${title_} wkwkwk`,
        body: `${fromUserId_} is creating this for you. created at ${createdAt_}`
      }
    };

    return Promise.all([title_, fromUserId_, createdAt_]).then(results => {

        // Send notifications to all tokens.
      return admin.messaging().sendToDevice(deviceToken, payload).then(response => {
        // For each message check if there was an error.
        const tokensToRemove = [];
        response.results.forEach((result, index) => {
          const error = result.error;
          if (error) {
            console.error('Failure sending notification to', tokens[index], error);
            // Cleanup the tokens who are not registered anymore.
            if (error.code === 'messaging/invalid-registration-token' ||
                error.code === 'messaging/registration-token-not-registered') {
              tokensToRemove.push(tokensSnapshot.ref.child(tokens[index]).remove());
            }
          }
        });
        return Promise.all(tokensToRemove);
      }).catch( er => {
        console.error(er)
      })
    })
  })