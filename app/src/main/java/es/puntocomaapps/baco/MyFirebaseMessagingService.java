package es.puntocomaapps.baco;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Objects;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private String TAG = "FIREBASE";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        Log.e(TAG, Objects.requireNonNull(Objects.requireNonNull(remoteMessage.getNotification()).getBody()));
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Log.e(TAG, s);
    }
}
