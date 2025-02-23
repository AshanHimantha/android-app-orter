package lk.jiat.orterclothing.main_activity_ui.profile;

public class UserAddress {

    private String addressName;
    private String addressLine1;
    private String addressLine2;
    private String fname;

    private String lname;

    private String contact;

    public UserAddress( String addressName,String fname ,String lname, String addressLine1, String addressLine2 , String contact) {
        this.addressName = addressName;
        this.fname = fname;
        this.lname = lname;
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.contact = contact;
    }

    public String getAddressName() {
        return addressName;
    }

    public void setAddressName(String addressName) {
        this.addressName = addressName;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getFullName() {
        return fname + " " + lname;
    }

    public String getFName() {
        return fname;
    }

    public void setFName(String fname) {
        this.fname = fname;
    }

    public String getLName() {
        return lname;
    }

    public void setLName(String lname) {
        this.lname = lname;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

}
