package com.example.oficialbombero.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.oficialbombero.R;
import com.example.oficialbombero.models.Conductor;
import com.example.oficialbombero.providers.AuthProvider;
import com.example.oficialbombero.providers.ConductorProvider;
import com.example.oficialbombero.providers.ImagesProvider;
import com.example.oficialbombero.utils.FileUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UpdateProfileDriverActivity extends AppCompatActivity {

    private ImageView mImageViewProfile;
    private Button mButtonUpdate;


    private TextView mTextViewAddress2;
    private TextView mTextViewEmail2;
    Spinner mSpinnertCity;
    TextInputEditText mTextInputContrato2;
    private CircleImageView mCircleImageBack;

    private ConductorProvider mConductorProvider;
    private AuthProvider mAuthProvider;
    private ImagesProvider mImageProvider;

    private File mImageFile;
    private String mImage;

    private final int GALLERY_REQUEST = 1;
    private ProgressDialog mProgressDialog;

    private String mName;
    private String mEmail;
    private String mContrato;
    private String mAddress;
    private String ciuidad;

    List<String> municipiosSantander = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile_driver);

        mImageViewProfile = findViewById(R.id.imageViewProfile);
        mButtonUpdate = findViewById(R.id.btnUpdateProfile);
        mTextViewEmail2 = findViewById(R.id.textInputEmailE);
        mTextViewAddress2 = findViewById(R.id.textInputDireccion);
        mSpinnertCity = findViewById(R.id.spinnerCity);
        mTextInputContrato2 = findViewById(R.id.textInputContrato2);
        mCircleImageBack = findViewById(R.id.circleImageBack);

        mConductorProvider = new ConductorProvider();
        mAuthProvider = new AuthProvider();
        mImageProvider = new ImagesProvider("bombero_images");

        mProgressDialog = new ProgressDialog(this);
        listSpinner();

        getDriverInfo();

        mImageViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        mButtonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfile();
            }
        });

        mCircleImageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
    private void listSpinner() {
        municipiosSantander.add(0, "Seleccionar municipio");
        municipiosSantander.add("Paramo");
        municipiosSantander.add("Socorro");
        municipiosSantander.add("Valle de San Jose");
        municipiosSantander.add("Curiti");
        municipiosSantander.add("Villa nueva");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, municipiosSantander);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnertCity.setAdapter(arrayAdapter);



    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GALLERY_REQUEST );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode== GALLERY_REQUEST && resultCode == RESULT_OK) {
            try {
                mImageFile = FileUtil.from(this, data.getData());
                mImageViewProfile.setImageBitmap(BitmapFactory.decodeFile(mImageFile.getAbsolutePath()));
            } catch(Exception e) {
                Log.d("ERROR", "Mensaje: " +e.getMessage());
            }
        }
    }

    private void getDriverInfo() {
        mConductorProvider.getDriver(mAuthProvider.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String email = dataSnapshot.child("email").getValue().toString();
                    String address = dataSnapshot.child("direccion").getValue().toString();
                    String contrato = dataSnapshot.child("contrato").getValue().toString();
                    String image = "";
                    if (dataSnapshot.hasChild("image")) {
                        image = dataSnapshot.child("image").getValue().toString();
                        Picasso.with(UpdateProfileDriverActivity.this).load(image).into(mImageViewProfile);
                    }

                    mTextInputContrato2.setText(contrato);
                    mTextViewEmail2.setText(email);
                    mTextViewAddress2.setText(address);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateProfile() {
        mContrato = mTextInputContrato2.getText().toString();
        mEmail = mTextViewEmail2.getText().toString();
        mAddress = mTextViewAddress2.getText().toString();
        ciuidad = mSpinnertCity.getSelectedItem().toString();


        if (!mEmail.equals("") && !mContrato.equals("") && !mAddress.equals("") && !ciuidad.equals("Seleccionar municipio") && mImageFile != null) {
            mProgressDialog.setMessage("Espere un momento...");
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.show();

            saveImage();
        }
        else {
            Toast.makeText(this, "Ingresa la imagen y el nombre", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveImage() {
        mImageProvider.saveImage(UpdateProfileDriverActivity.this, mImageFile, mAuthProvider.getId()).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String image = uri.toString();
                            Conductor conductor = new Conductor();
                            conductor.setImage(image);
                            conductor.setEmail(mEmail);
                            conductor.setAddress(mAddress);
                            conductor.setCiuidad(ciuidad);
                            conductor.setContrato(mContrato);
                            conductor.setId(mAuthProvider.getId());
                            mConductorProvider.update(conductor).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mProgressDialog.dismiss();
                                    Toast.makeText(UpdateProfileDriverActivity.this, "Su informacion se actualizo correctamente", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }
                else {
                    Toast.makeText(UpdateProfileDriverActivity.this, "Hubo un error al subir la imagen", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}