package com.example.oficialbombero.fragments;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.oficialbombero.R;
import com.example.oficialbombero.activities.ConfirmImageSendActivity;
import com.example.oficialbombero.adapters.CardAdapter;

import java.io.File;


public class ImagePagerFragment extends Fragment {

    View mView;
    CardView mCardViewOptions;
    ImageView mImageViewPicture;
    ImageView mImageViewBack;
    ImageView mImageViewSend;
    LinearLayout mLinearLayoutImagePager;
    EditText mEditTextComment;

    public static Fragment newInstance(int position, String imagePath, int size) {
        ImagePagerFragment fragment = new ImagePagerFragment();
        Bundle args = new Bundle();
        args.putInt("position", position);
        args.putInt("size", size);
        args.putString("image", imagePath);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        mView = inflater.inflate(R.layout.fragment_image_page, container, false);
        mCardViewOptions = mView.findViewById(R.id.cardViewOptions);
        mImageViewPicture = mView.findViewById(R.id.imageViewPicture);
        mImageViewBack = mView.findViewById(R.id.imageViewBack);
        mLinearLayoutImagePager = mView.findViewById(R.id.linearLayoutViewPager);
        mEditTextComment = mView.findViewById(R.id.editTextComment);
        mImageViewSend = mView.findViewById(R.id.imageViewSend);
        mCardViewOptions.setMaxCardElevation(mCardViewOptions.getCardElevation() * CardAdapter.MAX_ELEVATION_FACTOR);

        String imagePath = getArguments().getString("image");
        int size = getArguments().getInt("size");
        final int position = getArguments().getInt("position");

        if (size == 1) {
            mLinearLayoutImagePager.setPadding(0, 0,0 ,0);
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mImageViewBack.getLayoutParams();
            params.leftMargin = 10;
            params.topMargin = 35;
        }

        if (imagePath != null) {
            File file = new File(imagePath);
            mImageViewPicture.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));
        }

        mImageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        mEditTextComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                ((ConfirmImageSendActivity) getActivity()).setMessage(position, charSequence.toString());

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mImageViewSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ConfirmImageSendActivity) getActivity()).send();
            }
        });



        return mView;
    }

    public CardView getCardView() {
        return mCardViewOptions;
    }


}