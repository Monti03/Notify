package com.francesco.montano.notify

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat


class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d("MyFirebaseMessagingService", "From: " + remoteMessage.from!!)



        //Log.e("MyFirebaseMessagingService", "Message Notification Body: " + remoteMessage.notification!!.body!!)

        // Check if message contains a notification payload.
        if (remoteMessage.notification != null) {
            Log.d("MyFirebaseMessagingService", "Message Notification Body: " + remoteMessage.notification!!.body!!)
            Log.d("MyFirebaseMessagingService", "Message Notification Body: " + remoteMessage.notification!!.title!!)

            var builder = NotificationCompat.Builder(this, "Notify")
                    .setSmallIcon(R.drawable.ic_stat_ic_notification)
                    .setContentTitle(remoteMessage.notification!!.title!!)
                    .setContentText(remoteMessage.notification!!.body!!)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            with(NotificationManagerCompat.from(this)) {
                // notificationId is a unique int for each notification that you must define
                notify(1, builder.build())
            }
        }



    }

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    override fun onNewToken(token: String) {
        Log.d("MyFirebaseMessagingService", "Refreshed token: $token")

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        // sendRegistrationToServer(token)
    }
}