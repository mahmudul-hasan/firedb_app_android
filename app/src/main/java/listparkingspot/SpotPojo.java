package listparkingspot;

import java.util.HashMap;

import apputils.ConstUtil;

/**
 * Created by mahmudul on 3/17/17.
 */

public class SpotPojo {
    public String street;
    public String city;
    public String state;
    public String zip;
    public String lat, lng;
    public String userUid;
//    public String isGated, isShaded, isHandicap;
    public HashMap<String, String> amenities;
    public String mFromTimestamp, mToTimestamp;
    public HashMap<String, String> mDays;
    public String spotImageUrl;
    public String available;
    public String priceperhour;

    public SpotPojo(){
        //obligatory default constructor
        amenities = new HashMap<String, String>();
        mDays = new HashMap<String, String>();
        this.available = ConstUtil.VALUE_NO;
        this.priceperhour = "0";
    }

    public SpotPojo(String street, String city, String state, String zip, String lat, String lng, String userUid) {
        this.street = street;
        this.city = city;
        this.state = state;
        this.zip = zip;
        this.lat = lat;
        this.lng = lng;
//        this.isGated = gated;
//        this.isShaded = shaded;
//        this.isHandicap = handicap;
        this.userUid = userUid;
    }

    public String getStreet() {
        return street;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getZip() {
        return zip;
    }

    public String getUserUid() {
        return userUid;
    }

    public String getLat() {
        return lat;
    }

    public String getLng() {
        return lng;
    }

//    public String getIsGated() {
//        return isGated;
//    }
//
//    public String getIsShaded() {
//        return isShaded;
//    }
//
//    public String getIsHandicap() {
//        return isHandicap;
//    }


    public HashMap<String, String> getAmenities() {
        return amenities;
    }

    public String getmFromTimestamp() {
        return mFromTimestamp;
    }

    public String getmToTimestamp() {
        return mToTimestamp;
    }

    public HashMap<String, String> getmDays() {
        return mDays;
    }

    public String getSpotImageUrl() {
        return spotImageUrl;
    }

    public String getAvailable() {
        return available;
    }

    public String getPriceperhour() {
        return priceperhour;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

//    public void setIsGated(String isGated) {
//        this.isGated = isGated;
//    }
//
//    public void setIsShaded(String isShaded) {
//        this.isShaded = isShaded;
//    }
//
//    public void setIsHandicap(String isHandicap) {
//        this.isHandicap = isHandicap;
//    }


    public void setAmenities(HashMap<String, String> amenities) {
        this.amenities = amenities;
    }

    public void setmFromTimestamp(String mFromTimestamp) {
        this.mFromTimestamp = mFromTimestamp;
    }

    public void setmToTimestamp(String mToTimestamp) {
        this.mToTimestamp = mToTimestamp;
    }

    public void setmDays(HashMap<String, String> mDays) {
        this.mDays = mDays;
    }

    public void setSpotImageUrl(String spotImageUrl) {
        this.spotImageUrl = spotImageUrl;
    }

    public void setAvailable(String available) {
        this.available = available;
    }

    public void setPriceperhour(String priceperhour) {
        this.priceperhour = priceperhour;
    }
}
