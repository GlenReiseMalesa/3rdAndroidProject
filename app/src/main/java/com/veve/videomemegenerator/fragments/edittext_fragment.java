package com.veve.videomemegenerator.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.veve.videomemegenerator.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class edittext_fragment extends Fragment {



    public edittext_fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View myV = inflater.inflate(R.layout.fragment_editext, container, false);

        return myV;
    }





}
