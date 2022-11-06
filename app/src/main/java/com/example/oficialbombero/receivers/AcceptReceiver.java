package com.example.oficialbombero.receivers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.oficialbombero.activities.MapDriverActivity;
import com.example.oficialbombero.activities.MapDriverBookingActivity;
import com.example.oficialbombero.providers.AuthProvider;
import com.example.oficialbombero.providers.ClientBookingProvider;
import com.example.oficialbombero.providers.GeoFireProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class AcceptReceiver extends BroadcastReceiver {
    private ClientBookingProvider mClientBookingProvider;
    private GeoFireProvider mGeofireProvider;
    private AuthProvider mAuthProvider;

    @Override
    public void onReceive(Context context, Intent intent) {
        mAuthProvider = new AuthProvider();
        mGeofireProvider = new GeoFireProvider("active_drivers");
        mGeofireProvider.removeLocation(mAuthProvider.getId());


        String idClient = intent.getExtras().getString("idClient");
        String searchById = intent.getExtras().getString("searchById");
        mClientBookingProvider = new ClientBookingProvider();

        if (searchById.equals("true")){
            mClientBookingProvider.updateStatus(idClient, "accept");
            Intent intent1 = new Intent(context, MapDriverBookingActivity.class);
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent1.setAction(Intent.ACTION_RUN);
            intent1.putExtra("idClient", idClient);
            context.startActivity(intent1);

        }
        else {
            checkIfClientBookingWasAccept(idClient, context);
        }






        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(2);





    }

    private void checkIfClientBookingWasAccept(final String idClient, final Context context) {

        mClientBookingProvider.getClientBooking(idClient).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    if (snapshot.hasChild("idDriver") && snapshot.hasChild("status")){
                        String status = snapshot.child("status").getValue().toString();
                        String idDriver = snapshot.child("idDriver").getValue().toString();

                        if (status.equals("create") && idDriver.equals("")){
                            mClientBookingProvider.updateStatusAndIdDriver(idClient, "accept", mAuthProvider.getId());
                            Intent intent1 = new Intent(context, MapDriverBookingActivity.class);
                            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent1.setAction(Intent.ACTION_RUN);
                            intent1.putExtra("idClient", idClient);
                            context.startActivity(intent1);

                        }
                        else {
                           goToMapDriverActivity(context);

                        }

                    }
                    else {
                        goToMapDriverActivity(context);
                    }
                }
                else {
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
}
