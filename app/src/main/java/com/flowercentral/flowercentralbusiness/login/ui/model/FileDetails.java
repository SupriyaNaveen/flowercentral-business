package com.flowercentral.flowercentralbusiness.login.ui.model;

public class FileDetails {

    private int mID;
    private String mUri;
    private String mFilePath;
    private String mFileType;

    public int getID() {
        return mID;
    }

    public void setID(int id) {
        this.mID = id;
    }

    public String getFileType() {
        return mFileType;
    }

    public void setFileType(String fileType) {
        this.mFileType = fileType;
    }

    public String getmUri() {
        return mUri;
    }

    public void setUri(String uri) {
        this.mUri = uri;
    }

    public String getFilePath() {
        return mFilePath;
    }

    public void setFilePath(String filePath) {
        this.mFilePath = filePath;
    }
}
