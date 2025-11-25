package com.jargo.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.jargo.app.R;

/**
 * WelcomeFragment - Màn hình chào mừng đầu tiên
 * Hiển thị logo và giới thiệu về JARGO
 */
public class WelcomeFragment extends Fragment {

    public WelcomeFragment() {
        // Required empty public constructor
    }

    public static WelcomeFragment newInstance() {
        return new WelcomeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_welcome, container, false);
    }
}
