package com.mhstudio.dbapp;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import apputils.ConstUtil;

public class HomeFragment extends Fragment {

    private BaseActivity mActivity;

    private Button leButton;
    private Button mListSpotButton;
    private Button mLookSpotButton;

    private TextView mWelcome;
    private Button mLogout;

    private FirebaseAuth mFireAuth;

    public interface OnHomeFragmentInteractionListener {
        void onHomeFragmentInteraction(String val);
    }

    private OnHomeFragmentInteractionListener mListener;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        mActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        mActivity.getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mFireAuth = FirebaseAuth.getInstance();

        mWelcome = (TextView) view.findViewById(R.id.home_welcome_user);
        mLogout = (Button) view.findViewById(R.id.logout);

        mListSpotButton = (Button) view.findViewById(R.id.btn_home_listspot);
        mLookSpotButton = (Button) view.findViewById(R.id.btn_home_lookspot);

        mListSpotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null){
                    /**
                     * check if the user is authenticated
                     * if not, bring up the Login page
                     * */

                    if(mFireAuth.getCurrentUser() != null){
                        mListener.onHomeFragmentInteraction(ConstUtil.LIST_SPOT_SELECTED);
                    }else {
                        mListener.onHomeFragmentInteraction(ConstUtil.LOGIN_PAGE_SELECTED);
                    }
//                    mListener.onHomeFragmentInteraction(ConstUtil.LOGIN_PAGE_SELECTED);
//
                }
            }
        });

        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mFireAuth.getCurrentUser() != null){
                    mFireAuth.signOut();
                    mWelcome.setText(mFireAuth.getCurrentUser()==null ? "null" : mFireAuth.getCurrentUser().getDisplayName());
                }
            }
        });

//        leButton = (Button) view.findViewById(R.id.btn_home);
//
//        leButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mListener != null) {
//                    mListener.onHomeFragmentInteraction();
//                }
//            }
//        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (BaseActivity) activity;
        if (activity instanceof OnHomeFragmentInteractionListener) {
            mListener = (OnHomeFragmentInteractionListener) activity;
        } else {
            throw new RuntimeException(activity.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();

        mActivity.getSupportActionBar().setTitle("Parkly");
        mActivity.getSupportActionBar().hide();

        if(mFireAuth.getCurrentUser() != null){
            FirebaseUser user = mFireAuth.getCurrentUser();
            mWelcome.setText(user.getDisplayName());
        }
    }

    @Override
    public void onPause() {
        mActivity.getSupportActionBar().show();
        super.onPause();
    }
}
