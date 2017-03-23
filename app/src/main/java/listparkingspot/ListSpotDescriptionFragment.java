package listparkingspot;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mhstudio.dbapp.BaseActivity;
import com.mhstudio.dbapp.R;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import apputils.ConstUtil;

import static android.app.Activity.RESULT_OK;

@SuppressWarnings("WrongConstant")
public class ListSpotDescriptionFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    private BaseActivity mActivity;

    private GoogleApiClient mGAPIClient;

    private TextView mSpotAddressET;
    private ImageView mIconAddress;
    private Button mSubmit;
    private AppCompatCheckBox mCheckGated, mCheckShaded, mCheckHandicap;
    private TextView mFromTime, mToTime, mListingDate;

    private String valGated= ConstUtil.VALUE_NO, valShaded=ConstUtil.VALUE_NO, valHandicap=ConstUtil.VALUE_NO;

    private Geocoder gcoder;
    private FirebaseAuth mFireAuth;
    private DatabaseReference mDBRef;

    private SpotPojo mSpot;

    public ListSpotDescriptionFragment() {
        // Required empty public constructor
    }

    public static ListSpotDescriptionFragment newInstance() {
        ListSpotDescriptionFragment fragment = new ListSpotDescriptionFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGAPIClient = new GoogleApiClient
                .Builder(mActivity)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i("GAPICONNECT", "connected");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i("GAPICONNECT", "connection failed");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("GAPICONNECT", "disconnected");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list_spot_description, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mFireAuth = FirebaseAuth.getInstance();
        mDBRef = FirebaseDatabase.getInstance().getReference();

        mSpot = new SpotPojo();
        mSpot.setUserUid(mFireAuth.getCurrentUser().getUid());

        mSpotAddressET = (TextView) view.findViewById(R.id.tv_spot_address);

        mCheckGated = (AppCompatCheckBox) view.findViewById(R.id.check_spot_gated);
        mCheckShaded = (AppCompatCheckBox) view.findViewById(R.id.check_spot_shaded);
        mCheckHandicap = (AppCompatCheckBox) view.findViewById(R.id.check_spot_handicap);

        mFromTime = (TextView) view.findViewById(R.id.tv_spot_fromtime);
        mToTime = (TextView) view.findViewById(R.id.tv_spot_totime);
        mListingDate = (TextView) view.findViewById(R.id.tv_spot_date);

        mSubmit = (Button) view.findViewById(R.id.btn_spot_submit);

//        mSpotImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                displayPlacePicker();
//            }
//        });

        mSpotAddressET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gcoder = new Geocoder(mActivity, Locale.getDefault());
                LayoutInflater infl = LayoutInflater.from(mActivity);
                final View leInflater = infl.inflate(R.layout.dialog_address, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                builder.setView(leInflater);
                builder.setTitle("Location Address");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText street = (EditText) leInflater.findViewById(R.id.et_spot_street);
                        EditText city = (EditText) leInflater.findViewById(R.id.et_spot_city);
                        EditText state = (EditText) leInflater.findViewById(R.id.et_spot_state);
                        EditText zip = (EditText) leInflater.findViewById(R.id.et_spot_zip);

                        String strStreet = street.getText().toString().trim();
                        String strCity = city.getText().toString().trim();
                        String strState = state.getText().toString().trim();
                        String strZip = zip.getText().toString().trim();
                        String locName = strStreet+", "+strCity+", "+strState+", "+strZip;

                        try {
                            List<Address> addresses = gcoder.getFromLocationName(locName, 1);
                            String addrstreet = addresses.get(0).getAddressLine(0);
                            String addrcity = addresses.get(0).getLocality();
                            String addrstate = addresses.get(0).getAdminArea();
                            String addrzip = addresses.get(0).getPostalCode();
                            String lat = String.valueOf(addresses.get(0).getLatitude());
                            String lng = String.valueOf(addresses.get(0).getLongitude());

                            mSpot.setStreet(addrstreet);
                            mSpot.setCity(addrcity);
                            mSpot.setState(addrstate);
                            mSpot.setZip(addrzip);
                            mSpot.setLat(lat);
                            mSpot.setLng(lng);

                            mSpotAddressET.setText(addrstreet+", "+addrcity+", "+addrstate+" "+addrzip);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
            }
        });

        mCheckGated.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    valGated = ConstUtil.VALUE_YES;
                }else{
                    valGated = ConstUtil.VALUE_NO;
                }
                mSpot.setIsGated(valGated);
            }
        });
        mCheckShaded.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    valShaded = ConstUtil.VALUE_YES;
                }else{
                    valShaded = ConstUtil.VALUE_NO;
                }
                mSpot.setIsShaded(valShaded);
            }
        });
        mCheckHandicap.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    valHandicap = ConstUtil.VALUE_YES;
                }else{
                    valHandicap = ConstUtil.VALUE_NO;
                }
                mSpot.setIsHandicap(valHandicap);
            }
        });

        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseUser leUser = mFireAuth.getCurrentUser();
                if(leUser != null){
                    mDBRef.child("spotlistings").push().setValue(mSpot);
                }
            }
        });

        mFromTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickTime(mFromTime);
            }
        });
        mToTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickTime(mToTime);
            }
        });
        mListingDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickTime(mListingDate);
            }
        });
    }

    private void pickTime(final TextView textView) {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        if(textView.getId() == R.id.tv_spot_date){
            DatePickerDialog dpDialog = new DatePickerDialog(mActivity, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    String month = "";
                    String day = "";
                    switch (monthOfYear){
                        case Calendar.JANUARY:
                            month = "January";
                            break;
                        case Calendar.FEBRUARY:
                            month = "February";
                            break;
                        case Calendar.MARCH:
                            month = "March";
                            break;
                        case Calendar.APRIL:
                            month = "April";
                            break;
                        case Calendar.MAY:
                            month = "May";
                            break;
                        case Calendar.JUNE:
                            month = "June";
                            break;
                        case Calendar.JULY:
                            month = "July";
                            break;
                        case Calendar.AUGUST:
                            month = "August";
                            break;
                        case Calendar.SEPTEMBER:
                            month = "September";
                            break;
                        case Calendar.OCTOBER:
                            month = "October";
                            break;
                        case Calendar.NOVEMBER:
                            month = "November";
                            break;
                        case Calendar.DECEMBER:
                            month = "December";
                            break;
                    }

                    textView.setText(month + " " + dayOfMonth + ", " + year);
                }
            }, year, month, day);
            dpDialog.show();
        }else {
            TimePickerDialog tpDialog = new TimePickerDialog(mActivity, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                    int hour = hourOfDay;
                    String timeSet = "";
                    if (hour > 12) {
                        hour -= 12;
                        timeSet = "PM";
                    } else if (hour == 0) {
                        hour += 12;
                        timeSet = "AM";
                    } else if (hour == 12) {
                        timeSet = "PM";
                    } else {
                        timeSet = "AM";
                    }
                    textView.setText(String.format("%02d:%02d", hour, minute) + timeSet);
                }
            }, hour, minute, false);
            tpDialog.show();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if(mGAPIClient != null){
            mGAPIClient.connect();
        }
    }

    @Override
    public void onStop() {
        if(mGAPIClient != null && mGAPIClient.isConnected()){
            mGAPIClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (BaseActivity) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
        mActivity.getSupportActionBar().setTitle("List Parking Spot");
    }

    @Override
    public void onDestroy() {
        Log.i("FRAGDESTROYED", "ListSpotDescriptionFragment");
        super.onDestroy();
    }

    //ONLY TO CHECK, MAY NOT NEED THIS METHOD
    private void displayPlacePicker() {
        if( mGAPIClient == null || !mGAPIClient.isConnected() )
            return;

//        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        PlaceAutocomplete.IntentBuilder builder = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN);

        try {
            startActivityForResult( builder.build( mActivity ), 127 );
        } catch ( GooglePlayServicesRepairableException e ) {
            Log.d( "PlacesAPI Demo", "GooglePlayServicesRepairableException thrown" );
        } catch ( GooglePlayServicesNotAvailableException e ) {
            Log.d( "PlacesAPI Demo", "GooglePlayServicesNotAvailableException thrown" );
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if( requestCode == 127 && resultCode == RESULT_OK ) {
//            displayPlace( PlacePicker.getPlace( data, this ) );
            Place lePlace = PlaceAutocomplete.getPlace(mActivity, data);

            Geocoder gcoder = new Geocoder(mActivity, Locale.getDefault());
            try {
                List<Address> addresses = gcoder.getFromLocation(lePlace.getLatLng().latitude, lePlace.getLatLng().longitude, 1);

//                mSpotStreetET.setText(addresses.get(0).getAddressLine(0));
//                mSpotCityET.setText(addresses.get(0).getLocality());
//                mSpotStateET.setText(addresses.get(0).getAdminArea());
//                mSpotZipET.setText(addresses.get(0).getPostalCode());

                String street = addresses.get(0).getAddressLine(0);
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String zip = addresses.get(0).getPostalCode();
                String lat = String.valueOf(lePlace.getLatLng().latitude);
                String lng = String.valueOf(lePlace.getLatLng().longitude);

                mSpotAddressET.setText(street+", "+city+", "+state+" "+zip);

                mSpot = new SpotPojo(street, city, state, zip, lat, lng, valGated, valShaded, valHandicap, mFireAuth.getCurrentUser().getUid());

                Log.i("GEOCOADDR", addresses.get(0).toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
