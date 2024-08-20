package models;

public class UserModel {
    private String uid;
    private String name;
    private String email;
    private String password;
    private String gender;
    private String birthdate;
    private String verified="";
    private String profileImage="";

    // Default constructor is required for calls to DataSnapshot.getValue(User.class)
    public UserModel() {}

    public UserModel(String uid, String name, String email, String password, String gender, String birthdate,
                     String verified) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.password = password;
        this.gender = gender;
        this.birthdate = birthdate;
        this.verified = verified;
    }

    public UserModel(String uid, String name, String email, String password, String gender, String birthdate, String verified, String profileImage) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.password = password;
        this.gender = gender;
        this.birthdate = birthdate;
        this.verified = verified;
        this.profileImage = profileImage;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getVerified() {
        return verified;
    }

    public void setVerified(String verified) {
        this.verified = verified;
    }
}
