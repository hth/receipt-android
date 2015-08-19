package com.receiptofi.receiptapp.model;

/**
 * User: hitender
 * Date: 1/7/15 3:48 PM
 */
public class ProfileModel {
    private String firstName;
    private String lastName;
    private String mail;
    private String name;
    private String rid;
    private int profileBackgroundImage;

    public ProfileModel(String firstName, String mail, String rid) {
        this.firstName = firstName;
        this.mail = mail;
        this.rid = rid;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMail() {
        return mail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRid() {
        return rid;
    }

    public void setProfileBackgroundImage(int image) {
        this.profileBackgroundImage = image;
    }

    public int getProfileBackgroundImage() {
        return profileBackgroundImage;
    }

    @Override
    public String toString() {
        return "ProfileModel{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", mail='" + mail + '\'' +
                ", name='" + name + '\'' +
                ", rid='" + rid + '\'' +
                '}';
    }
}
