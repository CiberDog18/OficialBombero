package com.example.oficialbombero.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.oficialbombero.R;
import com.example.oficialbombero.activities.NotificationBookingActivity;
import com.example.oficialbombero.channel.NotificationHelper;
import com.example.oficialbombero.models.Message;
import com.example.oficialbombero.receivers.AcceptReceiver;
import com.example.oficialbombero.receivers.CancelReceiver;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.Map;

public class MyFirebaseMessagingClient extends FirebaseMessagingService {

    private static final int NOTIFICATION_CODE = 100;

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        RemoteMessage.Notification notification = remoteMessage.getNotification();
        Map<String, String> data = remoteMessage.getData();
        String title = data.get("title");
        String body = data.get("body");
     //   String idNotification = data.get("idNotification");

        if (title != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (title.contains("SOLICITUD DE SERVICIO")) {
                    String idClient = data.get("idClient");
                    String origin = data.get("origin");
                 //   String destination  = data.get("destination");
                    String min = data.get("min");
                    String distance = data.get("distance");
                    String searchById = data.get("searchById");
                    showNotificationApiOreoActions(title, body, idClient, searchById);
                    showNotificationActivity(idClient, origin, min, distance, searchById);

                }
                else if (title.contains("EMERGENCIA CANCELADA")) {
                    NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    //ELIMINANDO LA NOTIFICACION DE SOLICITUD DE VIAJE
                    manager.cancel(2);
                   // showNotificationApiOreo(title, body);
                }
                else if (title.equals("MENSAJE")) {
                    getImageReceiver(data);
                }
                else {
                    showNotificationApiOreo(title, body);
                };
            }
            else {
                if (title.contains("SOLICITUD DE SERVICIO")) {
                    String idClient = data.get("idClient");
                    String origin = data.get("origin");
                   // String destination  = data.get("destination");
                    String min = data.get("min");
                    String distance = data.get("distance");
                    String searchById = data.get("searchById");
                    showNotificationActions(title, body, idClient, searchById);
                    showNotificationActivity(idClient, origin, min, distance, searchById);
                }
                else if (title.contains("EMERGENCIA CANCELADA")) {
                    NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    manager.cancel(2);
                    //ELIMINANDO LA NOTIFICACION DE SOLICITUD DE VIAJE
                    //showNotification(title, body);
                }
                else if (title.equals("MENSAJE")){
                    getImageReceiver(data);
                }
                else {
                    showNotification(title, body);
                }
            }
        }
    }

    private void getImageReceiver(final Map<String, String> data){
        final String imageReceiver = data.get("imageReceiver");
        Log.d("NOTIFICACION", "imageReceiver: " + imageReceiver);
        if (imageReceiver == null) {
            showNotificationMessage(data, null);
            return;
        }
        if (imageReceiver.equals("")) {
            showNotificationMessage(data, null);
            return;
        }

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Picasso.with(getApplicationContext()).load(imageReceiver).into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        showNotificationMessage(data, bitmap);

                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                        showNotificationMessage(data, null);
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });
            }
        });

    }



    private void showNotificationMessage(Map<String, String> data, Bitmap bitmapReceiver) {
      //  String idNotification = data.get("idNotification");
        String usernameSender = data.get("usernameSender");
        String usernameReceiver = data.get("usernameReceiver");
        String messagesJSON = data.get("messagesJSON");

        Gson gson = new Gson();
        Message[] messages = gson.fromJson(messagesJSON, Message[].class);

        NotificationHelper helper = new NotificationHelper(getBaseContext());

        NotificationCompat.Builder builder = helper.getNotificationMessage(messages, usernameReceiver, usernameSender, bitmapReceiver);
      //  int id = Integer.parseInt(idNotification);
      //  Log.d("NOTIFICACION", "ID: " + id);
        Log.d("NOTIFICACION", "usernameSender: " + usernameSender);
        Log.d("NOTIFICACION", "usernameReceiver: " + usernameReceiver);
        helper.getManager().notify(2, builder.build());


    }

    private void showNotificationActivity(String idClient, String origin, String min, String distance, String searchById) {
        PowerManager pm = (PowerManager) getBaseContext().getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = pm.isScreenOn();
        if (!isScreenOn) {
            PowerManager.WakeLock wakeLock = pm.newWakeLock(
                    PowerManager.PARTIAL_WAKE_LOCK |
                            PowerManager.ACQUIRE_CAUSES_WAKEUP |
                            PowerManager.ON_AFTER_RELEASE,
                    "AppName:MyLock"
            );
            wakeLock.acquire(10000);
        }
        Intent intent = new Intent(getBaseContext(), NotificationBookingActivity.class);
        intent.putExtra("idClient", idClient);
        intent.putExtra("origin", origin);
       // intent.putExtra("destination", destination);
        intent.putExtra("min", min);
        intent.putExtra("distance", distance);
        intent.putExtra("searchById", searchById);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

    }

    private void showNotification(String title, String body) {
        PendingIntent intent = PendingIntent.getActivity(getBaseContext(), 0, new Intent(), PendingIntent.FLAG_ONE_SHOT);
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationHelper notificationHelper = new NotificationHelper(getBaseContext());
        NotificationCompat.Builder builder = notificationHelper.getNotificationOldAPI(title, body, intent, sound);
        notificationHelper.getManager().notify(1, builder.build());
    }

    private void showNotificationActions(String title, String body, String idClient, String searchById) {

        // ACEPTAR
        Intent acceptIntent = new Intent(this, AcceptReceiver.class);
        acceptIntent.putExtra("idClient", idClient);
        acceptIntent.putExtra("searchById", searchById);
        PendingIntent acceptPendingIntent = PendingIntent.getBroadcast(this, NOTIFICATION_CODE, acceptIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Action acceptAction = new NotificationCompat.Action.Builder(
                R.mipmap.ic_launcher,
                "Aceptar",
                acceptPendingIntent
        ).build();

        // CANCELAR
        Intent cancelIntent = new Intent(this, CancelReceiver.class);
        cancelIntent.putExtra("idClient", idClient);
        cancelIntent.putExtra("searchById", searchById);
        PendingIntent cancelPendingIntent = PendingIntent.getBroadcast(this, NOTIFICATION_CODE, cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Action cancelAction = new NotificationCompat.Action.Builder(
                R.mipmap.ic_launcher,
                "Cancelar",
                cancelPendingIntent
        ).build();



        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationHelper notificationHelper = new NotificationHelper(getBaseContext());
        NotificationCompat.Builder builder = notificationHelper.getNotificationOldAPIActions(title, body, sound, acceptAction, cancelAction);
        notificationHelper.getManager().notify(2, builder.build());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showNotificationApiOreo(String title, String body) {
        PendingIntent intent = PendingIntent.getActivity(getBaseContext(), 0, new Intent(), PendingIntent.FLAG_ONE_SHOT);
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationHelper notificationHelper = new NotificationHelper(getBaseContext());
        Notification.Builder builder = notificationHelper.getNotification(title, body, intent, sound);

        notificationHelper.getManager().notify(1, builder.build());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showNotificationApiOreoActions(String title, String body, String idClient, String searchById) {
// ACEPTAR
        Intent acceptIntent = new Intent(this, AcceptReceiver.class);
        acceptIntent.putExtra("idClient", idClient);
        acceptIntent.putExtra("searchById", searchById);
        PendingIntent acceptPendingIntent = PendingIntent.getBroadcast(this, NOTIFICATION_CODE, acceptIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Action acceptAction= new Notification.Action.Builder(
                R.mipmap.ic_launcher,
                "Aceptar",
                acceptPendingIntent
        ).build();
// CANCELAR
        Intent cancelIntent = new Intent(this, CancelReceiver.class);
        cancelIntent.putExtra("idClient", idClient);
        cancelIntent.putExtra("searchById", searchById);
        PendingIntent cancelPendingIntent = PendingIntent.getBroadcast(this, NOTIFICATION_CODE, cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Action cancelAction= new Notification.Action.Builder(
                R.mipmap.ic_launcher,
                "Cancelar",
                cancelPendingIntent
        ).build();


        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationHelper notificationHelper = new NotificationHelper(getBaseContext());
        Notification.Builder builder = notificationHelper.getNotificationActions(title, body, sound, acceptAction, cancelAction);
        notificationHelper.getManager().notify(2, builder.build());
    }

}
