package lk.jiat.orterclothing.ui.profile;

public class UserAddress {

    private String addressName;
    private String addressLine1;
    private String addressLine2;
    private String owner;

    private String contact;

    public UserAddress( String addressName,String owner, String addressLine1, String addressLine2 , String contact) {
        this.addressName = addressName;
        this.owner = owner;
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

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

}
