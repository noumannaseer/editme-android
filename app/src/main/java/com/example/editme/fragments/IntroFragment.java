package com.example.editme.fragments;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 */
//******************************************************************
public class IntroFragment
        extends Fragment
//******************************************************************
{
    private int position = -1;

    private static final String ARG_LAYOUT_RES_ID = "layoutResId";
    private int layoutResId;
    private SharedPreferences preferences;

    //******************************************************************
    public static IntroFragment newInstance(int layoutResId, int position)
    //******************************************************************
    {
        IntroFragment sampleSlide = new IntroFragment();
        sampleSlide.setPosition(position);

        Bundle args = new Bundle();
        args.putInt(ARG_LAYOUT_RES_ID, layoutResId);
        sampleSlide.setArguments(args);

        return sampleSlide;
    }

    //************************************************************************
    public int getPosition()
    //************************************************************************
    {
        return position;
    }

    //************************************************************************
    public void setPosition(int position)
    //************************************************************************
    {
        this.position = position;
    }


    //******************************************************************
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    //******************************************************************
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey(ARG_LAYOUT_RES_ID))
        {
            layoutResId = getArguments().getInt(ARG_LAYOUT_RES_ID);
        }
    }


    //******************************************************************
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    //******************************************************************
    {
        View view = inflater.inflate(layoutResId, container, false);
        return view;
    }

}
