package listparkingspot;

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
    public String isGated, isShaded, isHandicap;

    public SpotPojo(){
        //obligatory default constructor
    }

    public SpotPojo(String street, String city, String state, String zip, String lat, String lng, String gated, String shaded, String handicap, String userUid) {
        this.street = street;
        this.city = city;
        this.state = state;
        this.zip = zip;
        this.lat = lat;
        this.lng = lng;
        this.isGated = gated;
        this.isShaded = shaded;
        this.isHandicap = handicap;
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

    public void setIsGated(String isGated) {
        this.isGated = isGated;
    }

    public void setIsShaded(String isShaded) {
        this.isShaded = isShaded;
    }

    public void setIsHandicap(String isHandicap) {
        this.isHandicap = isHandicap;
    }
}
