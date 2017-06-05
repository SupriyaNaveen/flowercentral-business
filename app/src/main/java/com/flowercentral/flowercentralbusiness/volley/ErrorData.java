package com.flowercentral.flowercentralbusiness.volley;

public class ErrorData {

    private int errorCode;
    private String errorMessage;
    private long networkTimems;
    private String errorCodeOfResponseData;
    private ERROR_TYPE errorType = ERROR_TYPE.APPLICATION_ERROR;

    public enum ERROR_TYPE {NETWORK_NOT_AVAILABLE, INTERNAL_SERVER_ERROR, CONNECTION_TIMEOUT, APPLICATION_ERROR, UNAUTHORIZED_ERROR, AUTHENTICATION_ERROR, INVALID_INPUT_SUPPLIED, SERVER_ERROR}

    @Override
    public String toString() {
        return "ErrorData{" +
                "errorCode=" + errorCode +
                ", errorMessage='" + errorMessage + '\'' +
                ", networkTimems=" + networkTimems +
                ", errorCodeOfResponseData='" + errorCodeOfResponseData + '\'' +
                ", errorType=" + errorType +
                '}';
    }

    public ErrorData() {

    }

    public ErrorData(String errorMessageage) {
        this.errorMessage = errorMessageage;
    }

    public ErrorData(int errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public void setErrorCodeOfResponseData(String errorCodeOfResponseData) {
        this.errorCodeOfResponseData = errorCodeOfResponseData;
    }

    public ERROR_TYPE getErrorType() {
        return errorType;
    }

    public void setErrorType(ERROR_TYPE errorType) {
        this.errorType = errorType;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public void setNetworkTimems(long networkTimems) {
        this.networkTimems = networkTimems;
    }
}
