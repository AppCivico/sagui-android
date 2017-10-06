package com.eokoe.sagui.services

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage



/**
 * @author Pedro Silva
 * @since 06/10/17
 */
class FCMService: FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // TODO show and persist notification
    }
}