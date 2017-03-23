package loginandregistration;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.mhstudio.dbapp.BaseActivity;
import com.mhstudio.dbapp.R;

import apputils.ConstUtil;

public class LoginFragment extends Fragment {

    private BaseActivity mActivity;

    private EditText mUserEmail, mPassword;
    private Button mSignupButton, mLoginButton;
    private TextView mForgotPass;

    private LinearLayout mLLResetPass;
    private Button mSendButton;

    private ProgressDialog mProgressDialog;

    private FirebaseAuth mFireAuth;

    public interface OnLoginPageListener{
        void onLoginPageSelected(String val);
    }
    private OnLoginPageListener mListener;

    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mProgressDialog = new ProgressDialog(mActivity);

        mFireAuth = FirebaseAuth.getInstance();

        mUserEmail = (EditText) view.findViewById(R.id.login_userid);
        mPassword = (EditText) view.findViewById(R.id.login_password);

        mLoginButton = (Button) view.findViewById(R.id.btn_login);
        mSignupButton = (Button) view.findViewById(R.id.btn_signup);

        mForgotPass = (TextView) view.findViewById(R.id.txt_login_forgotpassword);
        mLLResetPass = (LinearLayout) view.findViewById(R.id.ll_login_reset_pass);
        mSendButton = (Button) view.findViewById(R.id.btn_login_send_reset_pass);

        mSignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //goto the sign up page
                mListener.onLoginPageSelected(ConstUtil.SIGNUP_PAGE_SELECTED);
            }
        });

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mUserEmail.getText().toString().trim();
                String pass = mPassword.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    Toast.makeText(mActivity, "Email cannot be empty", Toast.LENGTH_LONG).show();
                    return;
                }
                if(TextUtils.isEmpty(pass)){
                    Toast.makeText(mActivity, "Password cannot be empty", Toast.LENGTH_LONG).show();
                    return;
                }

                mProgressDialog.setMessage("Signing in ...");
                mProgressDialog.show();

                mFireAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(mActivity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        mProgressDialog.dismiss();

                        if(task.isSuccessful()){
                            Toast.makeText(mActivity, "Login successful", Toast.LENGTH_LONG).show();
                            mActivity.gotoPreviousPage();
                        }else{
                            Toast.makeText(mActivity, "Login failed", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        mForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLLResetPass.setVisibility(View.VISIBLE);
            }
        });

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**Send the password reset to the email then disappear*/
                String email = mUserEmail.getText().toString().trim();
                if(TextUtils.isEmpty(email)){
                    Toast.makeText(mActivity, "Email cannot be empty", Toast.LENGTH_LONG).show();
                    return;
                }

                mFireAuth.sendPasswordResetEmail(email);
                mLLResetPass.setVisibility(View.GONE);
            }
        });

        mUserEmail.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    mLoginButton.performClick();
                }
                return false;
            }
        });
        mPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    mLoginButton.performClick();
                }
                return false;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        mActivity.getSupportActionBar().setTitle("Login");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (BaseActivity) context;
        if(context instanceof OnLoginPageListener){
            mListener = (OnLoginPageListener) context;
        }else{
            throw new RuntimeException(context.toString() + " must implement OnLoginPageListener");
        }
    }

    @Override
    public void onDetach() {
        mListener = null;
        super.onDetach();
    }
}
