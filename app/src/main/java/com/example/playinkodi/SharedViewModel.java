package com.example.playinkodi;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<String> urlLiveData = new MutableLiveData<>();

    public LiveData<String> getUrl() {
        return urlLiveData;
    }

    public void setUrl(String url) {
        urlLiveData.setValue(url);
    }
}