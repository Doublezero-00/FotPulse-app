package com.example.fotpulse.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.fotpulse.R;

public class AcademicFragment extends Fragment {

    public AcademicFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_academic, container, false);
        // You can add content specific to Academic section here
        TextView textView = view.findViewById(R.id.fragment_title); // Assuming you add a TextView in fragment_academic.xml
        if (textView != null) {
            textView.setText("Academic Content");
        }
        return view;
    }
}