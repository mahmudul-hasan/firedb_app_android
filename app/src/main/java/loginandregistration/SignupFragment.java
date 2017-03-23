package loginandregistration;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mhstudio.dbapp.BaseActivity;
import com.mhstudio.dbapp.R;

public class SignupFragment extends Fragment {

    private BaseActivity mActivity;

    private EditText mFullName, mEmail, mPassword, mVerifyPass;
    private Button mRegister, mGoBack;

    private ProgressDialog mProgressDialog;

    private FirebaseAuth mFireAuth;
    private DatabaseReference mDBRef;

    public SignupFragment() {
        // Required empty public constructor
    }

    public static SignupFragment newInstance() {
        SignupFragment fragment = new SignupFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View roodView = inflater.inflate(R.layout.fragment_signup, container, false);
        return roodView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mProgressDialog = new ProgressDialog(mActivity);

        mFireAuth = FirebaseAuth.getInstance();
        mDBRef = FirebaseDatabase.getInstance().getReference();

        mFullName = (EditText) view.findViewById(R.id.et_signup_fullname);
        mEmail = (EditText) view.findViewById(R.id.et_signup_email);
        mPassword = (EditText) view.findViewById(R.id.et_signup_password);
        mVerifyPass = (EditText) view.findViewById(R.id.et_signup_verifypass);

        mRegister = (Button) view.findViewById(R.id.btn_signup_register);
        mGoBack = (Button) view.findViewById(R.id.btn_signup_goback);

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pass = mPassword.getText().toString().trim();
                String verifyPass = mVerifyPass.getText().toString().trim();
                final String email = mEmail.getText().toString().trim();
                final String fullname = mFullName.getText().toString().trim();

                if(TextUtils.isEmpty(fullname)){
                    Toast.makeText(mActivity, "Fullname cannot be empty", Toast.LENGTH_LONG).show();
                    return;
                }
                if(TextUtils.isEmpty(email)){
                    Toast.makeText(mActivity, "Email cannot be empty", Toast.LENGTH_LONG).show();
                    return;
                }
                if(TextUtils.isEmpty(pass)){
                    Toast.makeText(mActivity, "Password cannot be empty", Toast.LENGTH_LONG).show();
                    return;
                }
                if(TextUtils.isEmpty(verifyPass)){
                    Toast.makeText(mActivity, "Verify Password cannot be empty", Toast.LENGTH_LONG).show();
                    return;
                }

                if(!pass.equals(verifyPass)){
                    Toast.makeText(mActivity, "Password and Verify Password did not match", Toast.LENGTH_LONG).show();
                    return;
                }

                mProgressDialog.setMessage("Signing up user ...");
                mProgressDialog.show();

                mFireAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(mActivity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        mProgressDialog.dismiss();
                        if(task.isSuccessful()){
                            FirebaseUser leUser = mFireAuth.getCurrentUser();
                            if(leUser != null){
                                SignupPojo signedupUser = new SignupPojo(fullname, email, leUser.getUid());

                                mDBRef.child("users").child(leUser.getUid()).child("userinfo").setValue(signedupUser);

                                Task<Void> updateTask = leUser.updateProfile(new UserProfileChangeRequest.Builder().setDisplayName(fullname).build());
                                updateTask.addOnCompleteListener(mActivity, new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            //profile update successful
                                        }else{
                                            //mayday mayday
                                        }
                                    }
                                });
                            }

                            Toast.makeText(mActivity, "Signup successful", Toast.LENGTH_LONG).show();

                            //now go back to the previous fragment which is login page to log in
                            mActivity.gotoPreviousPage();
                        }else{
                            Toast.makeText(mActivity, "Failed to sign up", Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
        });

        mGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.gotoPreviousPage();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mActivity.getSupportActionBar().setTitle("Sign Up");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (BaseActivity) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
