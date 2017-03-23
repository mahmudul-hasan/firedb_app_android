package com.mhstudio.dbapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import apputils.ConstUtil;
import listparkingspot.ListSpotDescriptionFragment;
import loginandregistration.LoginFragment;
import loginandregistration.SignupFragment;

public class BaseActivity extends AppCompatActivity implements HomeFragment.OnHomeFragmentInteractionListener,
        FirstFragment.OnFirstFragmentInteractionListener, LoginFragment.OnLoginPageListener {

    private HomeFragment mHomeFragment;
    private FirstFragment mFirstFragment;
    private SecondFragment mSecondFragment;
    private ListSpotDescriptionFragment mListSpotDescFragment;
    private LoginFragment mLoginFragment;
    private SignupFragment mSignupFragment;

    private FragmentManager mFragMan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        mHomeFragment = HomeFragment.newInstance();
        mFirstFragment = FirstFragment.newInstance();
        mSecondFragment = SecondFragment.newInstance();
//        mListSpotDescFragment = ListSpotDescriptionFragment.newInstance();

        startFragment(mHomeFragment);

        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                Log.i("BACKENTRYCOUNT", ""+getSupportFragmentManager().getBackStackEntryCount());
            }
        });
    }

    private void startFragment(Fragment fragment){
        mFragMan = getSupportFragmentManager();
        FragmentTransaction fragTrans = mFragMan.beginTransaction();
        if (fragment != null){
            fragTrans.replace(R.id.fragment_container, fragment);
            if(!(fragment instanceof HomeFragment)){
                fragTrans.addToBackStack(fragment.getTag());
            }
            fragTrans.commit();
        }
    }

    @Override
    public void onHomeFragmentInteraction(String val) {
        routeFragment(val);
    }

    @Override
    public void onLoginPageSelected(String val) {
        routeFragment(val);
    }

    @Override
    public void onFirstFragmentInteraction() {
        startFragment(mSecondFragment);
    }

    private void routeFragment(String val){
        switch (val){
            case ConstUtil.LIST_SPOT_SELECTED:
                if(mListSpotDescFragment == null){
                    mListSpotDescFragment = ListSpotDescriptionFragment.newInstance();
                }
                startFragment(mListSpotDescFragment);
                break;

            case ConstUtil.LOGIN_PAGE_SELECTED:
                if(mLoginFragment == null){
                    mLoginFragment = LoginFragment.newInstance();
                }
                startFragment(mLoginFragment);
                break;

            case ConstUtil.SIGNUP_PAGE_SELECTED:
                if(mSignupFragment == null){
                    mSignupFragment = SignupFragment.newInstance();
                }
                startFragment(mSignupFragment);
        }
    }

    public void gotoPreviousPage(){
        getSupportFragmentManager().popBackStack();
    }
}
