package com.example.oficialbombero.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.oficialbombero.R;
import com.example.oficialbombero.activities.ChatActivity;
import com.example.oficialbombero.models.Chat;
import com.example.oficialbombero.models.Cliente;
import com.example.oficialbombero.providers.AuthProvider;
import com.example.oficialbombero.providers.ClientProvider;
import com.example.oficialbombero.providers.ConductorProvider;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class ChatsAdapter extends FirestoreRecyclerAdapter<Chat, ChatsAdapter.ViewHolder> {

    Context context;
    AuthProvider authProvider;
    ConductorProvider conductorProvider;
    ClientProvider clientProvider;
    Cliente cliente;
    ValueEventListener mListener;


    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public ChatsAdapter(@NonNull FirestoreRecyclerOptions<Chat> options) {
        super(options);
        authProvider = new AuthProvider();
        conductorProvider = new ConductorProvider();
        cliente = new Cliente();
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Chat chat) {
        String idUser = "";

        for (int i = 0; i < chat.getIds().size(); i++) {
            if (!authProvider.getId().equals(chat.getIds().get(i))) {
                idUser = chat.getIds().get(i);
                break;
            }
        }

        getUserInfo(holder, idUser);


    }

    private void getUserInfo(final ViewHolder holder, String idUser) {

        mListener = clientProvider.getClient(idUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot != null) {
                    if (dataSnapshot.exists()) {
                        cliente = dataSnapshot.getValue(Cliente.class);
                        holder.textViewUsername.setText(cliente.getName());
                        if (cliente.getImage() != null) {
                            if (!cliente.getImage().equals("")) {
                                Picasso.with(context).load(cliente.getImage()).into(holder.circleImageUser);
                            }
                            else {
                                holder.circleImageUser.setImageResource(R.drawable.ic_person);
                            }
                        }
                        else {
                            holder.circleImageUser.setImageResource(R.drawable.ic_person);
                        }
                    }
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    public ValueEventListener getmListener(){
        return mListener;
    }

    private void goToChatActivity(String idChat, String idUser) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("idUser", idUser);
        intent.putExtra("idChat", idChat);
        context.startActivity(intent);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_chats, parent, false);
        return new ViewHolder(view);
    }




    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewUsername;
        TextView textViewLastMessage;
        TextView textViewTimestamp;
        CircleImageView circleImageUser;
        ImageView imageViewCheck;

        View myView;


        public ViewHolder(View view) {
            super(view);
            myView = view;
            textViewUsername = view.findViewById(R.id.textViewUsername);
            textViewLastMessage = view.findViewById(R.id.textViewLastMessage);
            textViewTimestamp = view.findViewById(R.id.textViewTimestamp);
            circleImageUser = view.findViewById(R.id.circleImageUser);
            imageViewCheck = view.findViewById(R.id.imageViewCheck);
        }
    }
}
