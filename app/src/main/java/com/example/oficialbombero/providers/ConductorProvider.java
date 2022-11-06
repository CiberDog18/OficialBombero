package com.example.oficialbombero.providers;


import com.example.oficialbombero.models.Conductor;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.HashMap;
import java.util.Map;

public class ConductorProvider {

    DatabaseReference mDatabase;

    public ConductorProvider(){
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("bombero");
    }

    public Task<Void> create(Conductor conductor){
        return mDatabase.child(conductor.getId()).setValue(conductor);
    }

    public void createToken(final String idUser) {
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String token = instanceIdResult.getToken();
                Map<String, Object> map = new HashMap<>();

                map.put("token", token);
                mDatabase.child(idUser).updateChildren(map);
            }
        });
    }

    public DatabaseReference getDriver(String idDriver) {
        return mDatabase.child(idDriver);
    }

    public Task<Void> update(Conductor conductor) {
        Map<String, Object> map = new HashMap<>();
        map.put("direccion", conductor.getAddress());
        map.put("email", conductor.getEmail());
        map.put("image", conductor.getImage());
        map.put("contrato", conductor.getContrato());
        return mDatabase.child(conductor.getId()).updateChildren(map);
    }

    public Task<Void> updateRegister(Conductor conductor) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", conductor.getName());
        map.put("cedula", conductor.getCedula());
        map.put("apellido", conductor.getApe());
        map.put("direccion", conductor.getAddress());
        map.put("email", conductor.getEmail());
        map.put("sexo", conductor.getSexo());
        map.put("image", conductor.getImage());
        map.put("Nacimiento", conductor.getDateborn());
        map.put("contrato", conductor.getContrato());
        return mDatabase.child(conductor.getId()).updateChildren(map);
    }



    public Task<Void> updateOnline(String idUser, boolean status) {
        Map<String, Object> map = new HashMap<>();
        map.put("online", status);
        return mDatabase.child(idUser).updateChildren(map);
    }
}
