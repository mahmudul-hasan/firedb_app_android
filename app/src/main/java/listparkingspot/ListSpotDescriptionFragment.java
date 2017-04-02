package listparkingspot;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
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
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mhstudio.dbapp.BaseActivity;
import com.mhstudio.dbapp.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import apputils.ConstUtil;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

@SuppressWarnings("WrongConstant")
public class ListSpotDescriptionFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    private BaseActivity mActivity;

    private GoogleApiClient mGAPIClient;

    private TextView mSpotAddressET, mSpotPrice;
    private ImageView mIconAddress, mSpotImage;
    private Button mSubmit, mTakePic;
    private AppCompatCheckBox mCheckGated, mCheckShaded, mCheckHandicap;
    private TextView mFromTime, mToTime, mListingDate;
    private View mToDash;

    private String valGated=ConstUtil.VALUE_NO, valShaded=ConstUtil.VALUE_NO, valHandicap=ConstUtil.VALUE_NO;
    private HashMap<String, String> mAmenitiesMap;

    private Geocoder gcoder;
    private FirebaseAuth mFireAuth;
    private DatabaseReference mDBRef;
    private StorageReference mStorageRef;

    private AppCompatCheckBox mCheckSun, mCheckMon, mCheckTue, mCheckWed, mCheckThu, mCheckFri, mCheckSat;
    private HashMap<String, String> mDaysMap;

    int mYear=0, mMonth=0, mDay=0, mFromHour=0, mFromMin=0, mToHour=0, mToMin=0;
    long mFromTimeMillis, mToTimeMillis;

    private SpotPojo mSpot;

    private boolean[] allSet;

    private static final int REQUEST_IMAGE_CAPTURE = 1;

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

        allSet = new boolean[ConstUtil.SpotListingFlag.values().length];

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
        mAmenitiesMap = new HashMap<String, String>();
        mAmenitiesMap.put(ConstUtil.KEY_GATED, valGated);
        mAmenitiesMap.put(ConstUtil.KEY_SHADED, valShaded);
        mAmenitiesMap.put(ConstUtil.KEY_HANDICAP, valHandicap);
        mDaysMap = new HashMap<String, String>();
        mDaysMap.put(ConstUtil.KEY_SUNDAY, ConstUtil.VALUE_NO);
        mDaysMap.put(ConstUtil.KEY_MONDAY, ConstUtil.VALUE_NO);
        mDaysMap.put(ConstUtil.KEY_TUESDAY, ConstUtil.VALUE_NO);
        mDaysMap.put(ConstUtil.KEY_WEDNESDAY, ConstUtil.VALUE_NO);
        mDaysMap.put(ConstUtil.KEY_THURSDAY, ConstUtil.VALUE_NO);
        mDaysMap.put(ConstUtil.KEY_FRIDAY, ConstUtil.VALUE_NO);
        mDaysMap.put(ConstUtil.KEY_SATURDAY, ConstUtil.VALUE_NO);
        mStorageRef = FirebaseStorage.getInstance().getReference();

        mSpot = new SpotPojo();
        mSpot.setUserUid(mFireAuth.getCurrentUser().getUid());

        mSpotImage = (ImageView) view.findViewById(R.id.img_spot);
        mSpotPrice = (TextView) view.findViewById(R.id.spot_price);

        mSpotAddressET = (TextView) view.findViewById(R.id.tv_spot_address);

        mCheckGated = (AppCompatCheckBox) view.findViewById(R.id.check_spot_gated);
        mCheckShaded = (AppCompatCheckBox) view.findViewById(R.id.check_spot_shaded);
        mCheckHandicap = (AppCompatCheckBox) view.findViewById(R.id.check_spot_handicap);

        mFromTime = (TextView) view.findViewById(R.id.tv_spot_fromtime);
        mToTime = (TextView) view.findViewById(R.id.tv_spot_totime);
        mToTime.setVisibility(View.GONE);
