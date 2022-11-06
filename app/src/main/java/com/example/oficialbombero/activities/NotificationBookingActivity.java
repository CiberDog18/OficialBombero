package com.example.oficialbombero.activities;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.oficialbombero.R;
import com.example.oficialbombero.models.FCMBody;
import com.example.oficialbombero.models.FCMResponse;
import com.example.oficialbombero.providers.AuthProvider;
import com.example.oficialbombero.providers.ClientBookingProvider;
import com.example.oficialbombero.providers.DriversFoundProvider;
import com.example.oficialbombero.providers.GeoFireProvider;
import com.example.oficialbombero.providers.NotificationProvider;
import com.example.oficialbombero.providers.TokenProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationBookingActivity extends AppCompatActivity {

   // private TextView mTextViewDestination;
    private TextView mTextViewOrigin;
    private TextView mTextViewMin;
    private TextView mTextViewDistance;
    private TextView mTextViewCounter;
    private Button mbuttonAccept;
    private Button mbuttonCancel;

    private ClientBookingProvider mClientBookingProvider;

    private GeoFireProvider mGeofireProvider;
    private AuthProvider mAuthProvider;
    private DriversFoundProvider mDriversFoundProvider;
    private TokenProvider mTokenProvider;
    private NotificationProvider mNotificationProvider;

    private String mExtraClientId;

    private String mExtraIdClient;
    private String mExtraOrigin;
    private String mExtraDestination;
    private String mExtraMin;
    private String mExtraDistance;
    private String mExtraSearchById;

    RoundedImageView mimageClientToken;


    boolean mRideStart = false;
    double mDistanceInMeters = 1;
    Location mPreviusLocation = new Location("");

    private MediaPlayer mMediaPlayer;

    private int mCounter = 120;
    private Handler mHandler;
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            mCounter = mCounter - 1;
            mTextViewCounter.setText(String.valueOf(mCounter));
            if (mCounter > 0) {
                initTimer();
            } else {
                cancelBooking();
            }

        }
    };


    private void initTimer() {
        mHandler = new Handler();
        mHandler.postDelayed(runnable, 1000);
    }

    private ValueEventListener mListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_booking);
      //  mTextViewDestination = findViewById(R.id.textViewDestination);
        mTextViewOrigin = findViewById(R.id.textViewOrigin);

        mTextViewCounter = findViewById(R.id.textViewCounter);
        mimageClientToken = findViewById(R.id.imageClientToken);
        mAuthProvider = new AuthProvider();
        mDriversFoundProvider = new DriversFoundProvider();
        mNotificationProvider = new NotificationProvider();
        mTokenProvider = new TokenProvider();

        mExtraClientId = getIntent().getStringExtra("idClient");
        mbuttonAccept = findViewById(R.id.btnAcceptBooking);
        mbuttonCancel = findViewById(R.id.btnCancelBooking);

        mExtraIdClient = getIntent().getStringExtra("idClient");
        mExtraOrigin = getIntent().getStringExtra("origin");
       // mExtraDestination = getIntent().getStringExtra("destination");
        mExtraMin = getIntent().getStringExtra("min");
        mExtraDistance = getIntent().getStringExtra("distance");
        mExtraSearchById = getIntent().getStringExtra("searchById");

      //  mTextViewDestination.setText(mExtraDestination);
   //     mTextViewOrigin.setText(mExtraOrigin);
      //  mTextViewMin.setText(mExtraMin);
     //   mTextViewDistance.setText(mExtraDistance);

        mMediaPlayer = MediaPlayer.create(this, R.raw.ringtone);
        mMediaPlayer.setLooping(true);
        mClientBookingProvider = new ClientBookingProvider();



        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        );
        getImageBooking(mExtraIdClient);

        initTimer();

        checkIfClientCancelBooking();

        mbuttonAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptBooking();

            }
        });

        mbuttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelBooking();

            }
        });
    }

    private void getImageBooking(final String idClient) {

        mClientBookingProvider.getClientBooking(mExtraClientId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("imageRequest")) {
                    String image = dataSnapshot.child("imageRequest").getValue().toString();
                    Picasso.with(NotificationBookingActivity.this).load(image).into(mimageClientToken);



                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void checkIfClientBookingWasAccept(final String idClient, final Context context) {

        mClientBookingProvider.getClientBooking(idClient).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (snapshot.hasChild("idDriver") && snapshot.hasChild("status")) {
                        String status = snapshot.child("status").getValue().toString();
                        String idDriver = snapshot.child("idDriver").getValue().toString();

                        if (status.equals("create") && idDriver.equals("")) {
                            mClientBookingProvider.updateStatusAndIdDriver(idClient, "accept", mAuthProvider.getId());
                            Intent intent1 = new Intent(context, MapDriverBookingActivity.class);
                            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent1.setAction(Intent.ACTION_RUN);
                            intent1.putExtra("idClient", idClient);
                            context.startActivity(intent1);

                        } else {
                            goToMapDriverActivity(context);

                        }

                    } else {
                        goToMapDriverActivity(context);
                    }
                } else {
                    goToMapDriverActivity(context);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void goToMapDriverActivity(final Context context) {
        Toast.makeText(context, "Otro bombero ya acepto el servicio", Toast.LENGTH_SHORT).show();
        Intent intent1 = new Intent(context, MapDriverActivity.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent1.setAction(Intent.ACTION_RUN);
        context.startActivity(intent1);
    }


    private void checkIfClientCancelBooking() {
        mListener = mClientBookingProvider.getClientBooking(mExtraIdClient).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    goToMapDriverActivity();
                }
                //El CLIENT BOOKING SI EXISTE
                else if (dataSnapshot.hasChild("idDriver") && dataSnapshot.hasChild("status")) {
                    String idDriver = dataSnapshot.child("idDriver").getValue().toString();
                    String status = dataSnapshot.child("status").getValue().toString();
                    if ((status.equals("accept") || status.equals("cancel")) && !idDriver.equals(mAuthProvider.getId())) {
                        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        manager.cancel(2);
                        goToMapDriverActivity();

                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void goToMapDriverActivity() {
        Toast.makeText(NotificationBookingActivity.this, "El usuario ya no esta disponible", Toast.LENGTH_LONG).show();
        if (mHandler != null) mHandler.removeCallbacks(runnable);
        Intent intent = new Intent(NotificationBookingActivity.this, MapDriverActivity.class);
        startActivity(intent);
        finish();
    }

    private void cancelBooking() {

        if (mHandler != null) mHandler.removeCallbacks(runnable);
        if (mExtraSearchById.equals("true")) {

             mClientBookingProvider.updateStatus(mExtraIdClient, "cancel");
        }


        mDriversFoundProvider.delete(mAuthProvider.getId());
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(2);
        Intent intent = new Intent(NotificationBookingActivity.this, MapDriverActivity.class);
        startActivity(intent);

        finish();
    }


    private void sendNotification(final String status) {
        mTokenProvider.getToken(mExtraClientId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String token = dataSnapshot.child("token").getValue().toString();
                    Map<String, String> map = new HashMap<>();
                    map.put("title", "ESTADO DE LA EMERGENCIA");
                    map.put("body",
                            "Tu estado de la solicitud es: " + status
                    );
                    FCMBody fcmBody = new FCMBody(token, "high", "4500s", map);
                    mNotificationProvider.sendNotification(fcmBody).enqueue(new Callback<FCMResponse>() {
                        @Override
                        public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                            if (response.body() != null) {
                                if (response.body().getSuccess() != 1) {
                                    Toast.makeText(NotificationBookingActivity.this, "No se pudo enviar la notificacion", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(NotificationBookingActivity.this, "No se pudo enviar la notificacion", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<FCMResponse> call, Throwable t) {
                            Log.d("Error", "Error " + t.getMessage());
                        }
                    });
                } else {
                    Toast.makeText(NotificationBookingActivity.this, "No se pudo enviar la notificacion porque el bombero no tiene un token de sesion", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }



    private void acceptBooking() {

        if (mHandler != null) mHandler.removeCallbacks(runnable);
        mGeofireProvider = new GeoFireProvider("active_drivers");
        mGeofireProvider.removeLocation(mAuthProvider.getId());

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(2);

        if (mExtraSearchById.equals("true")) {
            mClientBookingProvider.updateStatus(mExtraIdClient, "accept");
            Intent intent1 = new Intent(NotificationBookingActivity.this, MapDriverBookingActivity.class);
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent1.setAction(Intent.ACTION_RUN);
            intent1.putExtra("idClient", mExtraIdClient);
            startActivity(intent1);


        } else {
            checkIfClientBookingWasAccept(mExtraIdClient, NotificationBookingActivity.this);

        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.release();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mMediaPlayer != null) {
            if (!mMediaPlayer.isPlaying()) {
                mMediaPlayer.start();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHandler != null) mHandler.removeCallbacks(runnable);

        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
            }
        }
        if (mListener != null) {
            mClientBookingProvider.getClientBooking(mExtraIdClient).removeEventListener(mListener);
        }

    }
}