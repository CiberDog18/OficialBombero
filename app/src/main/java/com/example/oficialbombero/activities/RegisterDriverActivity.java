package com.example.oficialbombero.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.oficialbombero.R;
import com.example.oficialbombero.includes.MyToolbar;
import com.example.oficialbombero.models.Conductor;
import com.example.oficialbombero.providers.AuthProvider;
import com.example.oficialbombero.providers.ConductorProvider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class RegisterDriverActivity extends AppCompatActivity {

    AlertDialog mDialog;
    // views
    AuthProvider mAuthProvider;
    ConductorProvider mConductorProvider;


    private Button mButtonRegister;
    private TextInputEditText mTextInputEmail;
    private TextInputEditText mTextInputName;
    private TextInputEditText mTextInpuDoc;
    private TextInputEditText mTextInputApe;
    private TextInputEditText mTextInputAddress;
    private TextInputEditText mTextInputContrato;

    private String mExtraPhone2;
    private Spinner mSpinnerSex;
    private Spinner mSpinnertCiudad;
    private DatePickerDialog mDatePickerDialog;
    private Button mDateButton;

    private List<String> municipiosSantander = new ArrayList<>();
    private List<String> sexos = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_driver);
        MyToolbar.show(this, "Registrar Bombero", false);


        mAuthProvider = new AuthProvider();
        mConductorProvider = new ConductorProvider();


        mDialog = new SpotsDialog.Builder().setContext(RegisterDriverActivity.this).setMessage("Espere un momento").build();

        mTextInpuDoc = findViewById(R.id.textInpuDoc);
        mButtonRegister = findViewById(R.id.btnResgister);
        mTextInputEmail = findViewById(R.id.textInputEmail2);
        mTextInputName = findViewById(R.id.textInputName2);
        mTextInputApe = findViewById(R.id.textInputApe);
        mTextInputAddress = findViewById(R.id.textInputAddress);
        mTextInputContrato = findViewById(R.id.textInputContrato);

        mSpinnertCiudad = findViewById(R.id.spinnerCiudad);
        mSpinnerSex = findViewById(R.id.spinnerSex);
        mDateButton = findViewById(R.id.datePickerButton);


        initDatePicker();
        mDateButton.setText(getTodaysDate());


        listSpinner();

        mButtonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickRegister();
            }
        });

    }




    //////////////////////////////////////////////////////////////////////////////////////////////////////////
    private String getTodaysDate()
    {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month = month + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return makeDateString(day, month, year);
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = makeDateString(day, month, year);

                mDateButton.setText(date);

            }
        };
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;

        mDatePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);

    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////////
    private String makeDateString(int day, int month, int year)
    {
        return getMonthFormat(month) + " " + day + " " + year;
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////////
    private String getMonthFormat(int month)
    {
        if(month == 1)
            return "JAN";
        if(month == 2)
            return "FEB";
        if(month == 3)
            return "MAR";
        if(month == 4)
            return "APR";
        if(month == 5)
            return "MAY";
        if(month == 6)
            return "JUN";
        if(month == 7)
            return "JUL";
        if(month == 8)
            return "AUG";
        if(month == 9)
            return "SEP";
        if(month == 10)
            return "OCT";
        if(month == 11)
            return "NOV";
        if(month == 12)
            return "DEC";

        //default should never happen
        return "JAN";
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void openDatePicker(View view)
    {
        mDatePickerDialog.show();
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void listSpinner() {

        sexos.add(0, "Sexo");
        sexos.add("Hombre");
        sexos.add("Mujer");

        ArrayAdapter<String> arrayAdapterSex = new ArrayAdapter(this, android.R.layout.simple_list_item_1, sexos);
        arrayAdapterSex.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerSex.setAdapter(arrayAdapterSex);

        municipiosSantander.add(0, "Seleccionar municipio");
        municipiosSantander.add("Paramo");
        municipiosSantander.add("Socorro");
        municipiosSantander.add("Valle de San Jose");
        municipiosSantander.add("Curiti");
        municipiosSantander.add("Villa nueva");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, municipiosSantander);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnertCiudad.setAdapter(arrayAdapter);
    }


    void clickRegister() {
        final String name = mTextInputName.getText().toString();
        final String ape = mTextInputApe.getText().toString();
        final String doc = mTextInpuDoc.getText().toString();
        final String email = mTextInputEmail.getText().toString();
        final String address = mTextInputAddress.getText().toString();
        final String sex = mSpinnerSex.getSelectedItem().toString();
        final String ciuidad = mSpinnertCiudad.getSelectedItem().toString();
        final String date = mDateButton.getText().toString();
        final String contrato = mTextInputContrato.getText().toString();

        if (!ciuidad.equals("Seleccionar municipio") && !sex.equals("Sexo")) {
            if (!doc.isEmpty() && !name.isEmpty() && !ape.isEmpty() && !address.isEmpty() && !email.isEmpty() && !sex.isEmpty() && !ciuidad.isEmpty() && !date.isEmpty() && !contrato.isEmpty()) {
                mDialog.show();
                Conductor conductor = new Conductor();
                conductor.setId(mAuthProvider.getId());
                conductor.setName(name);
                conductor.setCedula(doc);
                conductor.setApe(ape);
                conductor.setAddress(address);
                conductor.setEmail(email);
                conductor.setCiuidad(ciuidad);
                conductor.setDateborn(date);
                conductor.setContrato(contrato);
                update(conductor);
                //register(name, email, password, vehiculoMarca, vehiculoPlaca);

            }

        }


    }

    void register(final String name, final String email, String password, final String vehiculoMarca, final String vehiculoPlaca) {
        mAuthProvider.register(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                mDialog.hide();
                if (task.isSuccessful()) {
                    String id = FirebaseAuth.getInstance().getCurrentUser().getUid();


                } else {
                    Toast.makeText(RegisterDriverActivity.this, "No se pudo registrar el usuario", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    void update(Conductor conductor) {
        mConductorProvider.updateRegister(conductor).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                    Toast.makeText(RegisterDriverActivity.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterDriverActivity.this, MapDriverActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                } else {
                    Toast.makeText(RegisterDriverActivity.this, "No se puedo crear el conductor", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}