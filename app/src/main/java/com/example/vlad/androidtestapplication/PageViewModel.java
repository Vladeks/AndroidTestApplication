package com.example.vlad.androidtestapplication;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

public class PageViewModel extends ViewModel {
    MutableLiveData<List<Integer>> pages;
}
