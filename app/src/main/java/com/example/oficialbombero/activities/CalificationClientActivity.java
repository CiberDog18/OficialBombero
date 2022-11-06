package com.example.oficialbombero.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.oficialbombero.R;
import com.example.oficialbombero.models.ClientBooking;
import com.example.oficialbombero.models.HistoryBooking;
import com.example.oficialbombero.providers.ClientBookingProvider;
import com.example.oficialbombero.providers.HistoryBookingProvider;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

public class CalificationClientActivity extends AppCompatActivity {

    private TextView mTextViewOrigin;
   // private TextView mTextViewDestination;
 //   private TextView mTextViewPrice;
    private RatingBar mRatinBar;
    private Button mButtonCalification;

    private ClientBookingProvider mClientBookingProvider;

    private String mExtraClientId;

    private HistoryBooking mHistoryBooking;
    private HistoryBookingProvider mHistoryBookingProvider;


    private float mCalification = 0;

    private double mExtraPrice = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calification_client);
       // mTextViewDestination = findViewById(R.id.textViewDestinationCalification);
        mTextViewOrigin = findViewById(R.id.textViewOriginCalification);
       // mTextViewPrice = findViewById(R.id.textViewPrice);
        mRatinBar = findViewById(R.id.ratingbarCalification);
        mButtonCalification = findViewById(R.id.btnCalification);
        mClientBookingProvider = new ClientBookingProvider();
        mHistoryBookingProvider = new HistoryBookingProvider();

        mExtraClientId = getIntent().getStringExtra("idClient");
        mExtraPrice = getIntent().getDoubleExtra("price", 0);
        double aprox = mExtraPrice;
        double total = aprox - (aprox % 100) + 100;
       // mTextViewPrice.setText(String.format("%.0f",  total) + "$");

        mRatinBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float calification, boolean fromUser) {
                mCalification = calification;

            }
        });
        mButtonCalification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calificate();
            }
        });

        getClientBooking();



    }

    private void getClientBooking(){
        mClientBookingProvider.getClientBooking(mExtraClientId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    ClientBooking clientBooking = dataSnapshot.getValue(ClientBooking.class);
                    mTextViewOrigin.setText(clientBooking.getOrigin());
                  //  mTextViewDestination.setText(clientBooking.getDestination());
                    mHistoryBooking = new HistoryBooking(
                            clientBooking.getIdHistoryBooking(),
                            clientBooking.getIdClient(),
                            clientBooking.getIdDriver(),
                            clientBooking.getOrigin(),
                            clientBooking.getTime(),
                            clientBooking.getKm(),
                            clientBooking.getStatus(),
                            clientBooking.getOriginLat(),
                            clientBooking.getOriginLng()
                    );
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    private void calificate() {
        if (mCalification  > 0) {
            mHistoryBooking.setCalificationClient(mCalification);
            mHistoryBooking.setTimestamp(new Date().getTime());
            mHistoryBookingProvider.getHistoryBooking(mHistoryBooking.getIdHistoryBooking()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        mHistoryBookingProvider.updateCalificactionClient(mHistoryBooking.getIdHistoryBooking(), mCalification).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(CalificationClientActivity.this, "La calificacion se guardo correctamente", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(CalificationClientActivity.this, MapDriverActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                    }
                    else {
                        mHistoryBookingProvider.create(mHistoryBooking).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(CalificationClientActivity.this, "La calificacion se guardo correctamente", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(CalificationClientActivity.this, MapDriverActivity.class);
                                startActivity(intent);
                                finish();

                            }
                        });
                    }

                }


                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        else {

        }



    }


}