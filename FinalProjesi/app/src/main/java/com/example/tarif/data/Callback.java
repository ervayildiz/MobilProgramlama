package com.example.tarif.data;

public interface Callback<T> {
    void onSuccess(T result);
    void onFailure(Exception e);
}
