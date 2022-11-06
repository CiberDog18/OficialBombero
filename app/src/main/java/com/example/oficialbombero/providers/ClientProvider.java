package com.example.oficialbombero.providers;

import com.example.oficialbombero.models.Cliente;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.HashMap;
import java.util.Map;

public class ClientProvider {

    DatabaseReference mDatabase;

    public ClientProvider(){
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("usuarios");
    }

    public Task<Void> create(Cliente cliente){
        Map<String, Object> map = new HashMap<>();
        map.put("cedula", cliente.getCedula());
        map.put("name", cliente.getName());
        map.put("apellido", cliente.getApe());
        map.put("direccion", cliente.getAddress());
        map.put("email", cliente.getEmail());
        map.put("sexo", cliente.getSexo());
        map.put("ciuidad", cliente.getCiuidad());
        map.put("Nacimiento", cliente.getDateborn());
        return mDatabase.child(cliente.getId()).setValue(map);
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

    public Task<Void> update(Cliente cliente) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", cliente.getName());
        map.put("image", cliente.getImage());
        return mDatabase.child(cliente.getId()).updateChildren(map);
    }

    public DatabaseReference getClient(String idClient) {
        return mDatabase.child(idClient);
    }

    public Task<Void> updateOnline(Cliente cliente, boolean status) {
        Map<String, Object> map = new HashMap<>();
        map.put("online", status);
        return mDatabase.child(cliente.getId()).updateChildren(map);
    }

}
