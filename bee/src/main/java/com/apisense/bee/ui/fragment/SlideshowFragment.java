package com.apisense.bee.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import com.apisense.bee.R;

import java.util.Random;

public class SlideshowFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_slideshow, container, false);

        Random r = new Random();

        TextView textView = (TextView) rootView.findViewById(R.id.slideShow);
        textView.setText(r.nextInt(10) + " !");

        return rootView;
    }
}
