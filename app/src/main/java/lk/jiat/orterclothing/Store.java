package lk.jiat.orterclothing;

    public class Store {
        private String storeName;

        private String storeContact;
        private String storeImage;
        private String address1;
        private String address2;
        private String zipCode;
        private String latitude;
        private String longitude;

        public Store(String storeName,  String storeContact, String storeImage,
                     String address1, String address2, String zipCode, String latitude, String longitude) {
            this.storeName = storeName;

            this.storeContact = storeContact;
            this.storeImage = storeImage;
            this.address1 = address1;
            this.address2 = address2;
            this.zipCode = zipCode;
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public String getStoreName() {
            return storeName;
        }

        public void setStoreName(String storeName) {
            this.storeName = storeName;
        }


        public String getStoreContact() {
            return storeContact;
        }

        public void setStoreContact(String storeContact) {
            this.storeContact = storeContact;
        }

        public String getStoreImage() {
            return storeImage;
        }

        public void setStoreImage(String storeImage) {
            this.storeImage = storeImage;
        }

        public String getAddress1() {
            return address1;
        }

        public void setAddress1(String address1) {
            this.address1 = address1;
        }

        public String getAddress2() {
            return address2;
        }

        public void setAddress2(String address2) {
            this.address2 = address2;
        }

        public String getZipCode() {
            return zipCode;
        }

        public void setZipCode(String zipCode) {
            this.zipCode = zipCode;
        }

        public String getLatitude() {
            return latitude;
        }

        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }

        public String getLongitude() {
            return longitude;
        }

        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }
    }