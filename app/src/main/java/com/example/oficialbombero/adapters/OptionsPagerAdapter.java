package com.example.oficialbombero.adapters;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.oficialbombero.fragments.ImagePagerFragment;

import java.util.ArrayList;
import java.util.List;

public class OptionsPagerAdapter  extends FragmentStatePagerAdapter implements CardAdapter {

    private List<ImagePagerFragment> fragments;
    private ArrayList<String> data;
    private float baseElevation;

    public OptionsPagerAdapter(Context context, FragmentManager fm , float baseElevation, ArrayList<String> data) {
        super(fm);
        fragments = new ArrayList<>();
        this.baseElevation = baseElevation;
        this.data = data;

        // AÑADIR VIEWS
        for(int i = 0; i < data.size(); i++){
            addCardFragment(new ImagePagerFragment());
        }

    }

    @Override
    public float getBaseElevation() {
        return baseElevation;
    }


    @Override
    public CardView getCardViewAt(int position) {
        return fragments.get(position).getCardView();
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public Fragment getItem(int position) {
        return ImagePagerFragment.newInstance(position, data.get(position), data.size());
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        int index = fragments.indexOf (object);

        if (index == -1)
            return POSITION_NONE;
        else
            return index;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Object fragment = super.instantiateItem(container, position);
        fragments.set(position, (ImagePagerFragment) fragment);
        return fragment;
    }

    public void addCardFragment(ImagePagerFragment fragment) {
        fragments.add(fragment);
    }

}