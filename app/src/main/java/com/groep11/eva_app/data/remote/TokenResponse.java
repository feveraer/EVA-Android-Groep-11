package com.groep11.eva_app.data.remote;

public class TokenResponse {
    private boolean success;
    private String message;
    private String token;
    private String userId;

    public TokenResponse() {}

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "TokenResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", token(size)='" + (token==null?"null":token.length()) + '\'' +
                '}';
    }
}
