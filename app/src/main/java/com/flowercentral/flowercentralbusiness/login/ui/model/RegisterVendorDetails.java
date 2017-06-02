package com.flowercentral.flowercentralbusiness.login.ui.model;

import com.flowercentral.flowercentralbusiness.profile.model.ProfileDetails;

import java.util.ArrayList;

/**
 * Created by Supriya on 02-06-2017.
 */

public class RegisterVendorDetails {

    ProfileDetails profileDetails;
    ArrayList<FileDetails> uploadDocList = new ArrayList<>();
    ArrayList<FileDetails> uploadImageList = new ArrayList<>();

    public ProfileDetails getProfileDetails() {
        return profileDetails;
    }

    public void setProfileDetails(ProfileDetails profileDetails) {
        this.profileDetails = profileDetails;
    }

    public ArrayList<FileDetails> getUploadDocList() {
        return uploadDocList;
    }

    public void setUploadDocList(ArrayList<FileDetails> uploadDocList) {
        this.uploadDocList = uploadDocList;
    }

    public ArrayList<FileDetails> getUploadImageList() {
        return uploadImageList;
    }

    public void setUploadImageList(ArrayList<FileDetails> uploadImageList) {
        this.uploadImageList = uploadImageList;
    }

    public void addDocumentToList(FileDetails fileDetails) {
        uploadDocList.add(fileDetails);
    }

    public void addImageToList(FileDetails fileDetails) {
        uploadImageList.add(fileDetails);
    }
}
