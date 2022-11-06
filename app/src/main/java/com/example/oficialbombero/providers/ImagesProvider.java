package com.example.oficialbombero.providers;

import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.oficialbombero.models.Message;
import com.example.oficialbombero.utils.CompressorBitmapImage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;

public class ImagesProvider {
    private StorageReference mStorage;
    FirebaseStorage mFirebaseStorage;
    int index = 0;
    MessagesProvider mMessagesProvider;

    public ImagesProvider(String ref) {
        mFirebaseStorage = FirebaseStorage.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference().child(ref);
        mMessagesProvider = new MessagesProvider();
    }

    public UploadTask saveImage(Context context, File image, String idUser) {
        byte[] imageByte = CompressorBitmapImage.getImage(context, image.getPath(), 500, 500);
        final StorageReference storage = mStorage.child(idUser + ".jpg");
        mStorage = storage;
        UploadTask uploadTask = storage.putBytes(imageByte);
        return uploadTask;
    }

    public void uploadMultiple(final Context context, final ArrayList<Message> messages) {

        Uri[] uri = new Uri[messages.size()];
        for (int i = 0; i < messages.size(); i++) {
            File file = CompressorBitmapImage.reduceImageSize(new File(messages.get(i).getUrl()));

            uri[i] = Uri.parse("file://" + file.getPath());
            final StorageReference ref = mStorage.child(uri[i].getLastPathSegment());
            ref.putFile(uri[i]).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                    if (task.isSuccessful()) {
                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String url = uri.toString();
                                messages.get(index).setUrl(url);
                                mMessagesProvider.create(messages.get(index));
                                index++;
                            }
                        });
                    }
                    else {
                        Toast.makeText(context, "Hubo un error al almacenar la imagen", Toast.LENGTH_SHORT).show();
                    }


                }
            });
        }

    }

    public Task<Uri> getDownloadUri() {
        return mStorage.getDownloadUrl();
    }

    public Task<Void> delete(String url) {
        return mFirebaseStorage.getReferenceFromUrl(url).delete();
    }

    public StorageReference getStorage() {
        return mStorage;
    }
}
