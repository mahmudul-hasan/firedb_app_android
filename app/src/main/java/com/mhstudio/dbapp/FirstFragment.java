package com.mhstudio.dbapp;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class FirstFragment extends Fragment {

    private BaseActivity mActivity;

    private Button mFirstButton;

    public interface OnFirstFragmentInteractionListener {
        void onFirstFragmentInteraction();
    }

    private OnFirstFragmentInteractionListener mListener;

    public FirstFragment() {
        // Required empty public constructor
    }

    public static FirstFragment newInstance() {
        FirstFragment fragment = new FirstFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_first, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mFirstButton = (Button) view.findViewById(R.id.btn_first);

        mFirstButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onFirstFragmentInteraction();
                }
            }
        });
    }

    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);
        mActivity = (BaseActivity) context;
        if (context instanceof OnFirstFragmentInteractionListener) {
            mListener = (OnFirstFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroy() {
        Log.i("FRAGDESTROYED", "First Fragment");
        super.onDestroy();
    }
}
