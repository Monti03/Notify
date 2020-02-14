const functions = require('firebase-functions')

const admin = require('firebase-admin')

//var serviceAccount = require("serviceAccountKey.json");

admin.initializeApp()

exports.addToken = functions.https.onCall((data, context) => {

  const new_id = makeid(7)

  var token = data.token

  /* if (!(typeof text === 'string') || text.length === 0) {
    throw new functions.https.HttpsError('invalid-argument', 'The function must be called with ' +
        'one arguments "text" containing the message text to add.');
  }  */

  const db = admin.database()

  db.ref('idToToken/'+new_id).set({
    'token' : token
  })
  /* db.ref('tokenToId/'+token).set({
    'id' : id
  }) */

  return new_id
})

exports.getId = functions.https.onCall((data, context) => {
  var token = data.token

  /* if (!(typeof text === 'string') || text.length === 0) {
    throw new functions.https.HttpsError('invalid-argument', 'The function must be called with ' +
        'one arguments "text" containing the message text to add.');
  }  */

  const db = admin.database()

  db.ref('tokenToId/' + token + '/id').once('value').then(function(snap) {
    return snap
  })   
})

function makeid(length) {
  var result           = ''
  var characters       = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789'
  var charactersLength = characters.length
  for ( var i = 0; i < length; i++ ) {
     result += characters.charAt(Math.floor(Math.random() * charactersLength))
  }
  return result
}


exports.sendNotification = functions.https.onRequest((request, response) => {
  var params = request.query

  var to = params.to
  var text = params.text 
  var title = params.title

  if (!to) {
    response.json({success: false, error: 'No "to" param specified.'})
    return
  }

  var db = admin.database()
  return db.ref('idToToken/' + to + '/token').once('value').then(function(snap) {
    to = snap.val()

    if (!to) {
      response.json({success: false, error: 'Invalid "to" param specified.'})
      return
    }


    var message = {
      notification: {
        title: title,
        text: text
      }
    }


    console.log("[Notify] Sending message to " + to);
    admin.messaging().sendToDevice(to, message)
      .then(() => {
        console.log("[Notify] Sent message to " + to);
        response.json({success: true})
      })
      .catch((error) => {
        console.warn("[Notify] Unable to send message to " + to, error)
        response.json({success: false, error: error})
      })
  })
})

