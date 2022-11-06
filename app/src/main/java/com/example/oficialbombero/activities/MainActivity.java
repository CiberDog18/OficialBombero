package com.example.oficialbombero.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.oficialbombero.R;
import com.example.oficialbombero.providers.AuthProvider;
import com.hbb20.CountryCodePicker;

public class MainActivity extends AppCompatActivity {


    Button mButtonIrLogin;
    CountryCodePicker mCountryCode;
    EditText mEditTextPhone;

    AuthProvider mAuthProvider;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuthProvider = new AuthProvider();

        mCountryCode = findViewById(R.id.ccp);
        mEditTextPhone = findViewById(R.id.editTextPhone);

        mButtonIrLogin= findViewById(R.id.btnIrLogin);

        mButtonIrLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IrLogin();
            }
        });

    }

    public void IrLogin() {
        String code = mCountryCode.getSelectedCountryCodeWithPlus();
        String phone = mEditTextPhone.getText().toString();

        if (!phone.equals("")) {
            Intent intent = new Intent(MainActivity.this, PhoneAuthActivity.class);
            intent.putExtra("phone", code + phone);
            startActivity(intent);
        }
        else {
            Toast.makeText(this, "Debes ingresar el telefono", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuthProvider.existSession()) {
            Intent intent = new Intent(MainActivity.this, MapDriverActivity.class);
            startActivity(intent);
        }
    }

}