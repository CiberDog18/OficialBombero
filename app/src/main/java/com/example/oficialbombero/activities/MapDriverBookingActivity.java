package com.example.oficialbombero.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.oficialbombero.R;
import com.example.oficialbombero.models.Chat;
import com.example.oficialbombero.models.FCMBody;
import com.example.oficialbombero.models.FCMResponse;
import com.example.oficialbombero.models.Info;
import com.example.oficialbombero.providers.AuthProvider;
import com.example.oficialbombero.providers.ClientBookingProvider;
import com.example.oficialbombero.providers.ClientProvider;
import com.example.oficialbombero.providers.ConductorProvider;
import com.example.oficialbombero.providers.GeoFireProvider;
import com.example.oficialbombero.providers.GoogleApiProvider;
import com.example.oficialbombero.providers.InfoProvider;
import com.example.oficialbombero.providers.MessagesProvider;
import com.example.oficialbombero.providers.NotificationProvider;
import com.example.oficialbombero.providers.TokenProvider;
import com.example.oficialbombero.service.ForegroundService;
import com.example.oficialbombero.utils.AppBackgroundHelper;
import com.example.oficialbombero.utils.CarMoveAnim;
import com.example.oficialbombero.utils.DecodePoints;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapDriverBookingActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private SupportMapFragment mMapFragment;
    private AuthProvider mAuthProvider;
    private GeoFireProvider mGeofireProvider;
    private TokenProvider mTokenProvider;
    private ClientProvider mClientProvider;
    private ClientBookingProvider mClientBookingProvider;
    private ConductorProvider mDriverProvider;
    private NotificationProvider mNotificationProvider;
    private ImageView mImageViewBooking;
    MessagesProvider messagesProvider;
    private InfoProvider mInfoProvider;

    private Info mInfo;

    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocation;

    private final static int LOCATION_REQUEST_CODE = 1;
    private final static int SETTINGS_REQUEST_CODE = 2;

    private Marker mMarker;
    private LatLng mCurrentLatLng;

    private TextView mTextViewClientBooking;
    private TextView mTextViewEmailClientBooking;
    private TextView mTextViewOriginClientBooking;
  //  TextView mtextViewMessagesNotRead;
   // private TextView mTextViewDestinationClientBooking;
    private TextView mTextViewTime;
    private CircleImageView mCircleImageBack;

    private String mExtraClientId;
    private String mExtraToken;

    private String mOrigin;
    private LatLng mOriginLatLng;

    private String mDestination;
    private LatLng mDestinationLatLng;
    private LatLng mDriverLatLng;


    private GoogleApiProvider mGoogleApiProvider;
    private List<LatLng> mPolylineList;
    private PolylineOptions mPolylineOptions;

    private boolean mIsFirstTime = true;
   // private boolean mIsCloseToClient = false;

    private Button mButtonStartBooking;
    private Button mButtonFinishBooking;
    //FrameLayout mframeLayoutMessagesNotRead;
    private CardView mCardviewChat;

    double mDistanceInMeters = 1;
    int mMinutes = 0;
    int mSeconds = 0;
    boolean mSecondIsOver = false;
    boolean mRideStart = false;
    Handler mHandler = new Handler();
    Location mPreviusLocation = new Location("");

    SharedPreferences mPref;
    SharedPreferences.Editor mEditor;

    boolean mIsFinishBooking = false;


    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            mSeconds++;
            if (!mSecondIsOver) {
                mTextViewTime.setText(mSeconds + " Seg");
            } else {
                mTextViewTime.setText(mMinutes + " Min " + mSeconds);
            }
            if (mSeconds == 59) {
                mSeconds = 0;
                mSecondIsOver = true;
                mMinutes++;
            }
            mHandler.postDelayed(runnable, 1000);

        }
    };

    private boolean mIsStartLocation = false;
    LatLng mStartLatLng;
    LatLng mEndLatLng;
    LocationManager mLocationManager;

    LocationListener locationListenerGPS = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            mCurrentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

          /*  if (mRideStart) {
                mDistanceInMeters = mDistanceInMeters + mPreviusLocation.distanceTo(location);
            } */

            mPreviusLocation = location;

            if (mStartLatLng != null) {
                mEndLatLng = mStartLatLng;
            }

            mStartLatLng = new LatLng(mCurrentLatLng.latitude, mCurrentLatLng.longitude);

            if (mEndLatLng != null) {
                CarMoveAnim.carAnim(mMarker, mEndLatLng, mStartLatLng);
            }

            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                    new CameraPosition.Builder()
                            .target(new LatLng(location.getLatitude(), location.getLongitude()))
                            .zoom(18f)
                            .build()
            ));

            updateLocation();

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };


    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {
                if (getApplicationContext() != null) {

                    mCurrentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

                    if (!mIsStartLocation) {
                        mMap.clear();

                        mMarker = mMap.addMarker(new MarkerOptions().position(
                                new LatLng(location.getLatitude(), location.getLongitude()))
                                .title("Tu posicion")
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.car_topview))
                        );
                        // OBTENER LA LOCALIZACION DEL USUARIO EN TIEMPO REAL
                        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(
                                new CameraPosition.Builder()
                                        .target(new LatLng(location.getLatitude(), location.getLongitude()))
                                        .zoom(16f)
                                        .build()
                        ));

                        updateLocation();

                        if (mIsFirstTime) {
                            mIsFirstTime = false;
                            getClientBooking();
                        }
                        mIsStartLocation = true;
                        if (ActivityCompat.checkSelfPermission(MapDriverBookingActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapDriverBookingActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListenerGPS);

                        stopLocation();
                    }

                }
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_driver_booking);

        mAuthProvider = new AuthProvider();
        mGeofireProvider = new GeoFireProvider("drivers_working");
        mTokenProvider = new TokenProvider();
        mClientProvider = new ClientProvider();
        mDriverProvider = new ConductorProvider();
        mClientBookingProvider = new ClientBookingProvider();
        mNotificationProvider = new NotificationProvider();
        mInfoProvider = new InfoProvider();
        messagesProvider = new MessagesProvider();

        mFusedLocation = LocationServices.getFusedLocationProviderClient(this);
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);

        mTextViewClientBooking = findViewById(R.id.textViewClientBooking);
        mTextViewEmailClientBooking = findViewById(R.id.textViewEmailClientBooking);
        mTextViewOriginClientBooking = findViewById(R.id.textViewOriginClientBooking);
    //    mTextViewDestinationClientBooking = findViewById(R.id.textViewDestinationClientBooking);
        mTextViewTime = findViewById(R.id.textViewTime);
        mImageViewBooking = findViewById(R.id.imageViewClientBooking);
        mCardviewChat = findViewById(R.id.cardviewChat);
        mCircleImageBack = findViewById(R.id.circleImageBack);
      //  mframeLayoutMessagesNotRead = findViewById(R.id.frameLayoutMessagesNotRead);
        //mtextViewMessagesNotRead = findViewById(R.id.textViewMessagesNotRead);


        mButtonStartBooking = findViewById(R.id.btnStartBooking);
        mButtonFinishBooking = findViewById(R.id.btnFinishBooking);

        mExtraClientId = getIntent().getStringExtra("idClient");
        mGoogleApiProvider = new GoogleApiProvider(MapDriverBookingActivity.this);
        getInfo();
        getClient();


        mCardviewChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getChat(mExtraClientId);


            }
        });

        mButtonStartBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startBooking();
              //  if (mIsCloseToClient) {
               // } else {
                  //  Toast.makeText(MapDriverBookingActivity.this, "Debes estar mas cerca a la emergencia", Toast.LENGTH_SHORT).show();
               // }
            }
        });


        mCircleImageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backBooking();
            }
        });
        mButtonFinishBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishBooking();

            }
        });

        createToken();
    }

    /*private void getMessagesNotRead(final String idChat) {
        messagesProvider.getReceiverMessagesNotRead(idChat, mAuthProvider.getId()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException error) {
                if (querySnapshot != null) {
                    int size = querySnapshot.size();
                    if (size > 0) {
                        mframeLayoutMessagesNotRead.setVisibility(View.VISIBLE);
                        mtextViewMessagesNotRead.setText(String.valueOf(size));

                    }
                    else {
                        mframeLayoutMessagesNotRead.setVisibility(View.GONE);

                    }
                }

            }
        });




    }*/

    private void createToken() {
        mDriverProvider.createToken(mAuthProvider.getId());
    }

    private void getChat(String mExtraClientId) {
        Chat chat = new Chat();
        Intent intent = new Intent(MapDriverBookingActivity.this, ChatActivity.class);
        intent.putExtra("idClient", mExtraClientId);
        intent.putExtra("idChat", chat.getId());
        startActivity(intent);
        //getMessagesNotRead(chat.getId());
        Toast.makeText(this, "ID: " + mExtraClientId, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        AppBackgroundHelper.online(MapDriverBookingActivity.this, true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeLocation();
        stopLocation();
    }

    @Override
    protected void onStop() {
        super.onStop();
        AppBackgroundHelper.online(MapDriverBookingActivity.this, false);
        if (!mIsFinishBooking) {

            startService();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        stopService();

    }

    private void stopLocation() {
        if (mLocationCallback != null && mFusedLocation != null) {
            mFusedLocation.removeLocationUpdates(mLocationCallback);
        }

    }

    private void startService() {
        stopLocation();
        Intent serviceIntent = new Intent(this, ForegroundService.class);
        ContextCompat.startForegroundService(MapDriverBookingActivity.this, serviceIntent);

    }

    private void stopService() {
        startLocation();
        Intent serviceIntent = new Intent(this, ForegroundService.class);
        stopService(serviceIntent);
    }


    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    /* private void calculateRide() {
        if (mMinutes == 0) {
            mMinutes = 1;
        }
        double priceMin = mMinutes * mInfo.getMin();
        double priceKm = (mDistanceInMeters / 1000) * mInfo.getKm();
        double tariMin = mInfo.getTarifaMin();
        double totalAux = 0;
        double auxPrice = 0;
        auxPrice = priceMin + priceKm;
        Log.d("VALORES", "Min Total: " + priceMin);
        Log.d("VALORES", "Min Total: " + priceKm);
        Log.d("VALORES", "Tarifa Minima: " + tariMin);
        if (auxPrice < tariMin){
            totalAux = tariMin;
        }
        else{
            totalAux = priceMin + priceKm;
        }
        Log.d("VALORES", "Total AUX: " + totalAux);
        final double total = totalAux;



        Log.d("VALORES", "Min Total: " + total);
        Log.d("VALORES", "Min Total: " + mMinutes);
        Log.d("VALORES", "Km Total: " + (mDistanceInMeters / 1000));

        mClientBookingProvider.updatePrice(mExtraClientId, total).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mClientBookingProvider.updateStatus(mExtraClientId, "finish");

                Intent intent = new Intent(MapDriverBookingActivity.this, CalificationClientActivity.class);
                intent.putExtra("idClient", mExtraClientId);
                intent.putExtra("price", total);f
                startActivity(intent);
                finish();

            }
        });

    }*/


    private void getInfo() {

        mInfoProvider.getInfo().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    mInfo = snapshot.getValue(Info.class);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void backBooking() {
        mIsFinishBooking = true;
        mEditor.clear().commit();
        sendNotification("Emergencia Cancelada");
        stopLocation();
        mGeofireProvider.removeLocation(mAuthProvider.getId());
        if (mHandler != null) {
            mHandler.removeCallbacks(runnable);
        }

        mClientBookingProvider.updateStatus(mExtraClientId, "cancelado");
        Intent intent = new Intent(MapDriverBookingActivity.this, MapDriverActivity.class);
        startActivity(intent);
        finish();


    }

    private void finishBooking() {
        mClientBookingProvider.updateIdHistoryBooking(mExtraClientId).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //LIEMPIAR TODS LO DATOS ALMACENADOS EN SHARE PREFERENCE
                mIsFinishBooking = true;
                mEditor.clear().commit();
                sendNotification("Emergencia finalizada");
                stopLocation();
                mGeofireProvider.removeLocation(mAuthProvider.getId());
                if (mHandler != null) {
                    mHandler.removeCallbacks(runnable);
                }
                mClientBookingProvider.updateStatus(mExtraClientId, "finish");
                Intent intent = new Intent(MapDriverBookingActivity.this, CalificationClientActivity.class);
                intent.putExtra("idClient", mExtraClientId);
      //          intent.putExtra("price", total);
                startActivity(intent);
                finish();

             // calculateRide();
            }
        });


    }


    private void startBooking() {
        mEditor.putString("status", "start");
        mEditor.putString("idClient", mExtraClientId);
        mEditor.apply();

        mClientBookingProvider.updateStatus(mExtraClientId, "start");
        mButtonStartBooking.setVisibility(View.GONE);
        mButtonFinishBooking.setVisibility(View.VISIBLE);
        mCircleImageBack.setVisibility(View.GONE);


      /*  mMap.clear();
      mMap.addMarker(new MarkerOptions().position(mDestinationLatLng).title("Destino").icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_pin_blue)));

        if (mCurrentLatLng != null){
            mMarker = mMap.addMarker(new MarkerOptions().position(
                    new LatLng(mCurrentLatLng.latitude, mCurrentLatLng.longitude))
                    .title("Tu posicion")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.car_topview))
            );

        } */

       // drawRoute(mDestinationLatLng);
        sendNotification("Emergencia iniciada");
        mRideStart = true;
        mHandler.postDelayed(runnable, 1000);


    }

    private double getDistanceBetween(LatLng clientLatLng, LatLng driverLatLng) {
        double distance = 0;
        Location clientLocation = new Location("");
        Location driverLocation = new Location("");
        clientLocation.setLatitude(clientLatLng.latitude);
        clientLocation.setLongitude(clientLatLng.longitude);
        driverLocation.setLatitude(driverLatLng.latitude);
        driverLocation.setLongitude(driverLatLng.longitude);
        distance = clientLocation.distanceTo(driverLocation);
        return distance;
    }

    private void getClientBooking() {
        mClientBookingProvider.getClientBooking(mExtraClientId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    //String destination = dataSnapshot.child("destination").getValue().toString();
                    String origin = dataSnapshot.child("origin").getValue().toString();
                    //double destinationLat = Double.parseDouble(dataSnapshot.child("destinationLat").getValue().toString());
                    //double destinationLng = Double.parseDouble(dataSnapshot.child("destinationLng").getValue().toString());
                    double originLat = Double.parseDouble(dataSnapshot.child("originLat").getValue().toString());
                    double originLng = Double.parseDouble(dataSnapshot.child("originLng").getValue().toString());
                    mOriginLatLng = new LatLng(originLat, originLng);
                    //mDestinationLatLng = new LatLng(destinationLat, destinationLng);
                    mTextViewOriginClientBooking.setText("Emergencia en: " + origin);
                   // mTextViewDestinationClientBooking.setText("Destino: " + destination);

                    mPref = getApplicationContext().getSharedPreferences("RideStatus", MODE_PRIVATE);
                    mEditor = mPref.edit();
// obtener el ultimo estado almacenado en el share preference
                    String status = mPref.getString("status", "");

                    if (status.equals("start")) {
                        startBooking();
                    } else {
                        //ESTE VALOR SE ALMACENA CUANDO EL CONDUCTOR INICA EL VIAJE POR PRIMERA VEZ
                        mEditor.putString("status", "ride");
                        mEditor.putString("idClient", mExtraClientId);
                        mEditor.apply();
                        mMap.addMarker(new MarkerOptions().position(mOriginLatLng).title("Emergecia aqui").icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_pin_red)));
                        drawRoute(mOriginLatLng);
                    }


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private void getClient() {
        mClientProvider.getClient(mExtraClientId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String name = dataSnapshot.child("name").getValue().toString();
                    String apellido = dataSnapshot.child("apellido").getValue().toString();
                    String email = dataSnapshot.child("email").getValue().toString();
                    String image = "";
                    if (dataSnapshot.hasChild("image")) {
                        image = dataSnapshot.child("image").getValue().toString();
                        Picasso.with(MapDriverBookingActivity.this).load(image).into(mImageViewBooking);
                    }
                    mTextViewClientBooking.setText(name + " " + apellido);
                    mTextViewEmailClientBooking.setText(email);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        mClientBookingProvider.getClientBooking(mExtraClientId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("imageRequest")) {
                    String image = dataSnapshot.child("imageRequest").getValue().toString();
                    Picasso.with(MapDriverBookingActivity.this).load(image).into(mImageViewBooking);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void drawRoute(LatLng latLng) {
        mGoogleApiProvider.getDirections(mCurrentLatLng, mOriginLatLng).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {

                    JSONObject jsonObject = new JSONObject(response.body());
                    JSONArray jsonArray = jsonObject.getJSONArray("routes");
                    JSONObject route = jsonArray.getJSONObject(0);
                    JSONObject polylines = route.getJSONObject("overview_polyline");
                    String points = polylines.getString("points");
                    mPolylineList = DecodePoints.decodePoly(points);
                    mPolylineOptions = new PolylineOptions();
                    mPolylineOptions.color(Color.DKGRAY);
                    mPolylineOptions.width(13f);
                    mPolylineOptions.startCap(new SquareCap());
                    mPolylineOptions.jointType(JointType.ROUND);
                    mPolylineOptions.addAll(mPolylineList);
                    mMap.addPolyline(mPolylineOptions);

                    JSONArray legs = route.getJSONArray("legs");
                    JSONObject leg = legs.getJSONObject(0);
                    JSONObject distance = leg.getJSONObject("distance");
                    JSONObject duration = leg.getJSONObject("duration");
                    String distanceText = distance.getString("text");
                    String durationText = duration.getString("text");

                } catch (Exception e) {
                    Log.d("Error", "Error encontrado " + e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(false);

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(5);

        startLocation();

    }


    private void updateLocation() {
        if (mAuthProvider.existSession() && mCurrentLatLng != null) {
            mGeofireProvider.saveLocation(mAuthProvider.getId(), mCurrentLatLng);
       //     if (!mIsCloseToClient) {
                if (mOriginLatLng != null && mCurrentLatLng != null) {
                    double distance = getDistanceBetween(mOriginLatLng, mCurrentLatLng); // METROS
                    if (distance <= 150) {
                        mButtonStartBooking.setEnabled(true);
                      //  mIsCloseToClient = true;
                        Toast.makeText(this, "Estas cerca de la de emergencia", Toast.LENGTH_SHORT).show();
                    }
                }
           // }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    if (gpsActived()) {
                        mFusedLocation.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                    } else {
                        showAlertDialogNOGPS();
                    }
                } else {
                    checkLocationPermissions();
                }
            } else {
                checkLocationPermissions();
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SETTINGS_REQUEST_CODE && gpsActived()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mFusedLocation.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
        } else {
            showAlertDialogNOGPS();
        }
    }

    private void showAlertDialogNOGPS() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Por favor activa tu ubicacion para continuar")
                .setPositiveButton("Configuraciones", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), SETTINGS_REQUEST_CODE);
                    }
                }).create().show();
    }

    private boolean gpsActived() {
        boolean isActive = false;
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            isActive = true;
        }
        return isActive;
    }

    private void disconnect() {

        if (mFusedLocation != null) {
            mFusedLocation.removeLocationUpdates(mLocationCallback);
            if (mAuthProvider.existSession()) {
                mGeofireProvider.removeLocation(mAuthProvider.getId());
            }
        } else {
            Toast.makeText(this, "No te puedes desconectar", Toast.LENGTH_SHORT).show();
        }
    }

    private void startLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (gpsActived()) {

                    mFusedLocation.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                } else {
                    showAlertDialogNOGPS();
                }
            } else {
                checkLocationPermissions();
            }
        } else {
            if (gpsActived()) {
                mFusedLocation.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            } else {
                showAlertDialogNOGPS();
            }
        }
    }

    private void checkLocationPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this)
                        .setTitle("Proporciona los permisos para continuar")
                        .setMessage("Esta aplicacion requiere de los permisos de ubicacion para poder utilizarse")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(MapDriverBookingActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
                            }
                        })
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(MapDriverBookingActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            }
        }
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
                                    Toast.makeText(MapDriverBookingActivity.this, "No se pudo enviar la notificacion", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(MapDriverBookingActivity.this, "No se pudo enviar la notificacion", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<FCMResponse> call, Throwable t) {
                            Log.d("Error", "Error " + t.getMessage());
                        }
                    });
                } else {
                    Toast.makeText(MapDriverBookingActivity.this, "No se pudo enviar la notificacion porque el bombero no tiene un token de sesion", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void removeLocation() {
        if (locationListenerGPS != null) {
            mLocationManager.removeUpdates(locationListenerGPS);
        }
    }

}