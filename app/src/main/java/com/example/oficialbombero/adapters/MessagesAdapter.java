package com.example.oficialbombero.adapters;

import android.app.DownloadManager;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.oficialbombero.R;
import com.example.oficialbombero.models.Conductor;
import com.example.oficialbombero.models.Message;
import com.example.oficialbombero.providers.AuthProvider;
import com.example.oficialbombero.providers.ClientProvider;
import com.example.oficialbombero.providers.ConductorProvider;
import com.example.oficialbombero.utils.RelativeTime;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.ListenerRegistration;
import com.squareup.picasso.Picasso;

import java.io.File;

public class MessagesAdapter extends FirestoreRecyclerAdapter<Message, MessagesAdapter.ViewHolder> {

    Context context;
    AuthProvider authProvider;
    ConductorProvider conductorProvider;
    ClientProvider clientProvider;
    Conductor conductor;
    ListenerRegistration listener;



    public MessagesAdapter(FirestoreRecyclerOptions options, Context context) {
        super(options);
        this.context = context;
        authProvider = new AuthProvider();
        conductorProvider = new ConductorProvider();
        conductor = new Conductor();
    }
    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull final Message message) {

        holder.textViewMessage.setText(message.getMessage());
        holder.textViewDate.setText(RelativeTime.timeFormatAMPM(message.getTimestamp(), context));

        if (message.getIdSender().equals(authProvider.getId())) {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            params.setMargins(100, 0, 0, 0);
            holder.linearLayoutMessage.setLayoutParams(params);
            holder.linearLayoutMessage.setPadding(30, 20, 30, 20);
            holder.linearLayoutMessage.setBackground(context.getDrawable(R.drawable.bubble_corner_right));
            holder.textViewMessage.setTextColor(Color.BLACK);
            holder.textViewDate.setTextColor(Color.DKGRAY);
            holder.imageViewCheck.setVisibility(View.VISIBLE);

            if (message.getStatus().equals("ENVIADO")) {
                holder.imageViewCheck.setImageResource(R.drawable.icon_double_check_gray);
            }
            else if (message.getStatus().equals("VISTO")) {
                holder.imageViewCheck.setImageResource(R.drawable.icon_double_check_blue);
            }

        }
        else {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            params.setMargins(0, 0, 100, 0);
            holder.linearLayoutMessage.setLayoutParams(params);
            holder.linearLayoutMessage.setPadding(50, 20, 30, 20);
            holder.linearLayoutMessage.setBackground(context.getDrawable(R.drawable.bubble_corner_left));
            holder.textViewMessage.setTextColor(Color.BLACK);
            holder.textViewDate.setTextColor(Color.DKGRAY);
            holder.imageViewCheck.setVisibility(View.GONE);
            ViewGroup.MarginLayoutParams marginDate = (ViewGroup.MarginLayoutParams) holder.textViewDate.getLayoutParams();
            marginDate.rightMargin = 20;
        }

        showImage(holder, message);
        showDocument(holder, message);
        openMessage(holder, message);

    }

    private void openMessage(ViewHolder holder, final Message message) {
        holder.myView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (message.getType().equals("documento")) {
                    File file = new File(context.getExternalFilesDir(null), "file");
                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(message.getUrl()))
                            .setTitle(message.getMessage())
                            .setDescription("Download")
                            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                            .setDestinationUri(Uri.fromFile(file))
                            .setAllowedOverMetered(true)
                            .setAllowedOverRoaming(true);
                    DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                    downloadManager.enqueue(request);
                }
            }
        });

    }
    private void showDocument(ViewHolder holder, Message message) {

        if (message.getType().equals("documento")) {
            if (message.getUrl() != null) {
                if (!message.getUrl().equals("")) {
                    holder.linearLayoutDocument.setVisibility(View.VISIBLE);
                }
                else {
                    holder.linearLayoutDocument.setVisibility(View.GONE);
                }
            }
            else {
                holder.linearLayoutDocument.setVisibility(View.GONE);
            }
        }
        else {
            holder.linearLayoutDocument.setVisibility(View.GONE);
        }
    }

    private void showImage(ViewHolder holder, Message message) {

        if (message.getType().equals("imagen")) {
            if (message.getUrl() != null) {
                if (!message.getUrl().equals("")) {
                    holder.imageViewMessage.setVisibility(View.VISIBLE);
                    Picasso.with(context).load(message.getUrl()).into(holder.imageViewMessage);

                    if (message.getMessage().equals("\uD83D\uDCF7imagen")) {
                        holder.textViewMessage.setVisibility(View.GONE);
                        //holder.textViewDate.setPadding(0,0,10,0);
                        ViewGroup.MarginLayoutParams marginDate = (ViewGroup.MarginLayoutParams) holder.textViewDate.getLayoutParams();
                        ViewGroup.MarginLayoutParams marginCheck = (ViewGroup.MarginLayoutParams) holder.imageViewCheck.getLayoutParams();
                        marginDate.topMargin = 15;
                        marginCheck.topMargin = 15;

                    }
                    else {
                        holder.textViewMessage.setVisibility(View.VISIBLE);
                    }
                }
                else {
                    holder.imageViewMessage.setVisibility(View.GONE);
                    holder.textViewMessage.setVisibility(View.VISIBLE);
                }
            }
            else {
                holder.imageViewMessage.setVisibility(View.GONE);
                holder.textViewMessage.setVisibility(View.VISIBLE);
            }
        }
        else {
            holder.imageViewMessage.setVisibility(View.GONE);
            holder.textViewMessage.setVisibility(View.VISIBLE);
        }

    }


    public ListenerRegistration getListener() {
        return listener;
    }

    @NonNull
    @Override
    public MessagesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_message, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewMessage;
        TextView textViewDate;
        ImageView imageViewCheck;
        ImageView imageViewMessage;
        LinearLayout linearLayoutMessage;
        LinearLayout linearLayoutDocument;

        View myView;

        public ViewHolder(View view) {
            super(view);
            myView = view;
            textViewMessage = view.findViewById(R.id.textViewMessage);
            textViewDate = view.findViewById(R.id.textViewDate);
            imageViewCheck = view.findViewById(R.id.imageViewCheck);
            imageViewMessage = view.findViewById(R.id.imageViewMessage);
            linearLayoutMessage = view.findViewById(R.id.linearLayoutMessage);
            linearLayoutDocument = view.findViewById(R.id.linearLayoutDocument);
        }
    }
}
