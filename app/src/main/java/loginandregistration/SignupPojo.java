package loginandregistration;

/**
 * Created by mahmudul on 3/17/17.
 */

public class SignupPojo {
    String fullname;
    String email;
    String userUid;

    public SignupPojo(){
        //default constructor
    }

    public SignupPojo(String fullname, String email, String userUid) {
        this.fullname = fullname;
        this.email = email;
        this.userUid = userUid;
    }

    public String getFullname() {
        return fullname;
    }

    public String getEmail() {
        return email;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }
}
