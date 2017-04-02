package apputils;

/**
 * Created by mahmudul on 3/10/17.
 */

public class ConstUtil {
    public static final String SPREF_NAME = "dbappSharedPref";

    public static final String LIST_SPOT_SELECTED = "listSpotSelected";
    public static final String LOGIN_PAGE_SELECTED = "loginPageSelected";
    public static final String SIGNUP_PAGE_SELECTED = "signupPageSelected";

    public static final String VALUE_NO = "no";
    public static final String VALUE_YES = "yes";
    public static final String KEY_GATED = "gated";
    public static final String KEY_SHADED = "shaded";
    public static final String KEY_HANDICAP = "handicap";

    public static final String KEY_SUNDAY = "sunday";
    public static final String KEY_MONDAY = "monday";
    public static final String KEY_TUESDAY = "tuesday";
    public static final String KEY_WEDNESDAY = "wednesday";
    public static final String KEY_THURSDAY = "thursday";
    public static final String KEY_FRIDAY = "friday";
    public static final String KEY_SATURDAY = "saturday";

    public enum SpotListingFlag{
        SPOT_IMAGE,
        SPOT_LOCATION,
        DURATION
    }
}