//        mListingDate = (TextView) view.findViewById(R.id.tv_spot_date);
        mToDash = (View) view.findViewById(R.id.to_dash);
        mToDash.setVisibility(View.GONE);

        mTakePic = (Button) view.findViewById(R.id.btn_spot_takepic);
        mTakePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                cameraIntent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                if(cameraIntent.resolveActivity(mActivity.getPackageManager()) != null){
                    startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });

        mCheckSun = (AppCompatCheckBox) view.findViewById(R.id.check_sunday);
        mCheckMon = (AppCompatCheckBox) view.findViewById(R.id.check_monday);
        mCheckTue = (AppCompatCheckBox) view.findViewById(R.id.check_tuesday);
        mCheckWed = (AppCompatCheckBox) view.findViewById(R.id.check_wednesday);
        mCheckThu = (AppCompatCheckBox) view.findViewById(R.id.check_thursday);
        mCheckFri = (AppCompatCheckBox) view.findViewById(R.id.check_friday);
        mCheckSat = (AppCompatCheckBox) view.findViewById(R.id.check_saturday);

        mSubmit = (Button) view.findViewById(R.id.btn_spot_submit);

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

                            allSet[ConstUtil.SpotListingFlag.SPOT_LOCATION.ordinal()] = true;
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

        mSpotPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater infl = LayoutInflater.from(mActivity);
                final View leInflater = infl.inflate(R.layout.dialog_pricepicker, null);
                final NumberPicker dollarPicker = (NumberPicker) leInflater.findViewById(R.id.dollar_picker);
                dollarPicker.setMinValue(3);
                dollarPicker.setMaxValue(5);
                final NumberPicker centPicker = (NumberPicker) leInflater.findViewById(R.id.cent_picker);
                centPicker.setMinValue(0);
                centPicker.setMaxValue(99);
                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                builder.setView(leInflater);
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String centPicked = centPicker.getValue() < 10 ? "0"+centPicker.getValue() : ""+centPicker.getValue();
                        mSpotPrice.setText("$"+dollarPicker.getValue()+"."+centPicked+"/hour");
                        int priceperhour = dollarPicker.getValue()*100 + centPicker.getValue();
                        mSpot.setPriceperhour(String.valueOf(priceperhour));
                        dialog.dismiss();
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
                mAmenitiesMap.put(ConstUtil.KEY_GATED, valGated);
//                mSpot.setIsGated(valGated);
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
                mAmenitiesMap.put(ConstUtil.KEY_SHADED, valShaded);
//                mSpot.setIsShaded(valShaded);
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
                mAmenitiesMap.put(ConstUtil.KEY_HANDICAP, valHandicap);
//                mSpot.setIsHandicap(valHandicap);
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
                allSet[ConstUtil.SpotListingFlag.DURATION.ordinal()] = true;
            }
        });
