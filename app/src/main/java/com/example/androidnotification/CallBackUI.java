package com.example.androidnotification;

public interface CallBackUI {
    void onSuccess(String from, String value);
    void onError(String errorMessage);
}
