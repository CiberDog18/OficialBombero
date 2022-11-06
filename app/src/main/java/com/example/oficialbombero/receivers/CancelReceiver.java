package com.example.oficialbombero.receivers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.oficialbombero.providers.AuthProvider;
import com.example.oficialbombero.providers.ClientBookingProvider;
import com.example.oficialbombero.providers.DriversFoundProvider;

public class CancelReceiver extends BroadcastReceiver {
    private ClientBookingProvider mClientBookingProvider;
    private DriversFoundProvider mDriversFoundProvider;
    private AuthProvider mAuthProvider;
    @Override
    public void onReceive(Context context, Intent intent) {
        String idClient = intent.getExtras().getString("idClient");
        String searchById = intent.getExtras().getString("searchById");
        mClientBookingProvider = new ClientBookingProvider();
        mDriversFoundProvider = new DriversFoundProvider();
        mAuthProvider = new AuthProvider();

        if (searchById.equals("true")){

            mClientBookingProvider.updateStatus(idClient, "cancel");
        }


        mDriversFoundProvider.delete(mAuthProvider.getId());

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(2);

    }
}