//        mListingDate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                pickTime(mListingDate);
//            }
//        });

        mCheckSun.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mDaysMap.put(ConstUtil.KEY_SUNDAY, ConstUtil.VALUE_YES);
                }else {
                    mDaysMap.put(ConstUtil.KEY_SUNDAY, ConstUtil.VALUE_NO);
                }
                Log.i("OHMYDAYZ", mDaysMap.size()+"");
            }
        });
        mCheckMon.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mDaysMap.put(ConstUtil.KEY_MONDAY, ConstUtil.VALUE_YES);
                }else {
                    mDaysMap.put(ConstUtil.KEY_MONDAY, ConstUtil.VALUE_NO);
                }
            }
        });
        mCheckTue.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mDaysMap.put(ConstUtil.KEY_TUESDAY, ConstUtil.VALUE_YES);
                }else {
                    mDaysMap.put(ConstUtil.KEY_TUESDAY, ConstUtil.VALUE_NO);
                }
            }
        });
        mCheckWed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mDaysMap.put(ConstUtil.KEY_WEDNESDAY, ConstUtil.VALUE_YES);
                }else {
                    mDaysMap.put(ConstUtil.KEY_WEDNESDAY, ConstUtil.VALUE_NO);
                }
            }
        });
        mCheckThu.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mDaysMap.put(ConstUtil.KEY_THURSDAY, ConstUtil.VALUE_YES);
                }else {
                    mDaysMap.put(ConstUtil.KEY_THURSDAY, ConstUtil.VALUE_NO);
                }
            }
        });
        mCheckFri.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mDaysMap.put(ConstUtil.KEY_FRIDAY, ConstUtil.VALUE_YES);
                }else {
                    mDaysMap.put(ConstUtil.KEY_FRIDAY, ConstUtil.VALUE_NO);
                }
            }
        });
        mCheckSat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mDaysMap.put(ConstUtil.KEY_SATURDAY, ConstUtil.VALUE_YES);
                }else {
                    mDaysMap.put(ConstUtil.KEY_SATURDAY, ConstUtil.VALUE_NO);
                }
            }
        });

        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for(int i=0; i<allSet.length; i++){
                    if(!allSet[i]) {
                        Toast.makeText(mActivity, "Image, location and duration are required", Toast.LENGTH_LONG).show();
                        return;
                    }
                }

                int noCnt = 0;
                for(String key : mDaysMap.keySet()){
                    if(mDaysMap.get(key).equals(ConstUtil.VALUE_NO)) noCnt++;
                }
                if(noCnt == mDaysMap.keySet().size()) {
                    Toast.makeText(mActivity, "Please select days", Toast.LENGTH_LONG).show();
                    return;
                }

                FirebaseUser leUser = mFireAuth.getCurrentUser();
                SharedPreferences spref = mActivity.getSharedPreferences(ConstUtil.SPREF_NAME, Context.MODE_PRIVATE);
                if(leUser != null){
                    String pushedUid = spref.getString(leUser.getUid(), null);
                    mSpot.setAmenities(mAmenitiesMap);
                    mSpot.setmDays(mDaysMap);
                    mSpot.setAvailable(ConstUtil.VALUE_YES);

                    //TEST

                    //TEST ends

                    final ProgressDialog pd = new ProgressDialog(mActivity);
                    pd.setMessage("Saving ...");
                    pd.show();
                    DatabaseReference pushedRef;
                    if(pushedUid == null) {
                        pushedRef = mDBRef.child("spotlistings").push();
//                        pushedRef.setValue(mSpot);
                        spref.edit().putString(leUser.getUid(), pushedRef.getKey()).apply();
                    }else{
                        pushedRef = mDBRef.child("spotlistings").child(pushedUid);
//                        pushedRef.setValue(mSpot);
                    }
                    pushedRef.setValue(mSpot).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            pd.setMessage("Save successful");
                            pd.dismiss();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.setMessage("Save failed.");
                            pd.dismiss();
                        }
                    });
                }
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

        c.set(year, month, day, hour, minute, 0);
        /**long currentDateInMillis = c.getTimeInMillis();
        Log.i("CURRENTTIME", "in millis "+currentDateInMillis);*/

