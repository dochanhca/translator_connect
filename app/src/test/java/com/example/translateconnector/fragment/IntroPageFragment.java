package com.example.translateconnector.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.imoktranslator.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by tontn on 3/24/18.
 */

public class IntroPageFragment extends Fragment {
    @BindView(R.id.background)
    ImageView imageView;
    private String urlBackground;

    public static IntroPageFragment newInstance(String urlBackground) {
        IntroPageFragment fragment = new IntroPageFragment();
        Bundle args = new Bundle();
        args.putString("urlBackground", urlBackground);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        urlBackground = getArguments().getString("urlBackground", "url");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_intro_page, container, false);
        ButterKnife.bind(this, view);
        imageView = view.findViewById(R.id.background);
        Glide.with(this).load(urlBackground).into(imageView);
        return view;
    }
}