//        if(textView.getId() == R.id.tv_spot_date){
//            DatePickerDialog dpDialog = new DatePickerDialog(mActivity, new DatePickerDialog.OnDateSetListener() {
//                @Override
//                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//                    String month = "";
//                    String day = "";
//                    switch (monthOfYear){
//                        case Calendar.JANUARY:
//                            month = "January";
//                            break;
//                        case Calendar.FEBRUARY:
//                            month = "February";
//                            break;
//                        case Calendar.MARCH:
//                            month = "March";
//                            break;
//                        case Calendar.APRIL:
//                            month = "April";
//                            break;
//                        case Calendar.MAY:
//                            month = "May";
//                            break;
//                        case Calendar.JUNE:
//                            month = "June";
//                            break;
//                        case Calendar.JULY:
//                            month = "July";
//                            break;
//                        case Calendar.AUGUST:
//                            month = "August";
//                            break;
//                        case Calendar.SEPTEMBER:
//                            month = "September";
//                            break;
//                        case Calendar.OCTOBER:
//                            month = "October";
//                            break;
//                        case Calendar.NOVEMBER:
//                            month = "November";
//                            break;
//                        case Calendar.DECEMBER:
//                            month = "December";
//                            break;
//                    }
//                    Calendar pickedDate = Calendar.getInstance();
//                    Calendar curr = Calendar.getInstance();
//                    pickedDate.set(Calendar.YEAR, year);
//                    pickedDate.set(Calendar.MONTH, monthOfYear);
//                    pickedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
//                    if(pickedDate.getTimeInMillis() < curr.getTimeInMillis()){
//                        Toast.makeText(mActivity, "You cannot go past date ...at least not now", Toast.LENGTH_LONG).show();
//                        return;
//                    }
//                    mYear = year;
//                    mMonth = monthOfYear;
//                    mDay = dayOfMonth;
//
//                    textView.setText(month + " " + dayOfMonth + ", " + year);
//
//                    //TEST
//                    Calendar calFrom = Calendar.getInstance();
//                    Calendar calTo = Calendar.getInstance();
//                    calFrom.set(mYear, mMonth, mDay, mFromHour, mFromMin, 0);
//                    calTo.set(mYear, mMonth, mDay, mToHour, mToMin, 0);
//                    mFromTimeMillis = calFrom.getTimeInMillis();
//                    mToTimeMillis = calTo.getTimeInMillis();
//                    Log.i("CURRENTTIME", "from millis "+mFromTimeMillis+ " to millis "+mToTimeMillis);
//                    fromMillisToTime(calTo.getTimeInMillis()-calFrom.getTimeInMillis());
//                    //TEST ends
//                }
//            }, year, month, day);
//            dpDialog.show();
//        }else {
//            TimePickerDialog tpDialog = new TimePickerDialog(mActivity, new TimePickerDialog.OnTimeSetListener() {
//                @Override
//                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//
//                    int hour = hourOfDay;
//                    String timeSet = "";
//                    if (hour > 12) {
//                        hour -= 12;
//                        timeSet = "PM";
//                    } else if (hour == 0) {
//                        hour += 12;
//                        timeSet = "AM";
//                    } else if (hour == 12) {
//                        timeSet = "PM";
//                    } else {
//                        timeSet = "AM";
//                    }
//
//                    if(textView.getId() == R.id.tv_spot_fromtime) {
//                        Calendar pickedTime = Calendar.getInstance();
//                        Calendar curr = Calendar.getInstance();
//                        pickedTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
//                        pickedTime.set(Calendar.MINUTE, minute);
//                        if(pickedTime.getTimeInMillis() < curr.getTimeInMillis()){
//                            Toast.makeText(mActivity, "You cannot go past ...at least not now", Toast.LENGTH_LONG).show();
//                            return;
//                        }
//
//                        mFromHour = hourOfDay;
//                        mFromMin = minute;
//                        mToTime.setVisibility(View.VISIBLE);
//                    }else{
//                        Calendar pickedTime = Calendar.getInstance();
//                        Calendar fromTime = Calendar.getInstance();
//                        pickedTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
//                        pickedTime.set(Calendar.MINUTE, minute);
//                        fromTime.set(Calendar.HOUR_OF_DAY, mFromHour);
//                        fromTime.set(Calendar.MINUTE, mFromMin);
//                        if(pickedTime.getTimeInMillis() < fromTime.getTimeInMillis()){
//                            Toast.makeText(mActivity, "You cannot go past of from time", Toast.LENGTH_LONG).show();
//                            return;
//                        }
//
//                        mToHour = hourOfDay;
//                        mToMin = minute;
//                    }
//
//                    textView.setText(String.format("%02d:%02d", hour, minute) + timeSet);
//                }
//            }, hour, minute, false);
//            tpDialog.show();
//        }

        TimePickerDialog tpDialog = new TimePickerDialog(mActivity, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {



                if(textView.getId() == R.id.tv_spot_fromtime) {
                    Calendar pickedTime = Calendar.getInstance();
//                    Calendar curr = Calendar.getInstance();
                    pickedTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    pickedTime.set(Calendar.MINUTE, minute);
//                    if(pickedTime.getTimeInMillis() < curr.getTimeInMillis()){
//                        Toast.makeText(mActivity, "You cannot go past ...at least not now", Toast.LENGTH_LONG).show();
//                        return;
//                    }

                    mFromHour = hourOfDay;
                    mFromMin = minute;
                    mFromTimeMillis = pickedTime.getTimeInMillis();
                    mToTime.setVisibility(View.VISIBLE);
                    mToDash.setVisibility(View.VISIBLE);

                    mSpot.setmFromTimestamp(String.valueOf(mFromTimeMillis));
                }else{
                    Calendar pickedTime = Calendar.getInstance();
                    Calendar fromTime = Calendar.getInstance();
                    pickedTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    pickedTime.set(Calendar.MINUTE, minute);
                    fromTime.set(Calendar.HOUR_OF_DAY, mFromHour);
                    fromTime.set(Calendar.MINUTE, mFromMin);
                    if(pickedTime.getTimeInMillis() < fromTime.getTimeInMillis()){
                        Toast.makeText(mActivity, "You cannot go past of from time", Toast.LENGTH_LONG).show();
                        return;
                    }

                    mToHour = hourOfDay;
                    mToMin = minute;
                    mToTimeMillis = pickedTime.getTimeInMillis();

                    mSpot.setmToTimestamp(String.valueOf(mToTimeMillis));
                }

                convertAmPmToView(hourOfDay, minute, textView);
            }
        }, hour, minute, false);
        tpDialog.show();
    }

    private void convertAmPmToView(int hourOfDay, int minute, TextView textView) {
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

        final FirebaseUser leUser = mFireAuth.getCurrentUser();
        final String spotUid = mActivity.getSharedPreferences(ConstUtil.SPREF_NAME, MODE_PRIVATE).getString(leUser.getUid(), null);
        if(spotUid != null) {
            DatabaseReference leDBRef = mDBRef.child("spotlistings").child(spotUid);
            leDBRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Log.i("LISTINGID", "" + spotUid);
                        if(((String)(dataSnapshot.child("userUid").getValue())).equals(leUser.getUid())){
                            Log.i("LISTINGID", "" + leUser.getUid());
                            //TODO - populate with the existing data
                            SpotPojo retrievedSpot = dataSnapshot.getValue(SpotPojo.class);

                            Picasso.with(mActivity).load(retrievedSpot.getSpotImageUrl()).into(mSpotImage);

                            int cents = Integer.parseInt(retrievedSpot.getPriceperhour())%100;
                            String strCents = cents < 10 ? "0"+cents : ""+cents;
                            mSpotPrice.setText("$"+(Integer.parseInt(retrievedSpot.getPriceperhour())/100) + "." + strCents + "/hour");
                            mSpotAddressET.setText(retrievedSpot.getStreet()/**+", \n"+retrievedSpot.getCity()+", \n"+retrievedSpot.getState()*/);
                            mCheckGated.setChecked(retrievedSpot.getAmenities().get(ConstUtil.KEY_GATED).equals(ConstUtil.VALUE_YES) ? true : false);
                            mCheckShaded.setChecked(retrievedSpot.getAmenities().get(ConstUtil.KEY_SHADED).equals(ConstUtil.VALUE_YES) ? true : false);
                            mCheckHandicap.setChecked(retrievedSpot.getAmenities().get(ConstUtil.KEY_HANDICAP).equals(ConstUtil.VALUE_YES) ? true : false);

                            Log.i("RETRIEVEDDATA", "" + retrievedSpot.getmFromTimestamp());
                            mToTime.setVisibility(View.VISIBLE);
                            mToDash.setVisibility(View.VISIBLE);
                            fromMillisToTime(Long.parseLong(retrievedSpot.getmFromTimestamp()), mFromTime);
                            fromMillisToTime(Long.parseLong(retrievedSpot.getmToTimestamp()), mToTime);

                            mCheckSun.setChecked(retrievedSpot.getmDays().get(ConstUtil.KEY_SUNDAY).equals(ConstUtil.VALUE_YES) ? true : false);
                            mCheckMon.setChecked(retrievedSpot.getmDays().get(ConstUtil.KEY_MONDAY).equals(ConstUtil.VALUE_YES) ? true : false);
                            mCheckTue.setChecked(retrievedSpot.getmDays().get(ConstUtil.KEY_TUESDAY).equals(ConstUtil.VALUE_YES) ? true : false);
                            mCheckWed.setChecked(retrievedSpot.getmDays().get(ConstUtil.KEY_WEDNESDAY).equals(ConstUtil.VALUE_YES) ? true : false);
                            mCheckThu.setChecked(retrievedSpot.getmDays().get(ConstUtil.KEY_THURSDAY).equals(ConstUtil.VALUE_YES) ? true : false);
                            mCheckFri.setChecked(retrievedSpot.getmDays().get(ConstUtil.KEY_FRIDAY).equals(ConstUtil.VALUE_YES) ? true : false);
                            mCheckSat.setChecked(retrievedSpot.getmDays().get(ConstUtil.KEY_SATURDAY).equals(ConstUtil.VALUE_YES) ? true : false);

                            for(int i=0; i<allSet.length; i++){
                                allSet[i] = true;
                            }

                            mSpot = retrievedSpot;
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        //TEST query by a value to get list of listings
        DatabaseReference leDBRef = mDBRef.child("spotlistings");
        leDBRef.orderByChild("city").equalTo("Germantown").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i("DSS", dataSnapshot.getChildrenCount()+"");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //TEST ends
    }

    @Override
    public void onDestroy() {
        Log.i("FRAGDESTROYED", "ListSpotDescriptionFragment");
        super.onDestroy();
    }

    private void fromMillisToTime(long milliseconds, TextView tv){
//        int seconds = (int) (milliseconds / 1000) % 60 ;
//        int minutes = (int) TimeUnit.MILLISECONDS.toMinutes(milliseconds) % 60; //((milliseconds / (1000*60)) % 60);
//        int hours   = (int) TimeUnit.MILLISECONDS.toHours(milliseconds) % 24; //(int) ((milliseconds / (1000*60*60)) % 24);

        Date date = new Date(milliseconds);
        DateFormat df = new SimpleDateFormat("hh:mm a");
        df.setTimeZone(TimeZone.getDefault());
        String time = df.format(date);

        if(tv != null) {
//            convertAmPmToView(hours, minutes, tv);
//            tv.setText(hours+":"+minutes);
            tv.setText(time);
        }

//        Log.i("CURRENTTIME", ""+hours+":"+minutes);
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

                mSpot = new SpotPojo(street, city, state, zip, lat, lng, mFireAuth.getCurrentUser().getUid());

                Log.i("GEOCOADDR", addresses.get(0).toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            Log.i("PICCAPTURED", data.getData().toString());
            String[] projection = {MediaStore.Images.Media.DATA};
            final Cursor cursor = mActivity.getContentResolver().query(data.getData(), projection, null, null, null);
            final int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            Log.i("PICCAPTURED", cursor.getString(columnIndex));

            final ProgressDialog pdialog = new ProgressDialog(mActivity);
            pdialog.setMessage("Uploading image ...");
            pdialog.show();
            final StorageReference filepath = mStorageRef.child("spotpics").child(mFireAuth.getCurrentUser().getUid()).child("spotimg");
            filepath.putFile(data.getData()).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()){
                        pdialog.dismiss();
                        filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Log.i("PICCAPTURED", uri.toString());
                                mSpot.setSpotImageUrl(uri.toString());

                                File imageFile = new File(cursor.getString(columnIndex));
                                if(imageFile.exists()){
                                    Bitmap leBmp = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                                    mSpotImage.setImageBitmap(leBmp);
                                    allSet[ConstUtil.SpotListingFlag.SPOT_IMAGE.ordinal()] = true;
                                }
                            }
                        });
                    }else{
                        pdialog.dismiss();
                    }
                }
            });
        }
    }
}
